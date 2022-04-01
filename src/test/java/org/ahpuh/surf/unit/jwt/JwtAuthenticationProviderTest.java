package org.ahpuh.surf.unit.jwt;

import org.ahpuh.surf.jwt.Jwt;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.jwt.JwtAuthenticationProvider;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.ahpuh.surf.common.factory.MockUserFactory.createSavedUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationProviderTest {

    @Mock
    private Jwt jwt;

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @DisplayName("로그인 인증을 성공하고 Authentication 객체를 반환한다.")
    @Test
    void authenticateTest() {
        // Given
        final User user = createSavedUser();
        given(userService.login("cse0518", "password"))
                .willReturn(user);
        given(jwt.sign(any()))
                .willReturn("token");

        // When
        final Authentication authentication
                = jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken("cse0518", "password"));

        // Then
        final JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
        assertThat(jwtToken.getPrincipal()).isInstanceOf(JwtAuthentication.class);

        final JwtAuthentication jwtAuthentication = (JwtAuthentication) jwtToken.getPrincipal();
        assertAll(
                () -> assertThat(jwtAuthentication.userId).isEqualTo(user.getUserId()),
                () -> assertThat(jwtAuthentication.email).isEqualTo(user.getEmail()),
                () -> assertThat(jwtToken.getCredentials()).isNull()
        );
    }
}
