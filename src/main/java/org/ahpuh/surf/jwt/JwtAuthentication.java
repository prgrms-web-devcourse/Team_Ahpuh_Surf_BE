package org.ahpuh.surf.jwt;

import org.ahpuh.surf.common.exception.jwt.TokenNotFoundException;
import org.ahpuh.surf.common.exception.jwt.UserInformationNotFoundException;

import java.util.Objects;

public class JwtAuthentication {

    public final String token;
    public final Long userId;
    public final String email;

    public JwtAuthentication(final String token, final Long userId, final String email) {
        if (Objects.isNull(token))
            throw new TokenNotFoundException();
        if (Objects.isNull(userId))
            throw new UserInformationNotFoundException();
        if (Objects.isNull(email))
            throw new UserInformationNotFoundException();

        this.token = token;
        this.userId = userId;
        this.email = email;
    }
}
