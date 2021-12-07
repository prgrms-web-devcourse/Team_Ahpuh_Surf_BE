package org.ahpuh.surf.category.service;

import org.ahpuh.surf.category.converter.CategoryConverter;
import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
        CategoryCreateRequestDto dto = CategoryCreateRequestDto.builder()
                .name(category.getName())
                .colorCode(category.getColorCode())
                .build();

        // when
        categoryService.createCategory(dto);

        // then
        assertThat(categoryRepository.findAll().size(), is(2));
        assertThat(categoryRepository.findAll().get(1).getName(), is("test"));
        assertThat(categoryRepository.findAll().get(1).isPublic(), is(true));
        assertThat(categoryRepository.findAll().get(1).getColorCode(), is("#e7f5ff"));
        assertThat(categoryRepository.findAll().get(1).getIsDeleted(), is(false));
    }

    @Test
    @DisplayName("카테고리를 수정할 수 있다.")
    void updateCategoryTest() {
        // given
        CategoryUpdateRequestDto dto = CategoryUpdateRequestDto.builder()
                .name("update test")
                .isPublic(false)
                .colorCode("#d0ebff")
                .build();

        // when
        categoryService.updateCategory(category.getId(), dto);

        // then
        assertThat(categoryRepository.findAll().get(0).getName(), is("update test"));
        assertThat(categoryRepository.findAll().get(0).isPublic(), is(false));
        assertThat(categoryRepository.findAll().get(0).getColorCode(), is("#d0ebff"));
        assertThat(categoryRepository.findAll().get(0).getIsDeleted(), is(false));
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다.")
    void deleteCategoryTest() {
        // given
        Long id = category.getId();

        // when
        categoryService.deleteCategory(id);

        // then
        assertThat(categoryRepository.findAll().size(), is(1));
        assertThat(categoryRepository.findAll().get(0).getIsDeleted(), is(true));
    }
}
