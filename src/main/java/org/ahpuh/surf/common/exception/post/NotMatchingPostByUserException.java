package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class NotMatchingPostByUserException extends ApplicationException {

    public NotMatchingPostByUserException() {
        super(ExceptionType.NOT_MATCHING_POST_BY_USER.getHttpStatus(), ExceptionType.NOT_MATCHING_POST_BY_USER.getDetail());
    }
}
