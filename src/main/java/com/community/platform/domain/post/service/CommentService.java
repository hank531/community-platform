package com.community.platform.domain.post.service;

import com.community.platform.domain.post.dto.*;
import com.community.platform.domain.post.entity.*;
import com.community.platform.domain.post.repository.*;
import com.community.platform.domain.user.entity.User;
import com.community.platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository  commentRepository;
    private final PostRepository     postRepository;
    private final UserRepository     userRepository;
    private final LikeRepository     likeRepository;

    // ── 댓글 목록 조회 (대댓글 포함) ──────
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        List<Comment> roots = commentRepository
                .findByPostIdAndParentIsNullAndIsDeletedFalse(postId);

        return roots.stream().map(root -> {
            List<CommentResponse> children = commentRepository
                    .findByParentIdAndIsDeletedFalse(root.getId())
                    .stream()
                    .map(c -> CommentResponse.from(c, List.of()))
                    .collect(Collectors.toList());
            return CommentResponse.from(root, children);
        }).collect(Collectors.toList());
    }

    // ── 댓글 작성 ──────────────────────────
    @Transactional
    public CommentResponse createComment(Long postId,
                                         CommentRequest req, Long userId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
        }

        Comment comment = Comment.create(post, user, parent,
                req.getContent());
        commentRepository.save(comment);
        return CommentResponse.from(comment, List.of());
    }

    // ── 댓글 삭제 ──────────────────────────
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인 댓글만 삭제할 수 있습니다.");
        }
        comment.delete();
    }

    // ── 좋아요 토글 ────────────────────────
    @Transactional
    public boolean toggleLike(Long targetId, String targetType,
                              Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return likeRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId, targetType, targetId)
                .map(like -> {
                    likeRepository.delete(like);
                    return false; // 좋아요 취소
                })
                .orElseGet(() -> {
                    likeRepository.save(
                            Like.create(user, targetType, targetId));
                    return true;  // 좋아요 추가
                });
    }
}