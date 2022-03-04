package org.ahpuh.surf.common.exception.category;

import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.ExceptionType;

public class CategoryNotFoundException extends ApplicationException {

    public CategoryNotFoundException() {
        super(ExceptionType.CATEGORY_NOT_FOUND.getHttpStatus(), ExceptionType.CATEGORY_NOT_FOUND.getDetail());
    }
}
