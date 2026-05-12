package com.community.platform.domain.post.entity;

import com.community.platform.common.entity.BaseEntity;
import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 대댓글 부모 (null이면 최상위 댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── 생성 ──────────────────────────────
    public static Comment create(Post post, User user,
                                 Comment parent, String content) {
        Comment c = new Comment();
        c.post    = post;
        c.user    = user;
        c.parent  = parent;
        c.content = content;
        return c;
    }

    // ── 삭제 ──────────────────────────────
    public void delete() {
        this.isDeleted = true;
    }
}