package com.community.platform.domain.post.service;

import com.community.platform.domain.post.dto.*;
import com.community.platform.domain.post.entity.*;
import com.community.platform.domain.post.repository.*;
import com.community.platform.domain.user.entity.User;
import com.community.platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final GatheringRepository gatheringRepository;
    private final UserRepository userRepository;

    // ── 목록 조회 ──────────────────────────
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(PostType type, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Post> posts = (type != null)
                ? postRepository
                  .findByTypeAndIsDeletedFalseOrderByIsPinnedDescCreatedAtDesc(
                          type, pageable)
                : postRepository
                  .findByIsDeletedFalseOrderByIsPinnedDescCreatedAtDesc(
                          pageable);
        return posts.map(PostResponse::from);
    }

    // ── 상세 조회 ──────────────────────────
    @Transactional
    public PostResponse getPost(Long postId) {
        Post post = findPost(postId);
        postRepository.incrementViewCount(postId);
        return PostResponse.from(post);
    }

    // ── 게시글 작성 ────────────────────────
    @Transactional
    public PostResponse createPost(PostCreateRequest req, Long userId) {
        User user = findUser(userId);
        Post post = Post.create(user, req.getType(),
                req.getTitle(), req.getContent());
        postRepository.save(post);

        // 구인글이면 Gathering 자동 생성
        if (req.getType() == PostType.GATHERING) {
            Gathering gathering = Gathering.create(
                    post, user,
                    req.getMaxMembers(),
                    req.getMeetingDate(),
                    req.getMeetingTime(),
                    req.getLocation()
            );
            gatheringRepository.save(gathering);
        }
        return PostResponse.from(post);
    }

    // ── 게시글 수정 ────────────────────────
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest req,
                                   Long userId) {
        Post post = findPost(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 게시글만 수정할 수 있습니다.");
        }
        post.update(req.getTitle(), req.getContent());
        return PostResponse.from(post);
    }

    // ── 게시글 삭제 ────────────────────────
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findPost(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 게시글만 삭제할 수 있습니다.");
        }
        post.delete();
    }

    // ── 키워드 검색 ────────────────────────
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        return postRepository
                .searchByKeyword(keyword, pageable)
                .map(PostResponse::from);
    }

    // ── 내부 헬퍼 ──────────────────────────
    private Post findPost(Long id) {
        return postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("게시글을 찾을 수 없습니다."));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }
}