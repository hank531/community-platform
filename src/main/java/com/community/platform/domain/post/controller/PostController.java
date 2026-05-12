package com.community.platform.domain.post.controller;

import com.community.platform.common.dto.ApiResponse;
import com.community.platform.domain.post.dto.*;
import com.community.platform.domain.post.entity.PostType;
import com.community.platform.domain.post.service.*;
import com.community.platform.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService     postService;
    private final CommentService  commentService;
    private final GatheringService gatheringService;

    // GET /api/posts?type=FREE&page=0
    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "0") int page) {
        Page<PostResponse> result = postService.getPosts(type, page);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // GET /api/posts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(postService.getPost(id)));
    }

    // POST /api/posts
    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        postService.createPost(request, user.getId())));
    }

    // PUT /api/posts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        postService.updatePost(id, request, user.getId())));
    }

    // DELETE /api/posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        postService.deletePost(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // GET /api/posts/search?keyword=모임&page=0
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        postService.searchPosts(keyword, page)));
    }

    // ── 댓글 ───────────────────────────────

    // GET /api/posts/{id}/comments
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(commentService.getComments(id)));
    }

    // POST /api/posts/{id}/comments
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        commentService.createComment(id, request,
                                user.getId())));
    }

    // DELETE /api/posts/comments/{commentId}
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── 좋아요 ─────────────────────────────

    // POST /api/posts/{id}/like
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        boolean liked = commentService.toggleLike(
                id, "POST", user.getId());
        return ResponseEntity.ok(
                ApiResponse.ok(liked ? "좋아요" : "좋아요 취소"));
    }

    // ── 모임 신청 ──────────────────────────

    // POST /api/posts/{id}/join
    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        gatheringService.joinGathering(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // PATCH /api/posts/members/{memberId}/approve
    @PatchMapping("/members/{memberId}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long memberId,
            @AuthenticationPrincipal User user) {
        gatheringService.approveMember(memberId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // PATCH /api/posts/members/{memberId}/reject
    @PatchMapping("/members/{memberId}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long memberId,
            @AuthenticationPrincipal User user) {
        gatheringService.rejectMember(memberId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}