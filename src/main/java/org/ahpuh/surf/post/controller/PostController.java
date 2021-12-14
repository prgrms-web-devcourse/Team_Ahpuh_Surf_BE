package org.ahpuh.surf.post.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;
import org.ahpuh.surf.post.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping
    public ResponseEntity<Long> createPost(@AuthenticationPrincipal final JwtAuthentication authentication,
                                           @Valid @RequestBody final PostRequestDto request) {
        final Long response = postService.create(authentication.userId, request);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + response))
                .body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Long> updatePost(@PathVariable final Long postId, @Valid @RequestBody final PostRequestDto request) {
        final Long response = postService.update(postId, request);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> readPost(@PathVariable final Long postId) {
        final PostDto postDto = postService.readOne(postId);
        return ResponseEntity.ok()
                .body(postDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable final Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/favorite")
    public ResponseEntity<Long> makeFavorite(@AuthenticationPrincipal final JwtAuthentication authentication,
                                             @PathVariable final Long postId) {
        final Long response = postService.clickFavorite(authentication.userId, postId);
        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping("/{postId}/favorite")
    public ResponseEntity<Void> cancelFavorite(@AuthenticationPrincipal final JwtAuthentication authentication,
                                               @PathVariable final Long postId) {
        postService.clickFavorite(authentication.userId, postId);
        return ResponseEntity.noContent().build();
    }

}
