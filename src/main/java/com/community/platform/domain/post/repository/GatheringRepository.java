package com.community.platform.domain.post.repository;

import com.community.platform.domain.post.entity.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GatheringRepository
        extends JpaRepository<Gathering, Long> {

    Optional<Gathering> findByPostId(Long postId);
}