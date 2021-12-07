package org.ahpuh.backend.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.backend.post.dto.PostDto;
import org.ahpuh.backend.post.dto.PostIdResponse;
import org.ahpuh.backend.post.dto.PostRequest;
import org.ahpuh.backend.post.service.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostController postController;

    @MockBean
    private PostServiceImpl postService;

    private Long postId;
    private String postUrl;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        postId = 1L;
        postUrl = "/api/v1/posts";
    }

    @Test
    @DisplayName("post 생성")
    void createPost() throws Exception {
        // given
        final PostRequest postRequest = PostRequest.builder()
                .categoryId(1L)
                .selectedDate("2021-12-06")
                .content("ah-puh")
                .score(50)
                .build();
        final String requestBody = objectMapper.writeValueAsString(postRequest);

        given(postService.create(any(PostRequest.class)))
                .willReturn(new PostIdResponse(postId));

        // when
        final ResultActions resultActions = mockMvc.perform(post(postUrl)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpectAll(
                status().isCreated(),
                header().string(LOCATION, postUrl + "/" + postId),
                jsonPath("statusCode").value(201),
                jsonPath("data").isNotEmpty(),
                jsonPath("data.id").value(postId)
        );
    }

    @Test
    @DisplayName("post 수정")
    void updatePost() throws Exception {
        // given
        final PostRequest postRequest = PostRequest.builder()
                .categoryId(1L)
                .selectedDate("2021-12-06")
                .content("ah-puh")
                .score(100)
                .build();
        final String requestBody = objectMapper.writeValueAsString(postRequest);

        given(postService.update(anyLong(), any(PostRequest.class)))
                .willReturn(new PostIdResponse(postId));

        // when
        final ResultActions resultActions = mockMvc.perform(put(postUrl + "/{postId}", postId)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpectAll(
                status().isOk(),
                jsonPath("statusCode").value(200),
                jsonPath("data").isNotEmpty(),
                jsonPath("data.id").value(postId)
        );
    }

    @Test
    @DisplayName("post 단건 조회")
    void getPost() throws Exception {
        // given
        final PostDto postDto = PostDto.builder()
                .postId(postId)
                .categoryId(1L)
                .selectedDate("2021-12-06")
                .content("surf")
                .score(80)
                .build();
        given(postService.readOne(anyLong()))
                .willReturn(postDto);

        // when
        final ResultActions resultActions = mockMvc.perform(get(postUrl + "/{postId}", postId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpectAll(
                status().isOk(),
                jsonPath("statusCode").value(200),
                jsonPath("data").isNotEmpty(),
                jsonPath("data.postId").value(postId),
                jsonPath("data.content").value("surf")
        );
    }

    @Test
    @DisplayName("post 삭제")
    void deletePost() throws Exception {
        // given

        // when
        final ResultActions resultActions = mockMvc.perform(delete(postUrl + "/{postId}", postId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpectAll(
                status().isOk(),
                jsonPath("statusCode").value(204),
                jsonPath("data").isEmpty()
        );
    }

}
