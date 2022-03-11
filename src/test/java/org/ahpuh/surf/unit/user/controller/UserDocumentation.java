package org.ahpuh.surf.unit.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class UserDocumentation {

    protected static RestDocumentationResultHandler join() {
        return document("user/join",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                        fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("location")
                ));
    }

    protected static RestDocumentationResultHandler login() {
        return document("user/login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                ),
                responseFields(
                        fieldWithPath("token").type(JsonFieldType.STRING).description("토큰"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id")
                ));
    }

    protected static RestDocumentationResultHandler findUserInfo() {
        return document("user/findUserInfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("userId").description("유저 id")
                ),
                responseFields(
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                        fieldWithPath("profilePhotoUrl").type(JsonFieldType.STRING).description("유저 프로필사진 url 주소")
                                .optional(),
                        fieldWithPath("aboutMe").type(JsonFieldType.STRING).description("유저 자기소개 문구")
                                .optional(),
                        fieldWithPath("url").type(JsonFieldType.STRING).description("유저 컨택 url 주소")
                                .optional(),
                        fieldWithPath("followerCount").type(JsonFieldType.NUMBER).description("팔로워 수"),
                        fieldWithPath("followingCount").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                        fieldWithPath("accountPublic").type(JsonFieldType.BOOLEAN).description("계정 공개여부")
                ));
    }

    protected static RestDocumentationResultHandler updateUserWithImage() {
        return document("user/updateUserWithImage",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                requestPartBody("file"),
                requestPartFields("request",
                        fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                        fieldWithPath("url").type(JsonFieldType.STRING).optional()
                                .description("유저 컨택 url 주소"),
                        fieldWithPath("aboutMe").type(JsonFieldType.STRING).optional()
                                .description("유저 자기소개 문구"),
                        fieldWithPath("accountPublic").type(JsonFieldType.BOOLEAN).description("계정 공개여부")
                ));
    }

    protected static RestDocumentationResultHandler updateUserWithOnlyDto() {
        return document("user/updateUserWithOnlyDto",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                requestPartFields("request",
                        fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                        fieldWithPath("url").type(JsonFieldType.STRING).optional()
                                .description("유저 컨택 url 주소"),
                        fieldWithPath("aboutMe").type(JsonFieldType.STRING).optional()
                                .description("유저 자기소개 문구"),
                        fieldWithPath("accountPublic").type(JsonFieldType.BOOLEAN).description("계정 공개여부")
                ));
    }

    protected static RestDocumentationResultHandler deleteUser() {
        return document("user/deleteUser",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ));
    }
}
