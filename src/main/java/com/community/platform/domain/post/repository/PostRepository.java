package com.community.platform.domain.post.repository;

import com.community.platform.domain.post.entity.Post;
import com.community.platform.domain.post.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 타입별 목록 (삭제 제외, 공지 상단 고정)
    Page<Post> findByTypeAndIsDeletedFalseOrderByIsPinnedDescCreatedAtDesc(
            PostType type, Pageable pageable);

    // 전체 목록 (타입 무관)
    Page<Post> findByIsDeletedFalseOrderByIsPinnedDescCreatedAtDesc(
            Pageable pageable);

    // 상세 조회 (삭제 제외)
    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    // 키워드 검색
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false " +
            "AND (p.title LIKE %:kw% OR p.content LIKE %:kw%)")
    Page<Post> searchByKeyword(@Param("kw") String keyword,
                               Pageable pageable);

    // 조회수 증가 (직접 쿼리)
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 " +
            "WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}