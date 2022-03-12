package org.ahpuh.surf.unit.follow.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class FollowDocumentation {

    protected static RestDocumentationResultHandler follow() {
        return document("follow/follow",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                requestFields(
                        fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("내가 팔로우 하려는 유저의 id")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("location")
                ),
                responseFields(
                        fieldWithPath("followId").type(JsonFieldType.NUMBER).description("팔로우 id")
                ));
    }

    protected static RestDocumentationResultHandler unfollow() {
        return document("follow/unfollow",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("userId").description("유저 id")
                ));
    }

    protected static RestDocumentationResultHandler findFollowersList() {
        return document("follow/findFollowersList",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("userId").description("유저 id")
                ),
                responseFields(
                        fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("팔로워 id")
                                .optional(),
                        fieldWithPath("[].userName").type(JsonFieldType.STRING).description("팔로워 이름")
                                .optional(),
                        fieldWithPath("[].profilePhotoUrl").type(JsonFieldType.STRING).description("팔로워 프로필 이미지 주소")
                                .optional()
                ));
    }

    protected static RestDocumentationResultHandler findFollowingList() {
        return document("follow/findFollowingList",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("userId").description("유저 id")
                ),
                responseFields(
                        fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("팔로잉한 유저 id")
                                .optional(),
                        fieldWithPath("[].userName").type(JsonFieldType.STRING).description("팔로잉한 유저 이름")
                                .optional(),
                        fieldWithPath("[].profilePhotoUrl").type(JsonFieldType.STRING).description("팔로잉한 유저 프로필 이미지 주소")
                                .optional()
                ));
    }
}
