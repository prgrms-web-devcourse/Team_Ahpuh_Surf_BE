package org.ahpuh.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.backend.jwt.JwtAuthentication;
import org.ahpuh.backend.jwt.JwtAuthenticationToken;
import org.ahpuh.backend.user.dto.UserLoginRequestDto;
import org.ahpuh.backend.user.dto.UserLoginResponseDto;
import org.ahpuh.backend.user.entity.User;
import org.ahpuh.backend.user.service.UserServiceImpl;
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

    @PostMapping(path = "/user/login")
    public UserLoginResponseDto login(@RequestBody final UserLoginRequestDto request) {
        final JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(), request.getPassword());
        final Authentication resultToken = authenticationManager.authenticate(authToken);
        final JwtAuthentication authentication = (JwtAuthentication) resultToken.getPrincipal();
        final User user = (User) resultToken.getDetails();
        return new UserLoginResponseDto(authentication.token, user.getUserId());
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
