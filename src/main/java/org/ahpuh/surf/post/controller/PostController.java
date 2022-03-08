package org.ahpuh.surf.post.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.post.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestPart(value = "request") final PostRequestDto request,
            @RequestPart(value = "file", required = false) final MultipartFile file
    ) {
        final Long postId = postService.create(authentication.userId, request, file);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + postId)).build();
    }

    @PutMapping(value = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePost(
            @PathVariable final Long postId,
            @Valid @RequestPart(value = "request") final PostRequestDto request,
            @RequestPart(value = "file", required = false) final MultipartFile file
    ) {
        postService.update(postId, request, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostReadResponseDto> readPost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        final PostReadResponseDto response = postService.readPost(authentication.userId, postId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        postService.delete(authentication.userId, postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/favorite")
    public ResponseEntity<Void> makeFavorite(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        postService.makeFavorite(authentication.userId, postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/favorite")
    public ResponseEntity<Void> cancelFavorite(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        postService.cancelFavorite(authentication.userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/month")
    public ResponseEntity<List<PostsOfMonthResponseDto>> getPostOfPeriod(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Integer year,
            @RequestParam final Integer month
    ) {
        final Long userId = authentication.userId;
        return ResponseEntity.ok().body(postService.getPostsOfMonth(userId, year, month));
    }

    @GetMapping("/recentscore")
    public ResponseEntity<PostsRecentScoreResponseDto> getRecentScoreByAllPostsOfCategory(
            @RequestParam final Long categoryId
    ) {
        final PostsRecentScoreResponseDto response = postService.getRecentScore(categoryId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/posts/calendarGraph")
    public ResponseEntity<List<PostCountResponseDto>> getPostCountsOfYear(
            @RequestParam final int year,
            @RequestParam final Long userId
    ) {
        final List<PostCountResponseDto> postCountDtos = postService.getPostCountsOfYear(year, userId);
        return ResponseEntity.ok().body(postCountDtos);
    }

    @GetMapping("/posts/score")
    public ResponseEntity<List<CategorySimpleDto>> getScoresOfCategory(
            @RequestParam final Long userId
    ) {
        final List<CategorySimpleDto> categorySimpleDtos = postService.getScoresOfCategoryByUser(userId);
        return ResponseEntity.ok().body(categorySimpleDtos);
    }

    @GetMapping("/posts/recent")
    public ResponseEntity<CursorResult<RecentPostResponseDto>> recentAllPosts(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long cursorId
    ) {
        final CursorResult<RecentPostResponseDto> recentAllPosts = postService.recentAllPosts(authentication.userId, cursorId);
        return ResponseEntity.ok().body(recentAllPosts);
    }

    @GetMapping("/follow/posts")
    public ResponseEntity<CursorResult<ExploreResponseDto>> followExplore(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long cursorId
    ) {
        final CursorResult<ExploreResponseDto> followingPostDtos = postService.followExplore(authentication.userId, cursorId);
        return ResponseEntity.ok().body(followingPostDtos);
    }

    @GetMapping("/posts/all")
    public ResponseEntity<CursorResult<AllPostResponseDto>> getAllPostByUser(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long userId,
            @RequestParam final Long cursorId
    ) {
        return ResponseEntity.ok().body(postService.getAllPostByUser(authentication.userId, userId, cursorId));
    }

    @GetMapping("/posts")
    public ResponseEntity<CursorResult<AllPostResponseDto>> getAllPostByCategory(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long categoryId,
            @RequestParam final Long cursorId
    ) {
        return ResponseEntity.ok().body(postService.getAllPostByCategory(authentication.userId, categoryId, cursorId));
    }
}
