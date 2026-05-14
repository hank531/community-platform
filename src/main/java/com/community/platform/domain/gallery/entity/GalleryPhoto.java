package com.community.platform.domain.gallery.entity;

import com.community.platform.common.entity.BaseEntity;
import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gallery_photos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GalleryPhoto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private GalleryAlbum album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String imageUrl;        // MinIO 원본 URL

    @Column(nullable = false, length = 500)
    private String thumbnailUrl;    // MinIO 썸네일 URL

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false)
    private Long fileSize;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public static GalleryPhoto create(GalleryAlbum album, User user,
                                      String imageUrl, String thumbnailUrl,
                                      String originalFilename, Long fileSize) {
        GalleryPhoto p  = new GalleryPhoto();
        p.album            = album;
        p.user             = user;
        p.imageUrl         = imageUrl;
        p.thumbnailUrl     = thumbnailUrl;
        p.originalFilename = originalFilename;
        p.fileSize         = fileSize;
        return p;
    }

    public void updateCaption(String caption) {
        this.caption = caption;
    }

    public void delete() { this.isDeleted = true; }
}