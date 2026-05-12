package com.community.platform.domain.post.repository;

import com.community.platform.domain.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndTargetTypeAndTargetId(
            Long userId, String targetType, Long targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(
            Long userId, String targetType, Long targetId);

    long countByTargetTypeAndTargetId(
            String targetType, Long targetId);
}