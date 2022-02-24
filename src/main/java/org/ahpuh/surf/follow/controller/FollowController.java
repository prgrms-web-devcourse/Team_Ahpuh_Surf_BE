package org.ahpuh.surf.follow.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.dto.FollowUserDto;
import org.ahpuh.surf.follow.dto.response.FollowResponseDto;
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
    public ResponseEntity<FollowResponseDto> follow(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @RequestBody final Long followUserId
    ) {
        final FollowResponseDto response = followService.follow(authentication.userId, followUserId);
        return ResponseEntity.created(URI.create("/api/v1/users/" + authentication.userId + "/following"))
                .body(response);
    }

    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @PathVariable final Long userId
    ) {
        followService.unfollow(authentication.userId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<List<FollowUserDto>> findFollowersList(
            @PathVariable final Long userId
    ) {
        final List<FollowUserDto> response = followService.findFollowerList(userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/users/{userId}/following")
    public ResponseEntity<List<FollowUserDto>> findFollowingList(
            @PathVariable final Long userId
    ) {
        final List<FollowUserDto> response = followService.findFollowingList(userId);
        return ResponseEntity.ok().body(response);
    }
}
