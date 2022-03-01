package org.ahpuh.surf.common.exception.user;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException() {
        super(ExceptionType.USER_NOT_FOUND.getHttpStatus(), ExceptionType.USER_NOT_FOUND.getDetail());
    }
}
