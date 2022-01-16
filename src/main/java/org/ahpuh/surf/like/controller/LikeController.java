package org.ahpuh.surf.like.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<Long> like(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long postId
    ) {
        final Long likeId = likeService.like(authentication.userId, postId);
        return ResponseEntity.ok().body(likeId);
    }

    @DeleteMapping("/unlike/{likeId}")
    public ResponseEntity<Void> unlike(
            @PathVariable final Long postId,
            @PathVariable final Long likeId
    ) {
        likeService.unlike(postId, likeId);
        return ResponseEntity.noContent().build();
    }

}
