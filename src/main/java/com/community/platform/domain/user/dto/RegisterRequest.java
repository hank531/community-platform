package com.community.platform.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "아이디를 입력하세요")
    @Size(min = 3, max = 20, message = "아이디는 3~20자입니다")
    private String username;

    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min = 8, message = "비밀번호는 8자 이상입니다")
    private String password;

    @NotBlank(message = "닉네임을 입력하세요")
    @Size(max = 20, message = "닉네임은 20자 이하입니다")
    private String nickname;
}