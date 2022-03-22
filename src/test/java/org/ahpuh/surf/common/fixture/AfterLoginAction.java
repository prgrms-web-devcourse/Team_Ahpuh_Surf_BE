package org.ahpuh.surf.common.fixture;

import java.io.File;
import java.util.Objects;

public class AfterLoginAction {

    public final TUser user;
    private UserAction userAction = null;
    private CategoryAction categoryAction = null;
    private PostAction postAction = null;
    private FollowAction followAction = null;

    AfterLoginAction(final TUser user) {
        this.user = user;
    }

    public void 유저조회_요청(final Long userId) {
        userAction().유저조회_요청(userId);
    }

    public void 유저정보_수정_요청_With_File(final File file) {
        userAction().유저정보_수정_요청_With_File(file);
    }

    public void 유저정보_수정_요청_No_File() {
        userAction().유저정보_수정_요청_No_File();
    }

    public void 회원탈퇴_요청() {
        userAction().회원탈퇴_요청();
    }

    public void 카테고리_생성_요청() {
        categoryAction().카테고리_생성_요청();
    }

    public void 카테고리_생성_요청_name(final String categoryName) {
        categoryAction().카테고리_생성_요청_name(categoryName);
    }

    public void 카테고리_생성_요청_colorCode(final String colorCode) {
        categoryAction().카테고리_생성_요청_colorCode(colorCode);
    }

    public AfterLoginAction 카테고리_생성_완료() {
        categoryAction().카테고리_생성_요청();
        return this;
    }

    public void 카테고리_수정_요청() {
        categoryAction().카테고리_수정_요청();
    }

    public void 카테고리_수정_요청_name(final String categoryName) {
        categoryAction().카테고리_수정_요청_name(categoryName);
    }

    public void 카테고리_수정_요청_colorCode(final String colorCode) {
        categoryAction().카테고리_수정_요청_colorCode(colorCode);
    }

    public void 카테고리_삭제_요청() {
        categoryAction().카테고리_삭제_요청();
    }

    public void 내_모든_카테고리_조회_요청() {
        categoryAction().내_모든_카테고리_조회_요청();
    }

    public void 내_모든_카테고리_각각의_게시글_개수_및_평균점수_조회() {
        categoryAction().내_모든_카테고리_각각의_게시글_개수_및_평균점수_조회();
    }

    public AfterLoginAction 게시글_생성_완료(final int postScore) {
        postAction().게시글_생성_완료(postScore);
        return this;
    }

    private UserAction userAction() {
        if (Objects.isNull(userAction)) {
            userAction = new UserAction(user);
        }
        return userAction;
    }

    private CategoryAction categoryAction() {
        if (Objects.isNull(categoryAction)) {
            categoryAction = new CategoryAction(user);
        }
        return categoryAction;
    }

    private PostAction postAction() {
        if (Objects.isNull(postAction)) {
            postAction = new PostAction(user);
        }
        return postAction;
    }
}
