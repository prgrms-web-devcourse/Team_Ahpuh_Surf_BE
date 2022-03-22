package org.ahpuh.surf.acceptance.like;

import org.ahpuh.surf.acceptance.AcceptanceTest;
import org.ahpuh.surf.common.fixture.AfterLoginAction;
import org.ahpuh.surf.like.dto.response.LikeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.ahpuh.surf.common.fixture.TUser.USER_1;
import static org.ahpuh.surf.common.fixture.TUser.USER_2;

public class LikeAcceptanceTest extends AcceptanceTest {

    @DisplayName("게시글 좋아요")
    @Nested
    class 게시글_좋아요 {

        @Test
        void 게시글_좋아요_성공() {
            // Given
            USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            USER_2.로그인_완료().게시글_좋아요_요청(1L);

            // Then
            USER_2.response.statusCode(200);
        }
    }

    @DisplayName("게시글 좋아요 취소")
    @Nested
    class 게시글_좋아요_취소 {

        @Test
        void 게시글_좋아요_취소_성공() {
            // Given
            USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();
            final Long postId = 1L;
            
            final AfterLoginAction action = USER_2.로그인_완료()
                    .게시글_좋아요_완료(postId);
            final Long likeId = USER_2.response.statusCode(200)
                    .extract().as(LikeResponseDto.class)
                    .getLikeId();

            // When
            action.게시글_좋아요_취소_요청(postId, likeId);

            // Then
            USER_2.response.statusCode(204);
        }
    }
}
