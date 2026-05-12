package com.community.platform.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostUpdateRequest {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotBlank(message = "내용을 입력하세요")
    private String content;
}