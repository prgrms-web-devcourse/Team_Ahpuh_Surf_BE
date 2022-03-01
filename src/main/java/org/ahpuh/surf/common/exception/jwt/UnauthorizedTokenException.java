package org.ahpuh.surf.common.exception.jwt;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class UnauthorizedTokenException extends ApplicationException {

    public UnauthorizedTokenException() {
        super(ExceptionType.UNAUTHORIZED_TOKEN.getHttpStatus(), ExceptionType.UNAUTHORIZED_TOKEN.getDetail());
    }
}
