package org.ahpuh.surf.like.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.like.dto.response.LikeResponseDto;
import org.ahpuh.surf.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}")
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<LikeResponseDto> like(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        final LikeResponseDto response = likeService.like(authentication.userId, postId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/unlike/{likeId}")
    public ResponseEntity<Void> unlike(
            @PathVariable final Long postId,
            @PathVariable final Long likeId
    ) {
        likeService.unlike(likeId);
        return ResponseEntity.noContent().build();
    }
}
