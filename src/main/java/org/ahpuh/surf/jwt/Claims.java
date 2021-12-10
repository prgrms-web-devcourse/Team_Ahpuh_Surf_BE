package org.ahpuh.surf.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Claims {

    public Long userId;

    public String email;

    public String[] roles;

    public Date iat;

    public Date exp;

    public Claims(final DecodedJWT decodedJWT) {
        final Claim userId = decodedJWT.getClaim("user_id");
        if (!userId.isNull()) {
            this.userId = userId.asLong();
        }

        final Claim email = decodedJWT.getClaim("email");
        if (!email.isNull()) {
            this.email = email.asString();
        }

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

}
