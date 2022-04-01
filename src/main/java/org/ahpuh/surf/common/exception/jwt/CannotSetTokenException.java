package org.ahpuh.surf.common.exception.jwt;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class CannotSetTokenException extends ApplicationException {

    public CannotSetTokenException() {
        super(ExceptionType.CANNOT_SET_TOKEN.getHttpStatus(), ExceptionType.CANNOT_SET_TOKEN.getDetail());
    }
}
