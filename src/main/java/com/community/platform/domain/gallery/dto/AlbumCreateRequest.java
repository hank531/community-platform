package com.community.platform.domain.gallery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AlbumCreateRequest {

    @NotBlank(message = "앨범 제목을 입력하세요")
    @Size(max = 100, message = "제목은 100자 이하입니다")
    private String title;

    private String description;

    private boolean isPublic = true;
}