package com.community.platform.domain.post.entity;

import com.community.platform.common.entity.BaseEntity;
import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "gatherings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gathering extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User hostUser;

    @Column(nullable = false)
    private int maxMembers;

    @Column(nullable = false)
    private int currentMembers = 1; // 호스트 포함

    private LocalDate meetingDate;
    private LocalTime meetingTime;

    @Column(length = 300)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatheringStatus status = GatheringStatus.RECRUITING;

    // ── 생성 ──────────────────────────────
    public static Gathering create(Post post, User hostUser,
                                   int maxMembers, LocalDate meetingDate,
                                   LocalTime meetingTime, String location) {
        Gathering g  = new Gathering();
        g.post        = post;
        g.hostUser    = hostUser;
        g.maxMembers  = maxMembers;
        g.meetingDate = meetingDate;
        g.meetingTime = meetingTime;
        g.location    = location;
        return g;
    }

    // ── 인원 증가 (승인 시) ────────────────
    public void addMember() {
        this.currentMembers++;
        if (this.currentMembers >= this.maxMembers) {
            this.status = GatheringStatus.CLOSED;
        }
    }

    // ── 인원 감소 (취소 시) ────────────────
    public void removeMember() {
        if (this.currentMembers > 1) this.currentMembers--;
        if (this.status == GatheringStatus.CLOSED) {
            this.status = GatheringStatus.RECRUITING;
        }
    }
}