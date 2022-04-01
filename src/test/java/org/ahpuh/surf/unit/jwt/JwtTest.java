package org.ahpuh.surf.unit.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.ahpuh.surf.common.exception.jwt.InvalidTokenException;
import org.ahpuh.surf.jwt.Claims;
import org.ahpuh.surf.jwt.Jwt;
import org.ahpuh.surf.user.domain.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class JwtTest {

    private Jwt JWT;
    private final Long USERID = 1L;
    private final String EMAIL = "email@naver.com";
    private final String[] ROLES = new String[]{Permission.ROLE_ADMIN.getRole()};

    @BeforeEach
    void setup() {
        JWT = new Jwt("cse0518", "clientSecret", 1000);

        assertAll("Jwt 생성 확인",
                () -> assertThat(JWT.getIssuer()).isEqualTo("cse0518"),
                () -> assertThat(JWT.getClientSecret()).isEqualTo("clientSecret"),
                () -> assertThat(JWT.getExpirySeconds()).isEqualTo(1000),
                () -> assertThat(JWT.getAlgorithm()).isNotNull(),
                () -> assertThat(JWT.getJwtVerifier()).isNotNull()
        );
    }

    @DisplayName("Claims를 encode하여 token을 생성할 수 있다.")
    @Test
    void signClaimsTest() {
        // Given
        final Claims claims = Claims.from(USERID, EMAIL, ROLES);

        // When
        final String token = JWT.sign(claims);

        // Then
        final JWTVerifier jwtVerifier = com.auth0.jwt.JWT.require(Algorithm.HMAC512("clientSecret"))
                .withIssuer("cse0518")
                .build();
        final Claims decodedJwt = new Claims(jwtVerifier.verify(token));

        assertAll("Token 생성 확인",
                () -> assertThat(decodedJwt.getUserId()).isEqualTo(USERID),
                () -> assertThat(decodedJwt.getEmail()).isEqualTo(EMAIL),
                () -> assertThat(decodedJwt.getRoles()).isEqualTo(ROLES),
                () -> assertThat(decodedJwt.getExp().getTime() - decodedJwt.getIat().getTime()).isEqualTo(1000 * 1_000L)
        );
    }

    @DisplayName("decode 된 jwt 토큰을 검증하여 Claims로 변환할 수 있다.")
    @Test
    void verifyTokenTest() {
        // Given
        final String token = JWT.sign(Claims.from(USERID, EMAIL, ROLES));

        // When
        final Claims claims = JWT.verify(token);

        // Then
        assertAll("claims 생성 확인",
                () -> assertThat(claims.getUserId()).isEqualTo(USERID),
                () -> assertThat(claims.getEmail()).isEqualTo(EMAIL),
                () -> assertThat(claims.getRoles()).isEqualTo(ROLES)
        );
    }

    @DisplayName("clientSecret이 일치하지 않는 jwt 토큰은 예외가 발생한다.")
    @Test
    void verifyInvalidTokenTest() {
        // Given
        final Jwt invalidJwt = new Jwt("cse0518", "invalid", 1000);
        final String token = invalidJwt.sign(Claims.from(USERID, EMAIL, ROLES));

        // When Then
        assertThatThrownBy(() -> JWT.verify(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("서버에서 발급한 토큰이 아닙니다.");
    }
}
