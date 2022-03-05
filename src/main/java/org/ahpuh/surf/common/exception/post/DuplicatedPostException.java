package org.ahpuh.surf.common.exception.post;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class DuplicatedPostException extends ApplicationException {

    public DuplicatedPostException() {
        super(ExceptionType.DUPLICATED_POST.getHttpStatus(), ExceptionType.DUPLICATED_POST.getDetail());
    }
}
