package org.ahpuh.surf.common.exception.follow;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class FollowNotFoundException extends ApplicationException {

    public FollowNotFoundException() {
        super(ExceptionType.FOLLOW_NOT_FOUND.getHttpStatus(), ExceptionType.FOLLOW_NOT_FOUND.getDetail());
    }
}
