package org.ahpuh.surf.common.exception.s3;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class UploadFailException extends ApplicationException {

    public UploadFailException() {
        super(ExceptionType.UPLOAD_FAIL.getHttpStatus(), ExceptionType.UPLOAD_FAIL.getDetail());
    }
}
