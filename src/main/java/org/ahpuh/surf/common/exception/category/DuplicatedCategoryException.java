package org.ahpuh.surf.common.exception.category;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class DuplicatedCategoryException extends ApplicationException {

    public DuplicatedCategoryException() {
        super(ExceptionType.DUPLICATED_CATEGORY.getHttpStatus(), ExceptionType.DUPLICATED_CATEGORY.getDetail());
    }
}
