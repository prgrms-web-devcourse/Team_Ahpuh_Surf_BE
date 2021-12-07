package org.ahpuh.surf.user.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.response.ApiResponse;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.UserLoginResponseDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/users/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(
            @RequestBody final UserLoginRequestDto request
    ) {
        final JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(), request.getPassword());
        final Authentication resultToken = authenticationManager.authenticate(authToken);
        final JwtAuthentication authentication = (JwtAuthentication) resultToken.getPrincipal();
        final User user = (User) resultToken.getDetails();
        return ResponseEntity.ok(ApiResponse.ok(new UserLoginResponseDto(authentication.token, user.getUserId())));
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
