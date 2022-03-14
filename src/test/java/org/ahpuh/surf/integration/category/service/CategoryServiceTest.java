package org.ahpuh.surf.integration.category.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.service.CategoryService;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.integration.IntegrationTest;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.*;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPostWithScore;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryServiceTest extends IntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("카테고리 생성 테스트")
    @Nested
    class CreateCategoryTest {

        @DisplayName("유저는 카테고리를 생성할 수 있다.")
        @Test
        void createCategorySuccess() {
            // Given
            entityManager.persist(createMockUser());
            final User user = userRepository.findAll().get(0);
            final CategoryCreateRequestDto request = createMockCategoryCreateRequestDto();

            // When
            final Long categoryId = categoryService.createCategory(user.getUserId(), request);

            // Then
            assertThat(categoryId).isNotNull();
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다 - 404 응답")
        @Test
        void userNotFoundException_404() {
            // Given
            final CategoryCreateRequestDto request = createMockCategoryCreateRequestDto();

            // When Then
            assertThatThrownBy(() -> categoryService.createCategory(1L, request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }
    }

    @DisplayName("카테고리 수정 테스트")
    @Nested
    class UpdateCategoryTest {

        @DisplayName("유저는 카테고리 정보를 수정할 수 있다.")
        @Test
        void updateCategorySuccess() {
            // Given
            final User user = createMockUser();
            entityManager.persist(user);
            entityManager.persist(createMockCategory(user));
            final Category category = categoryRepository.findAll().get(0);
            final CategoryUpdateRequestDto request = createMockCategoryUpdateRequestDto();

            assertThat(category.getName()).isEqualTo("categoryName");

            // When
            categoryService.updateCategory(category.getCategoryId(), request);

            // Then
            final Category findedCategory = categoryRepository.getById(category.getCategoryId());
            assertThat(findedCategory.getName()).isEqualTo("update");
        }

        @DisplayName("존재하지 않는 카테고리 아이디가 입력되면 예외가 발생한다 - 404 응답")
        @Test
        void categoryNotFoundException_404() {
            // Given
            final CategoryUpdateRequestDto request = createMockCategoryUpdateRequestDto();

            // When Then
            assertThatThrownBy(() -> categoryService.updateCategory(1L, request))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 카테고리를 찾을 수 없습니다.");
        }
    }

    @DisplayName("카테고리 삭제 테스트")
    @Nested
    class DeleteCategoryTest {

        @DisplayName("카테고리를 삭제할 수 있다.")
        @Test
        void deleteCategorySuccess() {
            // Given
            final User user = createMockUser();
            entityManager.persist(user);
            entityManager.persist(createMockCategory(user));

            final List<Category> allCategory = categoryRepository.findAll();
            assertThat(allCategory.size()).isEqualTo(1);

            final Category category = allCategory.get(0);

            // When
            categoryService.deleteCategory(category.getCategoryId());

            // Then
            assertThat(categoryRepository.findAll().size()).isEqualTo(0);
        }
    }

    @DisplayName("해당 유저의 모든 카테고리 조회 테스트")
    @Nested
    class FindAllCategoryByUserTest {

        @DisplayName("해당 유저의 모든 카테고리를 조회할 수 있다.")
        @Test
        void findAllCategoryByUserSuccess() {
            // Given
            entityManager.persist(createMockUser());
            final User user = userRepository.findAll().get(0);

            entityManager.persist(createMockCategory(user));
            entityManager.persist(createMockCategory(user));
            final List<Category> allCategory = categoryRepository.findAll();
            assertThat(allCategory.size()).isEqualTo(2);

            // When
            final List<AllCategoryByUserResponseDto> response = categoryService.findAllCategoryByUser(user.getUserId());

            // Then
            assertThat(response.size()).isEqualTo(2);
        }

        @DisplayName("해당 유저의 카테고리가 없을 경우 빈 배열을 반환한다.")
        @Test
        void noCategoryReturnEmptyList() {
            // Given
            entityManager.persist(createMockUser());
            final User user = userRepository.findAll().get(0);

            // When
            final List<AllCategoryByUserResponseDto> response = categoryService.findAllCategoryByUser(user.getUserId());

            // Then
            assertThat(response).isEqualTo(List.of());
        }
    }

    @DisplayName("나의 카테고리 전체 조회 테스트")
    @Nested
    class GetCategoryDashboardTest {

        @DisplayName("나의 대시보드에 표시할 모든 카테고리의 게시글 평균점수와 개수를 반환할 수 있다.")
        @Test
        void getCategoryDashboardSuccess() {
            // Given
            entityManager.persist(createMockUser());
            final User user = userRepository.findAll().get(0);

            entityManager.persist(createMockCategory(user));
            entityManager.persist(createMockCategory(user));
            final List<Category> allCategory = categoryRepository.findAll();

            entityManager.persist(
                    createMockPostWithScore(user, allCategory.get(0), 100));
            entityManager.persist(
                    createMockPostWithScore(user, allCategory.get(0), 90));
            entityManager.persist(
                    createMockPostWithScore(user, allCategory.get(1), 80));
            
            entityManager.flush();
            entityManager.clear();

            // When
            final List<CategoryDetailResponseDto> response = categoryService.getCategoryDashboard(user.getUserId());

            // Then
            assertAll("카테고리1 -> 게시글 2개(100점, 90점), 카테고리2 -> 게시글 1개(80점)",
                    () -> assertThat(response.size()).isEqualTo(2),
                    () -> assertThat(response.get(0).getPostCount()).isEqualTo(2),
                    () -> assertThat(response.get(0).getAverageScore()).isEqualTo(95),
                    () -> assertThat(response.get(1).getPostCount()).isEqualTo(1),
                    () -> assertThat(response.get(1).getAverageScore()).isEqualTo(80)
            );
        }

        @DisplayName("해당 유저의 카테고리가 없을 경우 빈 배열을 반환한다.")
        @Test
        void noCategoryReturnEmptyList() {
            // Given
            entityManager.persist(createMockUser());
            final User user = userRepository.findAll().get(0);

            // When
            final List<CategoryDetailResponseDto> response = categoryService.getCategoryDashboard(user.getUserId());

            // Then
            assertThat(response).isEqualTo(List.of());
        }
    }
}
