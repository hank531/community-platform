package com.community.platform.domain.gallery.dto;

import com.community.platform.domain.gallery.entity.GalleryAlbum;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AlbumResponse {

    private Long   id;
    private String ownerNickname;
    private String title;
    private String description;
    private boolean isPublic;
    private long   photoCount;
    private LocalDateTime createdAt;

    public static AlbumResponse from(GalleryAlbum album,
                                     long photoCount) {
        AlbumResponse r  = new AlbumResponse();
        r.id             = album.getId();
        r.ownerNickname  = album.getUser().getNickname();
        r.title          = album.getTitle();
        r.description    = album.getDescription();
        r.isPublic       = album.isPublic();
        r.photoCount     = photoCount;
        r.createdAt      = album.getCreatedAt();
        return r;
    }
}