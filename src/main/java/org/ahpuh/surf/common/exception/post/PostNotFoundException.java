package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class PostNotFoundException extends ApplicationException {

    public PostNotFoundException() {
        super(ExceptionType.POST_NOT_FOUND.getHttpStatus(), ExceptionType.POST_NOT_FOUND.getDetail());
    }
}
