package org.ahpuh.surf.common.exception.jwt;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class InvalidTokenException extends ApplicationException {

    public InvalidTokenException() {
        super(ExceptionType.INVALID_TOKEN.getHttpStatus(), ExceptionType.INVALID_TOKEN.getDetail());
    }
}
