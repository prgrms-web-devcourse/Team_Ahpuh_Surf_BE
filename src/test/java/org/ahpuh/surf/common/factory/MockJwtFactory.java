package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;

public class MockJwtFactory {

    public static JwtAuthenticationToken createJwtToken(final Long userId, final String Email) {
        return new JwtAuthenticationToken(
                new JwtAuthentication("testToken", userId, Email),
                null,
                null);
    }
}
