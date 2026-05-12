package com.community.platform.domain.post.repository;

import com.community.platform.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    // 게시글의 최상위 댓글 목록 (대댓글 제외)
    List<Comment> findByPostIdAndParentIsNullAndIsDeletedFalse(
            Long postId);

    // 대댓글 목록
    List<Comment> findByParentIdAndIsDeletedFalse(Long parentId);
}