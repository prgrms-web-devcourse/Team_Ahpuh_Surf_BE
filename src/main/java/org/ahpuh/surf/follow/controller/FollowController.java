package org.ahpuh.surf.follow.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.dto.FollowUserDto;
import org.ahpuh.surf.follow.service.FollowService;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<Long> follow(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestBody final Long followUserId
    ) {
        final Long followId = followService.follow(authentication.userId, followUserId);
        return ResponseEntity.created(URI.create("/api/v1/users/" + authentication.userId + "/following"))
                .body(followId);
    }

    @DeleteMapping("/follow/{followId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable final Long followId
    ) {
        followService.unfollow(followId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<List<FollowUserDto>> findFollowingList(
            @PathVariable final Long userId
    ) {
        final List<FollowUserDto> response = followService.findFollowerList(userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/users/{userId}/following")
    public ResponseEntity<List<FollowUserDto>> findFollowList(
            @PathVariable final Long userId
    ) {
        final List<FollowUserDto> response = followService.findFollowingList(userId);
        return ResponseEntity.ok().body(response);
    }

}
