package com.community.platform.domain.gallery.controller;

import com.community.platform.common.dto.ApiResponse;
import com.community.platform.domain.gallery.dto.*;
import com.community.platform.domain.gallery.service.*;
import com.community.platform.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService   galleryService;
    private final PhotoLikeService photoLikeService;

    // ── 앨범 ───────────────────────────────

    // POST /api/albums
    @PostMapping("/albums")
    public ResponseEntity<?> createAlbum(
            @Valid @RequestBody AlbumCreateRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(
                galleryService.createAlbum(request, user.getId())));
    }

    // GET /api/albums?page=0
    @GetMapping("/albums")
    public ResponseEntity<?> getAlbums(
            @RequestParam(defaultValue = "0") int page) {
        Page<AlbumResponse> result = galleryService.getAlbums(page);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // GET /api/albums/{albumId}
    @GetMapping("/albums/{albumId}")
    public ResponseEntity<?> getAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(ApiResponse.ok(
                galleryService.getAlbum(albumId)));
    }

    // DELETE /api/albums/{albumId}
    @DeleteMapping("/albums/{albumId}")
    public ResponseEntity<?> deleteAlbum(
            @PathVariable Long albumId,
            @AuthenticationPrincipal User user) {
        galleryService.deleteAlbum(albumId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── 사진 ───────────────────────────────

    // POST /api/albums/{albumId}/photos  (multipart/form-data)
    @PostMapping(
            value    = "/albums/{albumId}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadPhotos(
            @PathVariable Long albumId,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal User user) {
        List<PhotoResponse> result =
                galleryService.uploadPhotos(albumId, files, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // GET /api/albums/{albumId}/photos?page=0
    @GetMapping("/albums/{albumId}/photos")
    public ResponseEntity<?> getPhotos(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "0") int page) {
        Page<PhotoResponse> result =
                galleryService.getPhotos(albumId, page);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // DELETE /api/photos/{photoId}
    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<?> deletePhoto(
            @PathVariable Long photoId,
            @AuthenticationPrincipal User user) {
        galleryService.deletePhoto(photoId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // PATCH /api/photos/{photoId}/caption
    @PatchMapping("/photos/{photoId}/caption")
    public ResponseEntity<?> updateCaption(
            @PathVariable Long photoId,
            @RequestParam String caption,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(
                galleryService.updateCaption(
                        photoId, caption, user.getId())));
    }

    // POST /api/photos/{photoId}/like
    @PostMapping("/photos/{photoId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long photoId,
            @AuthenticationPrincipal User user) {
        boolean liked =
                photoLikeService.toggleLike(photoId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(
                liked ? "좋아요" : "좋아요 취소"));
    }
}