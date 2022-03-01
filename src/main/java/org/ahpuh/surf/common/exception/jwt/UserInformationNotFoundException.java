package org.ahpuh.surf.common.exception.jwt;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class UserInformationNotFoundException extends ApplicationException {

    public UserInformationNotFoundException() {
        super(ExceptionType.USER_INFORMATION_NOT_FOUND.getHttpStatus(), ExceptionType.USER_INFORMATION_NOT_FOUND.getDetail());
    }
}
