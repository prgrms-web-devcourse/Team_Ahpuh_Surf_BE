package org.ahpuh.surf.unit.jwt;

import org.ahpuh.surf.common.exception.jwt.TokenNotFoundException;
import org.ahpuh.surf.common.exception.jwt.UserInformationNotFoundException;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtAuthenticationTest {

    @DisplayName("토큰이 null인 경우 예외가 발생한다.")
    @Test
    void tokenNotFoundException() {
        // When Then
        assertThatThrownBy(() -> new JwtAuthentication(null, 1L, "email@naver.com"))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessage("토큰이 없습니다.");
    }

    @DisplayName("userId이 null인 경우 예외가 발생한다.")
    @Test
    void userInformationNotFoundException_userId() {
        // When Then
        assertThatThrownBy(() -> new JwtAuthentication("token", null, "email@naver.com"))
                .isInstanceOf(UserInformationNotFoundException.class)
                .hasMessage("유저 정보가 없습니다.");
    }

    @DisplayName("email이 null인 경우 예외가 발생한다.")
    @Test
    void userInformationNotFoundException_email() {
        // When Then
        assertThatThrownBy(() -> new JwtAuthentication("token", 1L, null))
                .isInstanceOf(UserInformationNotFoundException.class)
                .hasMessage("유저 정보가 없습니다.");
    }
}
