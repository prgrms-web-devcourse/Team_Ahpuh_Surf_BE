package org.ahpuh.backend.post.controller;

import org.ahpuh.backend.common.response.ApiResponse;
import org.ahpuh.backend.post.dto.PostDto;
import org.ahpuh.backend.post.dto.PostIdResponse;
import org.ahpuh.backend.post.dto.PostRequest;
import org.ahpuh.backend.post.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostServiceImpl postService;

    public PostController(final PostServiceImpl postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostIdResponse>> createPost(@Valid @RequestBody final PostRequest request) {
        // TODO: userId
        final PostIdResponse response = postService.create(request);
        return ResponseEntity.created(URI.create("/posts/" + response.getId()))
                .body(ApiResponse.created(response));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostIdResponse>> updatePost(@PathVariable final Long postId, @Valid @RequestBody final PostRequest request) {
        final PostIdResponse response = postService.update(postId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.ok(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> readPost(@PathVariable final Long postId) {
        final PostDto postDto = postService.readOne(postId);
        return ResponseEntity.ok()
                .body(ApiResponse.ok(postDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable final Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok()
                .body(ApiResponse.noContent());
    }

}
