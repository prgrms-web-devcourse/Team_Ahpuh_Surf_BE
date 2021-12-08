package org.ahpuh.surf.post.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponseDto;
import org.ahpuh.surf.post.dto.PostRequestDto;
import org.ahpuh.surf.post.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping
    public ResponseEntity<PostIdResponseDto> createPost(@Valid @RequestBody final PostRequestDto request) {
        // TODO: userId
        final PostIdResponseDto response = postService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostIdResponseDto> updatePost(@PathVariable final Long postId, @Valid @RequestBody final PostRequestDto request) {
        final PostIdResponseDto response = postService.update(postId, request);
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

}
