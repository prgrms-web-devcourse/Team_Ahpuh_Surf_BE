package org.ahpuh.surf.unit.jwt;

import org.ahpuh.surf.common.exception.jwt.CannotSetTokenException;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.user.domain.Permission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtAuthenticationTokenTest {

    @DisplayName("isAuthenticated를 false로 초기화할 수 있다.")
    @Test
    void setAuthenticatedTest() {
        // Given
        final JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken("cse0518", "password");

        // When
        jwtAuthenticationToken.setAuthenticated(false);

        // Then
        assertThat(jwtAuthenticationToken.isAuthenticated()).isFalse();
    }

    @DisplayName("isAuthenticated는 생성자를 통해서만 true로 setting 가능하다.")
    @Test
    void cannotSetTokenException() {
        // Given
        final JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(
                "token",
                null,
                List.of(new SimpleGrantedAuthority(Permission.ROLE_ADMIN.getRole())));

        // When Then
        assertThatThrownBy(() -> jwtAuthenticationToken.setAuthenticated(true))
                .isInstanceOf(CannotSetTokenException.class)
                .hasMessage("토큰 상태를 변경할 수 없습니다.");
    }

    @DisplayName("인증된 이후에 credentials를 지울 수 있다.")
    @Test
    void eraseCredentialsTest() {
        // Given
        final JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken("cse0518", "password");
        assertThat(jwtAuthenticationToken.getCredentials()).isEqualTo("password");

        // When
        jwtAuthenticationToken.eraseCredentials();

        // Then
        assertThat(jwtAuthenticationToken.getCredentials()).isNull();
    }
}
