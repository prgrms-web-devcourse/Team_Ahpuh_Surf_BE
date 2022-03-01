package org.ahpuh.surf.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String detail;

    ExceptionType(final HttpStatus httpStatus, final String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}
