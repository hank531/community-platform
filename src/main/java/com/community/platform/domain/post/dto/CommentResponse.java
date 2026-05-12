package com.community.platform.domain.post.dto;

import com.community.platform.domain.post.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponse {

    private Long id;
    private String authorNickname;
    private String content;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private List<CommentResponse> children;

    public static CommentResponse from(Comment c,
                                       List<CommentResponse> children) {
        CommentResponse r = new CommentResponse();
        r.id              = c.getId();
        r.authorNickname  = c.isDeleted()
                ? "(삭제된 댓글)" : c.getUser().getNickname();
        r.content         = c.isDeleted()
                ? "(삭제된 댓글입니다.)" : c.getContent();
        r.isDeleted       = c.isDeleted();
        r.createdAt       = c.getCreatedAt();
        r.children        = children;
        return r;
    }
}