package org.ahpuh.backend.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
public class Jwt {

    private final String issuer; // 토큰 발급자

    private final String clientSecret; // 토큰 키 해시 값

    private final int expirySeconds; // 만료시간

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier; // JWT 검증자

    public Jwt(final String issuer, final String clientSecret, final int expirySeconds) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.expirySeconds = expirySeconds;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String sign(final Claims claims) {
        final Date now = new Date();
        final JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
        builder.withIssuer(issuer);
        builder.withIssuedAt(now);
        if (expirySeconds > 0) {
            builder.withExpiresAt(new Date(now.getTime() + expirySeconds * 1_000L));
        }
        builder.withClaim("user_id", claims.userId);
        builder.withClaim("email", claims.email);
        builder.withArrayClaim("roles", claims.roles);
        return builder.sign(algorithm);
    }

    public Claims verify(final String token) throws JWTVerificationException {
        return new Claims(jwtVerifier.verify(token));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static public class Claims {

        Long userId;
        String email;
        String[] roles;
        Date iat; // 토큰 발급 시각
        Date exp; // 만료시간이 지난 토큰은 사용불가

        Claims(final DecodedJWT decodedJWT) {
            final Claim userId = decodedJWT.getClaim("user_id");
            if (!userId.isNull())
                this.userId = userId.asLong();
            final Claim email = decodedJWT.getClaim("email");
            if (!email.isNull())
                this.email = email.asString();
            final Claim roles = decodedJWT.getClaim("roles");
            if (!roles.isNull()) {
                this.roles = roles.asArray(String.class);
            }
            this.iat = decodedJWT.getIssuedAt();
            this.exp = decodedJWT.getExpiresAt();
        }

        public static Claims from(final Long userId, final String email, final String[] roles) {
            final Claims claims = new Claims();
            claims.userId = userId;
            claims.email = email;
            claims.roles = roles;
            return claims;
        }

        long iat() {
            return iat != null ? iat.getTime() : -1;
        }

        long exp() {
            return exp != null ? exp.getTime() : -1;
        }

    }

}
