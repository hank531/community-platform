package com.community.platform.domain.post.entity;

import com.community.platform.common.entity.BaseEntity;
import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private boolean isPinned = false;

    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── 생성 ──────────────────────────────
    public static Post create(User user, PostType type,
                              String title, String content) {
        Post post = new Post();
        post.user    = user;
        post.type    = type;
        post.title   = title;
        post.content = content;
        return post;
    }

    // ── 수정 ──────────────────────────────
    public void update(String title, String content) {
        this.title   = title;
        this.content = content;
    }

    // ── 조회수 증가 ────────────────────────
    public void increaseViewCount() {
        this.viewCount++;
    }

    // ── 공지 고정 ──────────────────────────
    public void pin(boolean pinned) {
        this.isPinned = pinned;
    }

    // ── 삭제 ──────────────────────────────
    public void delete() {
        this.isDeleted = true;
    }
}