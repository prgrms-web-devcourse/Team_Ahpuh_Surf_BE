package org.ahpuh.surf.follow.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.service.FollowService;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<Long> follow(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestBody final Long followUserId
    ) {
        final Long followId = followService.follow(authentication.userId, followUserId);
        return ResponseEntity.created(URI.create("/users/" + authentication.userId + "/following"))
                .body(followId);
    }

    @DeleteMapping("/follow/{followId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable final Long followId
    ) {
        followService.unfollow(followId);
        return ResponseEntity.noContent().build();
    }


}
