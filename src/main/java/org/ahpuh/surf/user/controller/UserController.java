package org.ahpuh.surf.user.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.UserLoginResponseDto;
import org.ahpuh.surf.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @RequestBody final UserLoginRequestDto request
    ) {
        final UserLoginResponseDto loginResponse = userService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping(path = "/users")
    public ResponseEntity<ApiResponse<Long>> join(
            @RequestBody final UserJoinRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponse.created(userService.join(request)));
    }

    /**
     * 보호받는 엔드포인트 - ROLE_USER 또는 ROLE_ADMIN 권한 필요함
     **/
//    @GetMapping(path = "/user/me")
//    public UserDto me(@AuthenticationPrincipal final JwtAuthentication authentication) {
//        return userService.findById(authentication.username)
//                .map(user ->
//                        new UserDto(authentication.token, authentication.username, user.getPermission().getName())
//                )
//                .orElseThrow(() -> new IllegalArgumentException("Could not found user for " + authentication.username));
//    }
}
