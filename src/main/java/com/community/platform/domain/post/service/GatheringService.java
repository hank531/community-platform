package com.community.platform.domain.post.service;

import com.community.platform.domain.post.entity.*;
import com.community.platform.domain.post.repository.*;
import com.community.platform.domain.user.entity.User;
import com.community.platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository       gatheringRepository;
    private final GatheringMemberRepository memberRepository;
    private final UserRepository            userRepository;

    // ── 모임 참여 신청 ──────────────────────
    @Transactional
    public void joinGathering(Long postId, Long userId) {
        Gathering gathering = gatheringRepository.findByPostId(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("모임을 찾을 수 없습니다."));

        if (gathering.getStatus() != GatheringStatus.RECRUITING) {
            throw new IllegalArgumentException("모집이 마감된 모임입니다.");
        }
        if (memberRepository.existsByGatheringIdAndUserId(
                gathering.getId(), userId)) {
            throw new IllegalArgumentException("이미 신청한 모임입니다.");
        }
        if (gathering.getHostUser().getId().equals(userId)) {
            throw new IllegalArgumentException("호스트는 신청할 수 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        memberRepository.save(GatheringMember.create(gathering, user));
    }

    // ── 신청 승인 ──────────────────────────
    @Transactional
    public void approveMember(Long memberId, Long hostId) {
        GatheringMember member = findMember(memberId);
        verifyHost(member.getGathering(), hostId);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 신청만 승인할 수 있습니다.");
        }

        member.approve();
        member.getGathering().addMember();
    }

    // ── 신청 거절 ──────────────────────────
    @Transactional
    public void rejectMember(Long memberId, Long hostId) {
        GatheringMember member = findMember(memberId);
        verifyHost(member.getGathering(), hostId);
        member.reject();
    }

    // ── 내부 헬퍼 ──────────────────────────
    private GatheringMember findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("신청 정보를 찾을 수 없습니다."));
    }

    private void verifyHost(Gathering gathering, Long userId) {
        if (!gathering.getHostUser().getId().equals(userId)) {
            throw new IllegalArgumentException("호스트만 처리할 수 있습니다.");
        }
    }
}