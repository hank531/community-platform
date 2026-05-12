package com.community.platform.domain.post.repository;

import com.community.platform.domain.post.entity.GatheringMember;
import com.community.platform.domain.post.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GatheringMemberRepository
        extends JpaRepository<GatheringMember, Long> {

    boolean existsByGatheringIdAndUserId(
            Long gatheringId, Long userId);

    List<GatheringMember> findByGatheringId(Long gatheringId);

    Optional<GatheringMember> findByGatheringIdAndUserId(
            Long gatheringId, Long userId);
}