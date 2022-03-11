package org.ahpuh.surf.unit.post.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.exception.like.DuplicatedLikeException;
import org.ahpuh.surf.common.exception.post.FavoriteInvalidUserException;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.domain.FileType;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLike;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.ahpuh.surf.common.factory.MockUserFactory.createSavedUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PostTest {

    @DisplayName("게시글 정보를 수정할 수 있다.")
    @Test
    void updatePostTest() {
        // Given
        final User user = createMockUser();
        final Category category = createMockCategory(user);
        final Post post = createMockPost(user, category);

        // When
        post.updatePost(category, LocalDate.of(2022, 1, 1), "update", 50);

        // Then
        assertAll("게시글 정보 수정 테스트",
                () -> assertThat(post.getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 1)),
                () -> assertThat(post.getContent()).isEqualTo("update"),
                () -> assertThat(post.getScore()).isEqualTo(50)
        );
    }

    @DisplayName("updateFile 메소드는")
    @Nested
    class UpdateFileMethod {

        @DisplayName("이미지가 들어오면 imageUrl에 파일주소를 저장하고, fileUrl을 비운다.")
        @Test
        void imageFileTest() {
            // Given
            final User user = createMockUser();
            final Category category = createMockCategory(user);
            final Post post = createMockPost(user, category);
            final FileStatus fileStatus = new FileStatus("updateImageUrl", FileType.IMAGE);

            // When
            post.updateFile(fileStatus);

            // Then
            assertAll("이미지 업로드시",
                    () -> assertThat(post.getImageUrl()).isEqualTo("updateImageUrl"),
                    () -> assertThat(post.getFileUrl()).isNull()
            );
        }

        @DisplayName("파일이 들어오면 fileUrl에 파일주소를 저장하고, imageUrl을 비운다.")
        @Test
        void fileTest() {
            // Given
            final User user = createMockUser();
            final Category category = createMockCategory(user);
            final Post post = createMockPost(user, category);
            final FileStatus fileStatus = new FileStatus("updateUrl", FileType.FILE);

            // When
            post.updateFile(fileStatus);

            // Then
            assertAll("파일 업로드시",
                    () -> assertThat(post.getFileUrl()).isEqualTo("updateUrl"),
                    () -> assertThat(post.getImageUrl()).isNull()
            );
        }
    }

    @DisplayName("updateFavorite 메소드는")
    @Nested
    class UpdateFavoriteMethod {

        @DisplayName("해당 게시글을 작성한 유저가 즐겨찾기에 등록할 수 있다.")
        @Test
        void updateFavorite() {
            // Given
            final User user = createSavedUser();
            final Category category = createMockCategory(user);
            final Post post = createMockPost(user, category);
            assertThat(post.getFavorite()).isFalse();

            // When
            post.updateFavorite(user.getUserId());

            // Then
            assertThat(post.getFavorite()).isTrue();
        }

        @DisplayName("해당 게시글을 작성한 유저가 아닐 경우 예외가 발생한다.")
        @Test
        void favoriteUpdateFailException() {
            // Given
            final User user = createSavedUser();
            final Category category = createMockCategory(user);
            final Post post = createMockPost(user, category);
            assertThat(post.getFavorite()).isFalse();

            // When Then
            assertThatThrownBy(() -> post.updateFavorite(100L))
                    .isInstanceOf(FavoriteInvalidUserException.class)
                    .hasMessage("즐겨찾기를 등록 또는 취소할 수 없습니다.(내 게시글만 등록 가능)");
        }
    }

    @DisplayName("addLike 메소드는 이미 좋아요를 누른 게시글을 다시 좋아요하면 예외가 발생한다.")
    @Test
    void duplicatedLikeException() {
        // Given
        final User user = createMockUser();
        final Category category = createMockCategory(user);
        final Post post = createMockPost(user, category);
        final Like like = createMockLike(user, post);

        // When Then
        assertThatThrownBy(() -> post.addLike(like))
                .isInstanceOf(DuplicatedLikeException.class)
                .hasMessage("이미 좋아요를 누른 게시글입니다.");
    }
}
