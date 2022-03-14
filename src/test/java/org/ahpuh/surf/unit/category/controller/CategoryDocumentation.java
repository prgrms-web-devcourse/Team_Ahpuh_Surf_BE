package org.ahpuh.surf.unit.category.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class CategoryDocumentation {

    protected static RestDocumentationResultHandler create() {
        return document("category/create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리명"),
                        fieldWithPath("colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("location")
                ));
    }

    protected static RestDocumentationResultHandler update() {
        return document("category/update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("categoryId").description("카테고리 id")
                ),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리명"),
                        fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("카테고리 공개여부"),
                        fieldWithPath("colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                ));
    }

    protected static RestDocumentationResultHandler delete() {
        return document("category/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("categoryId").description("카테고리 id")
                ));
    }

    protected static RestDocumentationResultHandler findAll() {
        return document("category/findAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                responseFields(
                        fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 id")
                                .optional(),
                        fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리명")
                                .optional(),
                        fieldWithPath("[].isPublic").type(JsonFieldType.BOOLEAN).description("카테고리 공개여부")
                                .optional(),
                        fieldWithPath("[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                .optional()
                ));
    }

    protected static RestDocumentationResultHandler getDashboardInfo() {
        return document("category/getDashboardInfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                requestParameters(
                        parameterWithName("userId").description("유저 id")
                ),
                responseFields(
                        fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 id")
                                .optional(),
                        fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리명")
                                .optional(),
                        fieldWithPath("[].isPublic").type(JsonFieldType.BOOLEAN).description("카테고리 공개여부")
                                .optional(),
                        fieldWithPath("[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                .optional(),
                        fieldWithPath("[].averageScore").type(JsonFieldType.NUMBER).description("해당 카테고리의 게시글 평균점수")
                                .optional(),
                        fieldWithPath("[].postCount").type(JsonFieldType.NUMBER).description("해당 카테고리의 게시글 갯수")
                                .optional()
                ));
    }
}
