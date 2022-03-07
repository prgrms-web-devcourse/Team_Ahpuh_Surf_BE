package org.ahpuh.surf.common.exception.category;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class NoCategoryFromUserException extends ApplicationException {

    public NoCategoryFromUserException() {
        super(ExceptionType.NO_CATEGORY_FROM_USER.getHttpStatus(), ExceptionType.NO_CATEGORY_FROM_USER.getDetail());
    }
}
