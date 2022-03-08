package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class InvalidPeriodException extends ApplicationException {

    public InvalidPeriodException() {
        super(ExceptionType.INVALID_PERIOD.getHttpStatus(), ExceptionType.INVALID_PERIOD.getDetail());
    }
}
