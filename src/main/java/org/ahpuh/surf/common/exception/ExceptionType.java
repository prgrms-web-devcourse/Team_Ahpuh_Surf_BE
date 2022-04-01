package org.ahpuh.surf.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    // JWT
    USER_INFORMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보가 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰이 없습니다."),
    CANNOT_SET_TOKEN(HttpStatus.BAD_REQUEST, "토큰 상태를 변경할 수 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "서버에서 발급한 토큰이 아닙니다."),

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),

    // FOLLOW
    DUPLICATED_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우 한 사용자입니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 한 기록이 없습니다."),

    // CATEGORY
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    DUPLICATED_CATEGORY(HttpStatus.BAD_REQUEST, "이미 등록된 카테고리입니다."),
    NO_CATEGORY_FROM_USER(HttpStatus.BAD_REQUEST, "해당 유저의 카테고리가 아닙니다."),

    // POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    DUPLICATED_POST(HttpStatus.BAD_REQUEST, "이미 등록된 게시글입니다."),
    NOT_MATCHING_POST_BY_USER(HttpStatus.BAD_REQUEST, "다른 사용자의 게시글을 삭제할 수 없습니다."),
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "기간이 잘못 입력되었습니다."),
    FAVORITE_INVALID_USER(HttpStatus.BAD_REQUEST, "즐겨찾기를 등록 또는 취소할 수 없습니다.(내 게시글만 등록 가능)"),
    MAKE_FAVORITE_FAIL(HttpStatus.BAD_REQUEST, "이미 즐겨찾기에 추가된 게시글입니다."),
    CANCEL_FAVORITE_FAIL(HttpStatus.BAD_REQUEST, "이미 즐겨찾기 취소된 게시글입니다."),

    // LIKE
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 한 기록이 없습니다."),
    DUPLICATED_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다."),
    INVALID_LIKE_REQUEST(HttpStatus.BAD_REQUEST, "해당 게시글에 대한 좋아요 기록이 아닙니다."),

    // S3
    INVALID_FILENAME(HttpStatus.BAD_REQUEST, "파일 이름이 없습니다."),
    INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "파일의 확장자가 잘못되었습니다."),
    UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String detail;

    ExceptionType(final HttpStatus httpStatus, final String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}
