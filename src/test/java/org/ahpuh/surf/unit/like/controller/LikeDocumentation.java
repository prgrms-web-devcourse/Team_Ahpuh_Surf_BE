package org.ahpuh.surf.unit.like.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class LikeDocumentation {

    protected static RestDocumentationResultHandler like() {
        return document("like/like",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ),
                responseFields(
                        fieldWithPath("likeId").type(JsonFieldType.NUMBER).description("좋아요 id")
                ));
    }

    protected static RestDocumentationResultHandler unlike() {
        return document("like/unlike",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id"),
                        parameterWithName("likeId").description("좋아요 id")
                ));
    }
}
