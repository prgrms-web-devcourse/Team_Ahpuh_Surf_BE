package org.ahpuh.surf.common.exception.s3;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class InvalidExtensionException extends ApplicationException {

    public InvalidExtensionException() {
        super(ExceptionType.INVALID_EXTENSION.getHttpStatus(), ExceptionType.INVALID_EXTENSION.getDetail());
    }
}
