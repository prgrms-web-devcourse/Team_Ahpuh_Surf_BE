package org.ahpuh.surf.common.exception.jwt;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class TokenNotFoundException extends ApplicationException {

    public TokenNotFoundException() {
        super(ExceptionType.TOKEN_NOT_FOUND.getHttpStatus(), ExceptionType.TOKEN_NOT_FOUND.getDetail());
    }
}
