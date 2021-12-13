package org.ahpuh.surf.post.controller;

import org.ahpuh.surf.common.response.ApiResponse;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponse;
import org.ahpuh.surf.post.dto.PostRequest;
import org.ahpuh.surf.post.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class PostController {

    private final PostServiceImpl postService;

    public PostController(final PostServiceImpl postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostIdResponse>> createPost(@Valid @RequestBody final PostRequest request) {
        // TODO: userId
        final PostIdResponse response = postService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + response.getId()))
                .body(ApiResponse.created(response));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostIdResponse>> updatePost(@PathVariable final Long postId, @Valid @RequestBody final PostRequest request) {
        final PostIdResponse response = postService.update(postId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.ok(response));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> readPost(@PathVariable final Long postId) {
        final PostDto postDto = postService.readOne(postId);
        return ResponseEntity.ok()
                .body(ApiResponse.ok(postDto));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable final Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok()
                .body(ApiResponse.noContent());
    }

    @GetMapping("/follow/posts")
    public ResponseEntity<List<FollowingPostDto>> explore(
            @AuthenticationPrincipal final JwtAuthentication authentication
    ) {
        final List<FollowingPostDto> response = postService.explore(authentication.userId);
        return ResponseEntity.ok().body(response);
    }

}
