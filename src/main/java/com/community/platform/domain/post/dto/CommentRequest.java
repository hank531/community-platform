package com.community.platform.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequest {

    @NotBlank(message = "댓글 내용을 입력하세요")
    private String content;

    // null이면 최상위 댓글, 값이 있으면 대댓글
    private Long parentId;
}