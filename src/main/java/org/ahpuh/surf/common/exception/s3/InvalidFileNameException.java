package org.ahpuh.surf.common.exception.s3;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class InvalidFileNameException extends ApplicationException {

    public InvalidFileNameException() {
        super(ExceptionType.INVALID_FILENAME.getHttpStatus(), ExceptionType.INVALID_FILENAME.getDetail());
    }
}
