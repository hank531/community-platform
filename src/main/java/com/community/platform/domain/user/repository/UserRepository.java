package com.community.platform.domain.user.repository;

import com.community.platform.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (탈퇴하지 않은 사용자만)
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}