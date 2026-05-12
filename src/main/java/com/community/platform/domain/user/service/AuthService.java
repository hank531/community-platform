package com.community.platform.domain.user.service;

import com.community.platform.config.JwtTokenProvider;
import com.community.platform.domain.user.dto.LoginRequest;
import com.community.platform.domain.user.dto.RegisterRequest;
import com.community.platform.domain.user.dto.TokenResponse;
import com.community.platform.domain.user.entity.User;
import com.community.platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // ── 회원가입 ───────────────────────────────────
    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 아이디입니다.");
        }

        String encodedPw = passwordEncoder.encode(req.getPassword());

        User user = User.create(
                req.getUsername(),
                req.getEmail(),
                encodedPw,
                req.getNickname()
        );
        userRepository.save(user);
    }

    // ── 로그인 ─────────────────────────────────────
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepository
                .findByEmailAndIsDeletedFalse(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "이메일 또는 비밀번호가 틀렸습니다."));

        if (!passwordEncoder.matches(
                req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "이메일 또는 비밀번호가 틀렸습니다.");
        }

        String accessToken  = jwtTokenProvider
                .generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider
                .generateRefreshToken(user.getId());

        return new TokenResponse(accessToken, refreshToken);
    }
}