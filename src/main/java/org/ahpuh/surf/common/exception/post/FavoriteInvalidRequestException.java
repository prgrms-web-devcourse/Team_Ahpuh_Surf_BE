package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class FavoriteInvalidRequestException extends ApplicationException {

    public FavoriteInvalidRequestException() {
        super(ExceptionType.FAVORITE_INVALID_REQUEST.getHttpStatus(), ExceptionType.FAVORITE_INVALID_REQUEST.getDetail());
    }
}
