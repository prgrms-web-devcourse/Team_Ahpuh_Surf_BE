package org.ahpuh.surf.user.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.user.dto.*;
import org.ahpuh.surf.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @Valid @RequestBody final UserLoginRequestDto request
    ) {
        final UserLoginResponseDto loginResponse = userService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping
    public ResponseEntity<Long> join(
            @Valid @RequestBody final UserJoinRequestDto request
    ) {
        final long userId = userService.join(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId))
                .body(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findUserInfo(
            @PathVariable final Long userId
    ) {
        final UserDto response = userService.findById(userId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Long> updateUser(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestBody final UserUpdateRequestDto request
    ) {
        userService.update(authentication.userId, request);
        return ResponseEntity.ok().body(authentication.userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal final JwtAuthentication authentication
    ) {
        userService.delete(authentication.userId);
        return ResponseEntity.noContent().build();
    }

}
