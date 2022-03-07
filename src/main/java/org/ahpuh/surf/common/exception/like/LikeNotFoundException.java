package org.ahpuh.surf.common.exception.like;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class LikeNotFoundException extends ApplicationException {

    public LikeNotFoundException() {
        super(ExceptionType.LIKE_NOT_FOUND.getHttpStatus(), ExceptionType.LIKE_NOT_FOUND.getDetail());
    }
}
