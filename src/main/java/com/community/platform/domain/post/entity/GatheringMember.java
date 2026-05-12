package com.community.platform.domain.post.entity;

import com.community.platform.common.entity.BaseEntity;
import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "gathering_members",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"gathering_id", "user_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.PENDING;

    private LocalDateTime joinedAt;

    // ── 생성 ──────────────────────────────
    public static GatheringMember create(Gathering gathering,
                                         User user) {
        GatheringMember m = new GatheringMember();
        m.gathering = gathering;
        m.user      = user;
        return m;
    }

    // ── 승인 ──────────────────────────────
    public void approve() {
        this.status   = MemberStatus.APPROVED;
        this.joinedAt = LocalDateTime.now();
    }

    // ── 거절 ──────────────────────────────
    public void reject() {
        this.status = MemberStatus.REJECTED;
    }
}