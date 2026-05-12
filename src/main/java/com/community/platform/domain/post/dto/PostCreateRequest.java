package com.community.platform.domain.post.dto;

import com.community.platform.domain.post.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class PostCreateRequest {

    @NotNull(message = "게시글 유형을 선택하세요")
    private PostType type;

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    // ── 구인글(GATHERING)일 때만 사용 ─────
    private Integer maxMembers;
    private LocalDate meetingDate;
    private LocalTime meetingTime;
    private String location;
}