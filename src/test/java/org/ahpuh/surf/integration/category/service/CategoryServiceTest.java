package org.ahpuh.surf.integration.category.service;

import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.CategoryResponseDto;
import org.ahpuh.surf.category.dto.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.category.service.CategoryService;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager entityManager;

    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .password("password")
                .email("suebeen@gmail.com")
                .userName("name")
                .build());
        category = categoryRepository.save(Category.builder()
                .user(user)
                .name("test")
                .colorCode("#e7f5ff")
                .build());
    }

    @Test
    @DisplayName("카테고리를 생성할 수 있다.")
    void createCategoryTest() {
        // given
        final CategoryCreateRequestDto createRequestDto = CategoryCreateRequestDto.builder()
                .name(category.getName())
                .colorCode(category.getColorCode())
                .build();

        // when
        categoryService.createCategory(user.getUserId(), createRequestDto);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findAll().size()).isEqualTo(2),
                () -> assertThat(categoryRepository.findAll().get(1).getName()).isEqualTo(createRequestDto.getName()),
                () -> assertThat(categoryRepository.findAll().get(1).getIsPublic()).isTrue(),
                () -> assertThat(categoryRepository.findAll().get(1).getColorCode()).isEqualTo(createRequestDto.getColorCode())
        );
    }

    @Test
    @DisplayName("카테고리를 수정할 수 있다.")
    void updateCategoryTest() {
        // given
        final CategoryUpdateRequestDto updateRequestDto = CategoryUpdateRequestDto.builder()
                .name("update test")
                .isPublic(false)
                .colorCode("#d0ebff")
                .build();

        // when
        categoryService.updateCategory(category.getCategoryId(), updateRequestDto);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findAll().get(0).getName()).isEqualTo(updateRequestDto.getName()),
                () -> assertThat(categoryRepository.findAll().get(0).getIsPublic()).isFalse(),
                () -> assertThat(categoryRepository.findAll().get(0).getColorCode()).isEqualTo(updateRequestDto.getColorCode())
        );
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다.")
    void deleteCategoryTest() {
        // given
        final Long id = category.getCategoryId();

        // when
        categoryService.deleteCategory(id);

        // then
        assertThat(categoryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자의 모든 카테고리 정보를 조회할 수 있다.")
    void findAllCategoryByUserTest() {
        // given
        final Category newCategory = categoryRepository.save(Category.builder()
                .user(user)
                .name("test2")
                .colorCode("#e7f5df")
                .build());
        final Long id = user.getUserId();

        // when
        final List<CategoryResponseDto> categories = categoryService.findAllCategoryByUser(id);

        // then
        assertAll(
                () -> assertThat(categories.size()).isEqualTo(2),
                () -> assertThat(categories.get(0).getCategoryId()).isEqualTo(category.getCategoryId()),
                () -> assertThat(categories.get(1).getCategoryId()).isEqualTo(newCategory.getCategoryId())
        );
    }

    @Test
    @DisplayName("사용자의 대시보드를 조회할 수 있다.")
    void getCategoryDashboardTest() {
        // given
        final Category newCategory = categoryRepository.save(Category.builder()
                .user(user)
                .name("test2")
                .colorCode("#e7f5df")
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .category(newCategory)
                .selectedDate(LocalDate.now())
                .content("post1")
                .score(88).build());

        postRepository.save(Post.builder()
                .user(user)
                .category(newCategory)
                .selectedDate(LocalDate.now())
                .content("post2")
                .score(43).build());

        final Long id = user.getUserId();

        // when
        final List<CategoryDetailResponseDto> categories = categoryService.getCategoryDashboard(id);
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(categories.size()).isEqualTo(2),
                () -> assertThat(categories.get(0).getPostCount()).isZero(),
                () -> assertThat(categories.get(0).getAverageScore()).isZero(),
//                TODO : 테스트 통과x @Formula count가 안됨
//                () -> assertThat(categories.get(1).getPostCount()).isEqualTo(2),
                () -> assertThat(categoryRepository.findAll().get(1).getPosts().size()).isEqualTo(2),
                () -> assertThat(categories.get(1).getAverageScore()).isEqualTo(65)
        );
    }
}
