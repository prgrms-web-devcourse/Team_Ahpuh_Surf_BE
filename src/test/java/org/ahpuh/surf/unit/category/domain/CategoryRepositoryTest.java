package org.ahpuh.surf.unit.category.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.repository.CategoryRepository;
import org.ahpuh.surf.config.QuerydslConfig;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(QuerydslConfig.class)
@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("save 메소드는")
    @Nested
    class SaveMethod {

        @DisplayName("카테고리를 등록할 수 있다.")
        @Test
        void saveCategory_Success() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category = createMockCategory(user);

            // When
            categoryRepository.save(category);

            // Then
            final List<Category> categories = categoryRepository.findAll();
            assertAll("카테고리 등록 테스트",
                    () -> assertThat(categories.size()).isEqualTo(1),
                    () -> assertThat(categories.get(0).getName()).isEqualTo("categoryName")
            );
        }

        @DisplayName("카테고리를 등록하면 해당 유저와 연관관계가 매핑된다.")
        @Test
        void categoryUserMappingTest() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category = createMockCategory(user);

            // When
            categoryRepository.save(category);

            // Then
            final List<Category> categories = categoryRepository.findAll();
            assertAll("연관관계 매핑 테스트",
                    () -> assertThat(categories.size()).isEqualTo(1),
                    () -> assertThat(categories.get(0).getUser()).isEqualTo(user)
            );
        }
    }

    @DisplayName("findById 메소드는")
    @Nested
    class FindByIdMethod {

        @DisplayName("categoryId로 조회할 수 있다.")
        @Test
        void findById_Success() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category = createMockCategory(user);
            final Long categoryId = testEntityManager.persist(category).getCategoryId();

            // When
            final Optional<Category> findedCategory = categoryRepository.findById(categoryId);

            // Then
            assertAll(
                    () -> assertThat(findedCategory).isNotEmpty(),
                    () -> assertThat(findedCategory.get()).isSameAs(category)
            );
        }

        @DisplayName("존재하지 않는 id로 조회할 수 없다.")
        @Test
        void findById_Fail() {
            // When
            final Optional<Category> findedCategory = categoryRepository.findById(1L);

            // Then
            assertThat(findedCategory).isEmpty();
        }
    }

    @DisplayName("findByUser 메소드는")
    @Nested
    class FindByUserMethod {

        @DisplayName("해당 유저의 모든 카테고리를 조회할 수 있다.")
        @Test
        void findAllCategoryByUser() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category1 = Category.builder()
                    .user(user)
                    .name("category1")
                    .colorCode("000000")
                    .build();
            final Category category2 = Category.builder()
                    .user(user)
                    .name("category2")
                    .colorCode("000000")
                    .build();
            testEntityManager.persist(category1);
            testEntityManager.persist(category2);

            // When
            final List<Category> allCategoryByUser = categoryRepository.findByUser(user);

            // Then
            assertAll(
                    () -> assertThat(allCategoryByUser.size()).isEqualTo(2),
                    () -> assertThat(allCategoryByUser.get(0).getName()).isEqualTo("category1"),
                    () -> assertThat(allCategoryByUser.get(1).getName()).isEqualTo("category2")
            );
        }

        @DisplayName("해당 유저의 카테고리가 없는 경우 빈 리스트를 반환한다.")
        @Test
        void findByUser_NoCategory_ReturnEmptyList() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);

            // When
            final List<Category> allCategoryByUser = categoryRepository.findByUser(user);

            // Then
            assertThat(allCategoryByUser).isEqualTo(List.of());
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("카테고리가 삭제되고 orphanRemoval가 동작한다.")
        @Test
        void deleteAllMapping() {
            // Given
            final User user = userRepository.save(createMockUser());
            final Category category = categoryRepository.save(createMockCategory(user));
            postRepository.save(createMockPost(user, category));
            postRepository.save(createMockPost(user, category));

            testEntityManager.clear();

            final List<User> allUser = userRepository.findAll();
            final List<Category> allCategory = categoryRepository.findAll();
            final List<Post> allPost = postRepository.findAll();

            assertAll("user, category, post save 확인",
                    () -> assertThat(allUser.size()).isEqualTo(1),
                    () -> assertThat(allCategory.size()).isEqualTo(1),
                    () -> assertThat(allPost.size()).isEqualTo(2)
            );

            // When
            final Category categoryEntity = categoryRepository.getById(category.getCategoryId());
            categoryRepository.delete(categoryEntity);
            testEntityManager.flush();
            testEntityManager.clear();

            // Then
            assertAll("category와 모든 post가 orphan remove",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(0),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(0)
            );
        }
    }
}
