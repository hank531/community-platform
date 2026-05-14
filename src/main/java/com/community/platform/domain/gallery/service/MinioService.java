package com.community.platform.domain.gallery.service;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    // ── 원본 이미지 업로드 ─────────────────
    public String uploadImage(MultipartFile file) {
        validateImageFile(file);
        String key = "photos/" + UUID.randomUUID()
                + getExtension(file.getOriginalFilename());
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (MinioException | IOException
                 | InvalidKeyException
                 | NoSuchAlgorithmException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
        return buildUrl(key);
    }

    // ── 썸네일 생성 & 업로드 ───────────────
    public String uploadThumbnail(MultipartFile file) {
        String key = "thumbnails/" + UUID.randomUUID() + ".jpg";
        try {
            // 320x320 썸네일 생성
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(320, 320)
                    .outputFormat("jpg")
                    .toOutputStream(baos);

            byte[] thumbBytes = baos.toByteArray();
            ByteArrayInputStream bais =
                    new ByteArrayInputStream(thumbBytes);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(bais, thumbBytes.length, -1)
                            .contentType("image/jpeg")
                            .build()
            );
        } catch (MinioException | IOException
                 | InvalidKeyException
                 | NoSuchAlgorithmException e) {
            throw new RuntimeException("썸네일 생성에 실패했습니다.", e);
        }
        return buildUrl(key);
    }

    // ── 파일 삭제 ──────────────────────────
    public void deleteFile(String fileUrl) {
        String key = extractKey(fileUrl);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (MinioException | IOException
                 | InvalidKeyException
                 | NoSuchAlgorithmException e) {
            // 삭제 실패는 로그만 남기고 계속 진행
            System.err.println("MinIO 파일 삭제 실패: " + key);
        }
    }

    // ── 이미지 검증 ────────────────────────
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "이미지 파일만 업로드 가능합니다.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "파일 크기는 10MB 이하여야 합니다.");
        }
    }

    // ── URL 조합 ───────────────────────────
    // http://localhost:9000/community/photos/uuid.jpg
    private String buildUrl(String key) {
        return endpoint + "/" + bucket + "/" + key;
    }

    // ── URL에서 객체 키 추출 ───────────────
    private String extractKey(String url) {
        // "http://localhost:9000/community/photos/uuid.jpg"
        // prefix = "http://localhost:9000/community/"
        String prefix = endpoint + "/" + bucket + "/";
        return url.substring(prefix.length());
    }

    // ── 확장자 추출 ────────────────────────
    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}