package org.ahpuh.surf.category.service;

import org.ahpuh.surf.category.converter.CategoryConverter;
import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryConverter categoryConverter;

    Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .name("test")
                .isPublic(true)
                .colorCode("#e7f5ff")
                .build();
        categoryRepository.save(category);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
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
        categoryService.createCategory(createRequestDto);

        // then
        assertAll(
                () -> Assertions.assertThat(categoryRepository.findAll().size()).isEqualTo(2),
                () -> Assertions.assertThat(categoryRepository.findAll().get(1).getName()).isEqualTo(createRequestDto.getName()),
                () -> Assertions.assertThat(categoryRepository.findAll().get(1).isPublic()).isTrue(),
                () -> Assertions.assertThat(categoryRepository.findAll().get(1).getColorCode()).isEqualTo(createRequestDto.getColorCode())
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
        categoryService.updateCategory(category.getId(), updateRequestDto);

        // then
        assertAll(
                () -> Assertions.assertThat(categoryRepository.findAll().get(0).getName()).isEqualTo(updateRequestDto.getName()),
                () -> Assertions.assertThat(categoryRepository.findAll().get(0).isPublic()).isFalse(),
                () -> Assertions.assertThat(categoryRepository.findAll().get(0).getColorCode()).isEqualTo(updateRequestDto.getColorCode())
        );
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다.")
    void deleteCategoryTest() {
        // given
        final Long id = category.getId();

        // when
        categoryService.deleteCategory(id);

        // then
        assertThat(categoryRepository.findAll().size(), is(0));
    }
}
