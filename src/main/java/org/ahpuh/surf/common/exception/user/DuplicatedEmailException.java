package org.ahpuh.surf.common.exception.user;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class DuplicatedEmailException extends ApplicationException {
    
    public DuplicatedEmailException() {
        super(ExceptionType.DUPLICATED_EMAIL.getHttpStatus(), ExceptionType.DUPLICATED_EMAIL.getDetail());
    }
}
