package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class MakeFavoriteFailException extends ApplicationException {

    public MakeFavoriteFailException() {
        super(ExceptionType.MAKE_FAVORITE_FAIL.getHttpStatus(), ExceptionType.MAKE_FAVORITE_FAIL.getDetail());
    }
}
