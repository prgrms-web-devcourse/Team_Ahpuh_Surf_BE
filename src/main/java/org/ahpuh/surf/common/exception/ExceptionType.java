package org.ahpuh.surf.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    // JWT
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰이 없습니다."),
    UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "인증되지 않은 토큰입니다."),
    USER_INFORMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보가 없습니다."),

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),

    // FOLLOW
    DUPLICATED_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우 한 사용자입니다."),

    // CATEGORY
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    DUPLICATED_CATEGORY(HttpStatus.BAD_REQUEST, "이미 등록된 카테고리입니다."),

    // POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    DUPLICATED_POST(HttpStatus.BAD_REQUEST, "이미 등록된 게시글입니다."),
    FAVORITE_INVALID_USER(HttpStatus.BAD_REQUEST, "즐겨찾기를 등록 또는 취소할 수 없습니다.(내 게시글만 등록 가능)"),
    FAVORITE_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "이미 실행된 요청입니다."),

    // LIKE
    DUPLICATED_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다.");

    private final HttpStatus httpStatus;
    private final String detail;

    ExceptionType(final HttpStatus httpStatus, final String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}
