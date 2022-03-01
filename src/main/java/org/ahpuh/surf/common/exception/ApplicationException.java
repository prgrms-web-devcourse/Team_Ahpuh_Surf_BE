package org.ahpuh.surf.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;

    protected ApplicationException(final HttpStatus httpStatus, final String detail) {
        super(detail);
        this.httpStatus = httpStatus;
    }
}
