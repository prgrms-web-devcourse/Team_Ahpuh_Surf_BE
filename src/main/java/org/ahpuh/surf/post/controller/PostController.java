package org.ahpuh.surf.post.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.post.dto.*;
import org.ahpuh.surf.post.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Long> createPost(@AuthenticationPrincipal final JwtAuthentication authentication,
                                           @Valid @RequestBody final PostRequestDto request) {
        final Long response = postService.create(authentication.userId, request);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + response))
                .body(response);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Long> updatePost(@PathVariable final Long postId, @Valid @RequestBody final PostRequestDto request) {
        final Long response = postService.update(postId, request);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDto> readPost(@PathVariable final Long postId) {
        final PostDto postDto = postService.readOne(postId);
        return ResponseEntity.ok()
                .body(postDto);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable final Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/calendarGraph")
    public ResponseEntity<List<PostCountDto>> getCounts(@RequestParam final int year, @RequestParam final Long userId) {
        final List<PostCountDto> responses = postService.getCountsPerDayWithYear(year, userId);
        return ResponseEntity.ok()
                .body(responses);
    }

    @GetMapping("/posts/score")
    public ResponseEntity<List<CategorySimpleDto>> getScores(@RequestParam final Long userId) {
        final List<CategorySimpleDto> responses = postService.getScoresWithCategoryByUserId(userId);
        return ResponseEntity.ok()
                .body(responses);
    }

    @PostMapping("/posts/{postId}/favorite")
    public ResponseEntity<Long> makeFavorite(@AuthenticationPrincipal final JwtAuthentication authentication,
                                             @PathVariable final Long postId) {
        final Long response = postService.clickFavorite(authentication.userId, postId);
        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping("/posts/{postId}/favorite")
    public ResponseEntity<Void> cancelFavorite(@AuthenticationPrincipal final JwtAuthentication authentication,
                                               @PathVariable final Long postId) {
        postService.clickFavorite(authentication.userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/follow/posts")
    public ResponseEntity<List<FollowingPostDto>> explore(
            @AuthenticationPrincipal final JwtAuthentication authentication
    ) {
        final List<FollowingPostDto> response = postService.explore(authentication.userId);
        return ResponseEntity.ok().body(response);
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
    public ResponseEntity<CursorResult<PostResponseDto>> getAllPost(
            @RequestParam final Long userId,
            final Long cursorId
    ) {
        return ResponseEntity.ok().body(postService.getAllPost(userId, cursorId, PageRequest.of(0, 10)));
    }

    @GetMapping("/posts")
    public ResponseEntity<CursorResult<PostResponseDto>> getAllPostByCategory(
            @RequestParam final Long userId,
            @RequestParam final Long categoryId,
            final Long cursorId
    ) {
        return ResponseEntity.ok().body(postService.getAllPostByCategory(userId, categoryId, cursorId, PageRequest.of(0, 10)));
    }

}
