package org.ahpuh.surf.common.fixture;

import java.io.File;
import java.util.Objects;

public class AfterLoginAction {

    public final TUser user;
    private UserAction userAction = null;
    private CategoryAction categoryAction = null;
    private PostAction postAction = null;
    private FollowAction followAction = null;
    private LikeAction likeAction = null;

    AfterLoginAction(final TUser user) {
        this.user = user;
    }

    /**
     * UserAction
     */
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

    /**
     * CategoryAction
     */
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

    /**
     * PostAction
     */
    public void 게시글_생성_요청_With_File(final File file) {
        postAction().게시글_생성_요청_With_File(file);
    }

    public void 게시글_생성_요청_No_File() {
        postAction().게시글_생성_요청_No_File();
    }

    public void 게시글_생성_요청_selectedDate(final String selectedDate) {
        postAction().게시글_생성_요청_selectedDate(selectedDate);
    }

    public void 게시글_생성_요청_content(final String content) {
        postAction().게시글_생성_요청_content(content);
    }

    public void 게시글_생성_요청_score(final int score) {
        postAction().게시글_생성_요청_score(score);
    }

    public AfterLoginAction 게시글_생성_완료() {
        postAction().게시글_생성_요청_No_File();
        return this;
    }

    public AfterLoginAction 게시글_생성_완료_selectedDate(final String selectedDate) {
        postAction().게시글_생성_요청_selectedDate(selectedDate);
        return this;
    }

    public AfterLoginAction 게시글_생성_완료_score(final int postScore) {
        postAction().게시글_생성_요청_score(postScore);
        return this;
    }

    public void 게시글_수정_요청_With_File(final File file) {
        postAction().게시글_수정_요청_With_File(file);
    }

    public void 게시글_수정_요청_No_File() {
        postAction().게시글_수정_요청_No_File();
    }

    public void 게시글_수정_요청_selectedDate(final String selectedDate) {
        postAction().게시글_수정_요청_selectedDate(selectedDate);
    }

    public void 게시글_수정_요청_content(final String content) {
        postAction().게시글_수정_요청_content(content);
    }

    public void 게시글_수정_요청_score(final int score) {
        postAction().게시글_수정_요청_score(score);
    }

    public void 게시글_조회_요청() {
        postAction().게시글_조회_요청();
    }

    public void 게시글_삭제_요청() {
        postAction().게시글_삭제_요청();
    }

    public void 즐겨찾기_추가_요청() {
        postAction().즐겨찾기_추가_요청();
    }

    public AfterLoginAction 즐겨찾기_추가_완료() {
        postAction().즐겨찾기_추가_요청();
        return this;
    }

    public void 즐겨찾기_삭제_요청() {
        postAction().즐겨찾기_삭제_요청();
    }

    public void 특정_Month_모든_게시글_조회_요청(final int year, final int month) {
        postAction().특정_Month_모든_게시글_조회_요청(year, month);
    }

    public void 해당_카테고리의_최신_게시글_점수_조회_요청() {
        postAction().해당_카테고리의_최신_게시글_점수_조회_요청();
    }

    public void 특정_Year의_날짜별_게시글_개수_조회_요청(final int year) {
        postAction().특정_Year의_날짜별_게시글_개수_조회_요청(year);
    }

    public void 해당_유저의_카테고리별_게시글_점수_조회_요청() {
        postAction().해당_유저의_카테고리별_게시글_점수_조회_요청();
    }

    public void 전체_최신_게시글_둘러보기_요청() {
        postAction().전체_최신_게시글_둘러보기_요청();
    }

    public void 내가_팔로우한_유저의_게시글_둘러보기_요청() {
        postAction().내가_팔로우한_유저의_게시글_둘러보기_요청();
    }

    public void 해당_유저의_전체_게시글_조회_요청(final TUser user) {
        postAction().해당_유저의_전체_게시글_조회_요청(user);
    }

    public void 해당_카테고리의_전체_게시글_조회_요청(final Long categoryId) {
        postAction().해당_카테고리의_전체_게시글_조회_요청(categoryId);
    }

    /**
     * FollowAction
     */
    public void 팔로우_요청(final TUser target) {
        followAction().팔로우_요청(target);
    }

    public AfterLoginAction 팔로우_완료(final TUser target) {
        팔로우_요청(target);
        return this;
    }

    public void 언팔로우_요청(final TUser target) {
        followAction().언팔로우_요청(target);
    }

    public void 해당_유저의_팔로워_조회_요청() {
        followAction().해당_유저의_팔로워_조회_요청();
    }

    public void 해당_유저가_팔로잉한_유저_조회_요청() {
        followAction().해당_유저가_팔로잉한_유저_조회_요청();
    }

    /**
     * LikeAction
     */
    public void 게시글_좋아요_요청(final Long postId) {
        likeAction().게시글_좋아요_요청(postId);
    }

    public AfterLoginAction 게시글_좋아요_완료(final Long postId) {
        게시글_좋아요_요청(postId);
        return this;
    }

    public void 게시글_좋아요_취소_요청(final Long postId, final Long likeId) {
        likeAction().게시글_좋아요_취소_요청(postId, likeId);
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

    private FollowAction followAction() {
        if (Objects.isNull(followAction)) {
            followAction = new FollowAction(user);
        }
        return followAction;
    }

    private LikeAction likeAction() {
        if (Objects.isNull(likeAction)) {
            likeAction = new LikeAction(user);
        }
        return likeAction;
    }
}
