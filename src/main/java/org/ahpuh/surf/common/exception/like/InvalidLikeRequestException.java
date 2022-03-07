package org.ahpuh.surf.common.exception.like;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class InvalidLikeRequestException extends ApplicationException {

    public InvalidLikeRequestException() {
        super(ExceptionType.INVALID_LIKE_REQUEST.getHttpStatus(), ExceptionType.INVALID_LIKE_REQUEST.getDetail());
    }
}
