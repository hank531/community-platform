package com.community.platform.domain.gallery.dto;

import com.community.platform.domain.gallery.entity.GalleryPhoto;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PhotoResponse {

    private Long   id;
    private Long   albumId;
    private String uploaderNickname;
    private String imageUrl;
    private String thumbnailUrl;
    private String originalFilename;
    private Long   fileSize;
    private String caption;
    private long   likeCount;
    private LocalDateTime createdAt;

    public static PhotoResponse from(GalleryPhoto photo,
                                     long likeCount) {
        PhotoResponse r    = new PhotoResponse();
        r.id               = photo.getId();
        r.albumId          = photo.getAlbum().getId();
        r.uploaderNickname = photo.getUser().getNickname();
        r.imageUrl         = photo.getImageUrl();
        r.thumbnailUrl     = photo.getThumbnailUrl();
        r.originalFilename = photo.getOriginalFilename();
        r.fileSize         = photo.getFileSize();
        r.caption          = photo.getCaption();
        r.likeCount        = likeCount;
        r.createdAt        = photo.getCreatedAt();
        return r;
    }
}