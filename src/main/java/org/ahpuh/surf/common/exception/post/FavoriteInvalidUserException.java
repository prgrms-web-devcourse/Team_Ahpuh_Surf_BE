package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class FavoriteInvalidUserException extends ApplicationException {

    public FavoriteInvalidUserException() {
        super(ExceptionType.FAVORITE_INVALID_USER.getHttpStatus(), ExceptionType.FAVORITE_INVALID_USER.getDetail());
    }
}
