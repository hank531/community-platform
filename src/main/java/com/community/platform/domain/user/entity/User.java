package com.community.platform.domain.user.entity;

import com.community.platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── 생성 ──────────────────────────────────────
    public static User create(String username, String email,
                              String passwordHash, String nickname) {
        User user = new User();
        user.username    = username;
        user.email       = email;
        user.passwordHash = passwordHash;
        user.nickname    = nickname;
        return user;
    }

    // ── 수정 ──────────────────────────────────────
    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname        = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    // ── 탈퇴 ──────────────────────────────────────
    public void delete() {
        this.isDeleted = true;
    }
}