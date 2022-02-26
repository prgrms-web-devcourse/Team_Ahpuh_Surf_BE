package org.ahpuh.surf.post.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.post.dto.ExploreDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.dto.RecentPostDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.post.service.PostService;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<PostCreateResponseDto> createPost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestPart(value = "request") final PostRequestDto request,
            @RequestPart(value = "file", required = false) final MultipartFile file
    ) {
        final PostCreateResponseDto response = postService.create(authentication.userId, request, file);

        return ResponseEntity.created(URI.create("/api/v1/posts/" + response))
                .body(response);
    }

    @PutMapping(value = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostUpdateResponseDto> updatePost(
            @PathVariable final Long postId,
            @Valid @RequestPart(value = "request") final PostRequestDto request,
            @RequestPart(value = "file", required = false) final MultipartFile file
    ) {
        final PostUpdateResponseDto response = postService.update(postId, request, file);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostReadResponseDto> readPost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        final PostReadResponseDto response = postService.readOne(authentication.userId, postId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable final Long postId
    ) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/calendarGraph")
    public ResponseEntity<List<PostCountDto>> getCounts(
            @RequestParam final int year,
            @RequestParam final Long userId
    ) {
        final List<PostCountDto> postCountDtos = postService.getCountsPerDayWithYear(year, userId);
        return ResponseEntity.ok().body(postCountDtos);
    }

    @GetMapping("/posts/score")
    public ResponseEntity<List<CategorySimpleDto>> getScores(
            @RequestParam final Long userId
    ) {
        final List<CategorySimpleDto> categorySimpleDtos = postService.getScoresWithCategoryByUserId(userId);
        return ResponseEntity.ok().body(categorySimpleDtos);
    }

    @PostMapping("/posts/{postId}/favorite")
    public ResponseEntity<PostFavoriteResponseDto> makeFavorite(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        final PostFavoriteResponseDto response = postService.clickFavorite(authentication.userId, postId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/posts/{postId}/favorite")
    public ResponseEntity<Void> cancelFavorite(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        postService.clickFavorite(authentication.userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/follow/posts")
    public ResponseEntity<CursorResult<ExploreDto>> followingExplore(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long cursorId
    ) {
        final CursorResult<ExploreDto> followingPostDtos = postService.followingExplore(authentication.userId, cursorId, PageRequest.of(0, 10));
        return ResponseEntity.ok().body(followingPostDtos);
    }

    @GetMapping("/posts/month")
    public ResponseEntity<List<PostResponseDto>> getPost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Integer year,
            @RequestParam final Integer month
    ) {
        final Long userId = authentication.userId;
        return ResponseEntity.ok().body(postService.getPost(userId, year, month));
    }

    @GetMapping("/posts/all")
    public ResponseEntity<CursorResult<AllPostResponseDto>> getAllPost(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long userId,
            @RequestParam final Long cursorId
    ) {
        return ResponseEntity.ok().body(postService.getAllPost(authentication.userId, userId, cursorId, PageRequest.of(0, 10)));
    }

    @GetMapping("/posts")
    public ResponseEntity<CursorResult<AllPostResponseDto>> getRecentScoreByAllPostsOfCategory(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long userId,
            @RequestParam final Long categoryId,
            @RequestParam final Long cursorId
    ) {
        return ResponseEntity.ok().body(postService.getAllPostByCategory(authentication.userId, userId, categoryId, cursorId, PageRequest.of(0, 10)));
    }

    @GetMapping("/recentscore")
    public ResponseEntity<PostsRecentScoreResponseDto> getRecentScoreByAllPostsOfCategory(
            @RequestParam final Long categoryId
    ) {
        final PostsRecentScoreResponseDto response = postService.getRecentScore(categoryId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/posts/recent")
    public ResponseEntity<CursorResult<RecentPostDto>> recentAllPosts(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestParam final Long cursorId
    ) {
        final CursorResult<RecentPostDto> recentAllPosts = postService.recentAllPosts(authentication.userId, cursorId, PageRequest.of(0, 10));
        return ResponseEntity.ok().body(recentAllPosts);
    }
}
