package org.ahpuh.backend.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class JwtAuthentication {

    public final String token;

    public final String email;

    public JwtAuthentication(final String token, final String email) {
        checkArgument(isNotEmpty(token), "token must be provided.");
        checkArgument(isNotEmpty(email), "email must be provided.");

        this.token = token;
        this.email = email;
    }

}
