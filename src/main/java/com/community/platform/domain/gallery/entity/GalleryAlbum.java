package com.community.platform.domain.gallery.entity;

import com.community.platform.common.entity.BaseEntity;
import com.community.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gallery_albums")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GalleryAlbum extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isPublic = true;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public static GalleryAlbum create(User user, String title,
                                      String description,
                                      boolean isPublic) {
        GalleryAlbum a = new GalleryAlbum();
        a.user        = user;
        a.title       = title;
        a.description = description;
        a.isPublic    = isPublic;
        return a;
    }

    public void update(String title, String description,
                       boolean isPublic) {
        this.title       = title;
        this.description = description;
        this.isPublic    = isPublic;
    }

    public void delete() { this.isDeleted = true; }
}