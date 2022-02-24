package org.ahpuh.surf.user.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.s3.S3Service;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.dto.response.UserJoinResponseDto;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;
import org.ahpuh.surf.user.dto.response.UserUpdateResponseDto;
import org.ahpuh.surf.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @Valid @RequestBody final UserLoginRequestDto request
    ) {
        final UserLoginResponseDto loginResponse = userService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping
    public ResponseEntity<UserJoinResponseDto> join(
            @Valid @RequestBody final UserJoinRequestDto request
    ) {
        final UserJoinResponseDto joinResponse = userService.join(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + joinResponse.getUserId()))
                .body(joinResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserFindInfoResponseDto> findUserInfo(
            @PathVariable final Long userId
    ) {
        final UserFindInfoResponseDto response = userService.findUser(userId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserUpdateResponseDto> updateUser(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestPart(value = "request") final UserUpdateRequestDto request,
            @RequestPart(value = "file", required = false) final MultipartFile profilePhoto
    ) throws IOException {
        final String profilePhotoUrl = s3Service.uploadUserImg(profilePhoto);
        final UserUpdateResponseDto response = userService.update(authentication.userId, request, profilePhotoUrl);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal final JwtAuthentication authentication
    ) {
        userService.delete(authentication.userId);
        return ResponseEntity.noContent().build();
    }
}
