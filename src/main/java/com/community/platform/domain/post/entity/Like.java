package com.community.platform.domain.post.entity;

import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "likes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "target_type", "target_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String targetType; // "POST", "COMMENT", "PHOTO"

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── 생성 ──────────────────────────────
    public static Like create(User user, String targetType,
                              Long targetId) {
        Like like = new Like();
        like.user       = user;
        like.targetType = targetType;
        like.targetId   = targetId;
        return like;
    }
}