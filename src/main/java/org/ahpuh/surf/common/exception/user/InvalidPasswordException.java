package org.ahpuh.surf.common.exception.user;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class InvalidPasswordException extends ApplicationException {

    public InvalidPasswordException() {
        super(ExceptionType.INVALID_PASSWORD.getHttpStatus(), ExceptionType.INVALID_PASSWORD.getDetail());
    }
}
