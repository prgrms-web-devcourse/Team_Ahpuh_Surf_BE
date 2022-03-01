package org.ahpuh.surf.jwt;

import org.ahpuh.surf.common.exception.jwt.TokenNotFoundException;
import org.ahpuh.surf.common.exception.jwt.UserInformationNotFoundException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class JwtAuthentication {

    public final String token;
    public final Long userId;
    public final String email;

    public JwtAuthentication(final String token, final Long userId, final String email) {
        if (!isNotEmpty(token))
            throw new TokenNotFoundException();
        if (userId == null)
            throw new UserInformationNotFoundException();
        if (!isNotEmpty(email))
            throw new UserInformationNotFoundException();

        this.token = token;
        this.userId = userId;
        this.email = email;
    }
}
