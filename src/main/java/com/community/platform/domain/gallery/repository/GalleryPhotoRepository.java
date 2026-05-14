package com.community.platform.domain.gallery.repository;

import com.community.platform.domain.gallery.entity.GalleryPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GalleryPhotoRepository
        extends JpaRepository<GalleryPhoto, Long> {

    Page<GalleryPhoto> findByAlbumIdAndIsDeletedFalse(
            Long albumId, Pageable pageable);

    List<GalleryPhoto> findByAlbumIdAndIsDeletedFalse(Long albumId);

    Optional<GalleryPhoto> findByIdAndIsDeletedFalse(Long id);

    long countByAlbumIdAndIsDeletedFalse(Long albumId);
}