package com.community.platform.domain.gallery.repository;

import com.community.platform.domain.gallery.entity.GalleryAlbum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GalleryAlbumRepository
        extends JpaRepository<GalleryAlbum, Long> {

    Page<GalleryAlbum> findByIsPublicTrueAndIsDeletedFalse(
            Pageable pageable);

    Page<GalleryAlbum> findByUserIdAndIsDeletedFalse(
            Long userId, Pageable pageable);

    Optional<GalleryAlbum> findByIdAndIsDeletedFalse(Long id);
}