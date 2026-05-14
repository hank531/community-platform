package com.community.platform.domain.gallery.service;

import com.community.platform.domain.gallery.dto.*;
import com.community.platform.domain.gallery.entity.*;
import com.community.platform.domain.gallery.repository.*;
import com.community.platform.domain.post.repository.LikeRepository;
import com.community.platform.domain.user.entity.User;
import com.community.platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryAlbumRepository albumRepository;
    private final GalleryPhotoRepository photoRepository;
    private final UserRepository         userRepository;
    private final LikeRepository         likeRepository;
    private final MinioService           minioService;  // ← MinioService

    // ── 앨범 생성 ──────────────────────────
    @Transactional
    public AlbumResponse createAlbum(AlbumCreateRequest req,
                                     Long userId) {
        User user = findUser(userId);
        GalleryAlbum album = GalleryAlbum.create(
                user, req.getTitle(),
                req.getDescription(), req.isPublic());
        albumRepository.save(album);
        return AlbumResponse.from(album, 0);
    }

    // ── 앨범 목록 조회 ─────────────────────
    @Transactional(readOnly = true)
    public Page<AlbumResponse> getAlbums(int page) {
        Pageable pageable = PageRequest.of(page, 12,
                Sort.by("createdAt").descending());
        return albumRepository
                .findByIsPublicTrueAndIsDeletedFalse(pageable)
                .map(album -> {
                    long cnt = photoRepository
                            .countByAlbumIdAndIsDeletedFalse(album.getId());
                    return AlbumResponse.from(album, cnt);
                });
    }

    // ── 앨범 상세 조회 ─────────────────────
    @Transactional(readOnly = true)
    public AlbumResponse getAlbum(Long albumId) {
        GalleryAlbum album = findAlbum(albumId);
        long cnt = photoRepository
                .countByAlbumIdAndIsDeletedFalse(albumId);
        return AlbumResponse.from(album, cnt);
    }

    // ── 앨범 삭제 ──────────────────────────
    @Transactional
    public void deleteAlbum(Long albumId, Long userId) {
        GalleryAlbum album = findAlbum(albumId);
        if (!album.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인 앨범만 삭제할 수 있습니다.");
        }
        photoRepository.findByAlbumIdAndIsDeletedFalse(albumId)
                .forEach(photo -> {
                    minioService.deleteFile(photo.getImageUrl());
                    minioService.deleteFile(photo.getThumbnailUrl());
                    photo.delete();
                });
        album.delete();
    }

    // ── 사진 업로드 (다중) ─────────────────
    @Transactional
    public List<PhotoResponse> uploadPhotos(Long albumId,
                                            List<MultipartFile> files, Long userId) {
        GalleryAlbum album = findAlbum(albumId);
        if (!album.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인 앨범에만 업로드할 수 있습니다.");
        }
        if (files.size() > 10) {
            throw new IllegalArgumentException(
                    "한 번에 최대 10장까지 업로드 가능합니다.");
        }
        User user = findUser(userId);
        return files.stream().map(file -> {
            String imageUrl     = minioService.uploadImage(file);
            String thumbnailUrl = minioService.uploadThumbnail(file);
            GalleryPhoto photo  = GalleryPhoto.create(
                    album, user,
                    imageUrl, thumbnailUrl,
                    file.getOriginalFilename(),
                    file.getSize());
            photoRepository.save(photo);
            return PhotoResponse.from(photo, 0);
        }).collect(Collectors.toList());
    }

    // ── 사진 목록 조회 ─────────────────────
    @Transactional(readOnly = true)
    public Page<PhotoResponse> getPhotos(Long albumId, int page) {
        Pageable pageable = PageRequest.of(page, 20,
                Sort.by("createdAt").descending());
        return photoRepository
                .findByAlbumIdAndIsDeletedFalse(albumId, pageable)
                .map(photo -> {
                    long likes = likeRepository
                            .countByTargetTypeAndTargetId(
                                    "PHOTO", photo.getId());
                    return PhotoResponse.from(photo, likes);
                });
    }

    // ── 사진 삭제 ──────────────────────────
    @Transactional
    public void deletePhoto(Long photoId, Long userId) {
        GalleryPhoto photo = photoRepository
                .findByIdAndIsDeletedFalse(photoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("사진을 찾을 수 없습니다."));
        if (!photo.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인 사진만 삭제할 수 있습니다.");
        }
        minioService.deleteFile(photo.getImageUrl());
        minioService.deleteFile(photo.getThumbnailUrl());
        photo.delete();
    }

    // ── 캡션 수정 ──────────────────────────
    @Transactional
    public PhotoResponse updateCaption(Long photoId,
                                       String caption,
                                       Long userId) {
        GalleryPhoto photo = photoRepository
                .findByIdAndIsDeletedFalse(photoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("사진을 찾을 수 없습니다."));
        if (!photo.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인 사진만 수정할 수 있습니다.");
        }
        photo.updateCaption(caption);
        long likes = likeRepository
                .countByTargetTypeAndTargetId("PHOTO", photoId);
        return PhotoResponse.from(photo, likes);
    }

    // ── 내부 헬퍼 ──────────────────────────
    private GalleryAlbum findAlbum(Long id) {
        return albumRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("앨범을 찾을 수 없습니다."));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }
}