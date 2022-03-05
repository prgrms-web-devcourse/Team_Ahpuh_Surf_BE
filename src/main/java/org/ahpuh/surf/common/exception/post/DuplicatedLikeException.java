package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class DuplicatedLikeException extends ApplicationException {

    public DuplicatedLikeException() {
        super(ExceptionType.DUPLICATED_LIKE.getHttpStatus(), ExceptionType.DUPLICATED_LIKE.getDetail());
    }
}
