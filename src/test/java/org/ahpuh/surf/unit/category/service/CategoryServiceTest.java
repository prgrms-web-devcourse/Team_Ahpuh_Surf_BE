package org.ahpuh.surf.unit.category.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryConverter;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.service.CategoryService;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryConverter categoryConverter;

    @InjectMocks
    private CategoryService categoryService;

    @DisplayName("createCategory 메소드는")
    @Nested
    class CreateCategoryMethod {

        @DisplayName("카테고리를 생성할 수 있다.")
        @Test
        void createCategory_Success() {
            // Given
            final User mockUser = mock(User.class);
            final Category mockCategory = mock(Category.class);
            final Long userId = 1L;
            final CategoryCreateRequestDto requestDto = mock(CategoryCreateRequestDto.class);
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(categoryConverter.toEntity(mockUser, requestDto))
                    .willReturn(mockCategory);
            given(categoryRepository.save(mockCategory))
                    .willReturn(mockCategory);
            given(mockCategory.getCategoryId())
                    .willReturn(1L);

            // When
            final Long categoryId = categoryService.createCategory(userId, requestDto);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryConverter, times(1))
                    .toEntity(any(User.class), any(CategoryCreateRequestDto.class));
            verify(categoryRepository, times(1))
                    .save(any(Category.class));
            assertThat(categoryId).isEqualTo(1L);
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() {
            // Given
            final CategoryCreateRequestDto requestDto = mock(CategoryCreateRequestDto.class);
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> categoryService.createCategory(1L, requestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryConverter, times(0))
                    .toEntity(any(User.class), any(CategoryCreateRequestDto.class));
            verify(categoryRepository, times(0))
                    .save(any(Category.class));
        }
    }

    @DisplayName("updateCategory 메소드는")
    @Nested
    class UpdateCategoryMethod {

        @DisplayName("카테고리 이름, 색깔정보, 공개여부를 수정한다.")
        @Test
        void updateCategory_Success() {
            // Given
            final Category mockCategory = mock(Category.class);
            final Long categoryId = 1L;
            final CategoryUpdateRequestDto requestDto = mock(CategoryUpdateRequestDto.class);
            given(categoryRepository.findById(categoryId))
                    .willReturn(Optional.of(mockCategory));

            // When
            categoryService.updateCategory(categoryId, requestDto);

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(mockCategory, times(1))
                    .update(any(), anyBoolean(), any());
        }

        @DisplayName("존재하지 않는 카테고리 아이디가 입력되면 예외가 발생한다.")
        @Test
        void categoryNotFoundException() {
            // Given
            final CategoryUpdateRequestDto requestDto = mock(CategoryUpdateRequestDto.class);
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> categoryService.updateCategory(1L, requestDto))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage("해당 카테고리를 찾을 수 없습니다.");

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
        }
    }

    @DisplayName("deleteCategory 메소드는")
    @Nested
    class DeleteCategoryMethod {

        @DisplayName("카테고리를 삭제한다.")
        @Test
        void deleteCategory_Success() {
            // Given
            final Category mockCategory = mock(Category.class);
            final Long categoryId = 1L;
            given(categoryRepository.findById(categoryId))
                    .willReturn(Optional.of(mockCategory));

            // When
            categoryService.deleteCategory(categoryId);

            // Then
            verify(categoryRepository, times(1))
                    .findById(categoryId);
            verify(categoryRepository, times(1))
                    .delete(any(Category.class));
        }

        @DisplayName("존재하지 않는 카테고리 아이디가 입력되면 예외가 발생한다.")
        @Test
        void categoryNotFoundException() {
            // Given
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage("해당 카테고리를 찾을 수 없습니다.");

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
        }
    }

    @DisplayName("findAllCategoryByUser 메소드는")
    @Nested
    class FindAllCategoryByUserMethod {

        @DisplayName("해당 유저의 모든 카테고리를 반환한다.")
        @Test
        void findAllCategoryByUser_Success() {
            // Given
            final User mockUser = mock(User.class);
            final Category mockCategory1 = mock(Category.class);
            final Category mockCategory2 = mock(Category.class);
            final Long userId = 1L;
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(categoryRepository.findByUser(mockUser))
                    .willReturn(List.of(mockCategory1, mockCategory2));
            given(categoryConverter.toCategoryResponseDto(any(Category.class)))
                    .willReturn(mock(AllCategoryByUserResponseDto.class));

            // When
            categoryService.findAllCategoryByUser(userId);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(1))
                    .findByUser(any(User.class));
            verify(categoryConverter, times(2))
                    .toCategoryResponseDto(any(Category.class));
        }

        @DisplayName("해당 유저의 카테고리가 없을 경우 빈 배열을 반환한다.")
        @Test
        void noCategoryByUser_ReturnEmptyList() {
            // Given
            final User mockUser = mock(User.class);
            final Long userId = 1L;
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(categoryRepository.findByUser(mockUser))
                    .willReturn(List.of());

            // When
            final List<AllCategoryByUserResponseDto> responseDtos = categoryService.findAllCategoryByUser(userId);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(1))
                    .findByUser(any(User.class));
            verify(categoryConverter, times(0))
                    .toCategoryResponseDto(any(Category.class));
            assertThat(responseDtos).isEqualTo(List.of());
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() {
            // Given
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> categoryService.findAllCategoryByUser(1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(0))
                    .findByUser(any(User.class));
            verify(categoryConverter, times(0))
                    .toCategoryResponseDto(any(Category.class));
        }
    }

    @DisplayName("getCategoryDashboard 메소드는")
    @Nested
    class GetCategoryDashboardMethod {

        @DisplayName("대시보드에 표시할 모든 카테고리의 게시글 평균점수와 개수를 반환한다.")
        @Test
        void getCategoryDashboard_Success() {
            // Given
            final User mockUser = mock(User.class);
            final Category mockCategory = mock(Category.class);
            final Post post = createMockPost(mockUser, mockCategory);
            final Long userId = 1L;
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(categoryRepository.findByUser(mockUser))
                    .willReturn(List.of(mockCategory, mockCategory));
            given(categoryConverter.toCategoryDetailResponseDto(any(Category.class), anyInt()))
                    .willReturn(mock(CategoryDetailResponseDto.class));

            // When
            categoryService.getCategoryDashboard(userId);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(1))
                    .findByUser(any(User.class));
            verify(categoryConverter, times(2))
                    .toCategoryDetailResponseDto(any(), isA(Integer.class));
        }

        @DisplayName("해당 유저의 카테고리가 없을 경우 빈 배열을 반환한다.")
        @Test
        void noCategoryByUser_ReturnEmptyList() {
            // Given
            final User mockUser = mock(User.class);
            final Long userId = 1L;
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(categoryRepository.findByUser(mockUser))
                    .willReturn(List.of());

            // When
            final List<CategoryDetailResponseDto> responseDtos = categoryService.getCategoryDashboard(userId);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(1))
                    .findByUser(any(User.class));
            verify(categoryConverter, times(0))
                    .toCategoryDetailResponseDto(any(Category.class), isA(Integer.class));
            assertThat(responseDtos).isEqualTo(List.of());
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() {
            // Given
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> categoryService.getCategoryDashboard(1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(0))
                    .findByUser(any());
            verify(categoryConverter, times(0))
                    .toCategoryDetailResponseDto(any(), isA(Integer.class));
        }
    }

    @DisplayName("getAverageScore 메소드는")
    @Nested
    class GetAverageScoreMethod {

        @DisplayName("카테고리의 모든 게시글의 평균점수를 반환한다.")
        @Test
        void getAverageScore() {
            // Given
            final User user = mock(User.class);
            final Category category = mock(Category.class);
            final Post post1 = new Post(user, category, null, null, 95);
            final Post post2 = new Post(user, category, null, null, 85);
            given(category.getPosts())
                    .willReturn(List.of(post1, post2));

            // When
            final double average = category.getPosts()
                    .stream()
                    .mapToInt(Post::getScore)
                    .average()
                    .orElse(0);

            // Then
            assertThat(average).isEqualTo(90D);
        }

        @DisplayName("카테고리의 게시글이 없는 경우 0을 반환한다.")
        @Test
        void getAverageScore_NoPost_ReturnZero() {
            // Given
            final Category category = mock(Category.class);
            given(category.getPosts())
                    .willReturn(List.of());

            // When
            final double average = category.getPosts()
                    .stream()
                    .mapToInt(Post::getScore)
                    .average()
                    .orElse(0);

            // Then
            assertThat(average).isEqualTo(0);
        }
    }
}
