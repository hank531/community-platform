package com.community.platform.domain.post.dto;

import com.community.platform.domain.post.entity.Post;
import com.community.platform.domain.post.entity.PostType;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private Long id;
    private String authorNickname;
    private PostType type;
    private String title;
    private String content;
    private int viewCount;
    private boolean isPinned;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post) {
        PostResponse r = new PostResponse();
        r.id              = post.getId();
        r.authorNickname  = post.getUser().getNickname();
        r.type            = post.getType();
        r.title           = post.getTitle();
        r.content         = post.getContent();
        r.viewCount       = post.getViewCount();
        r.isPinned        = post.isPinned();
        r.createdAt       = post.getCreatedAt();
        return r;
    }
}