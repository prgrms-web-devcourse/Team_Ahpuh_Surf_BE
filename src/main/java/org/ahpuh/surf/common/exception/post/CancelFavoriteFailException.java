package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class CancelFavoriteFailException extends ApplicationException {

    public CancelFavoriteFailException() {
        super(ExceptionType.CANCEL_FAVORITE_FAIL.getHttpStatus(), ExceptionType.CANCEL_FAVORITE_FAIL.getDetail());
    }
}
