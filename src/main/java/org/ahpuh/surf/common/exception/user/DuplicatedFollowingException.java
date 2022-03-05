package org.ahpuh.surf.common.exception.user;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class DuplicatedFollowingException extends ApplicationException {

    public DuplicatedFollowingException() {
        super(ExceptionType.DUPLICATED_FOLLOWING.getHttpStatus(), ExceptionType.DUPLICATED_FOLLOWING.getDetail());
    }
}
