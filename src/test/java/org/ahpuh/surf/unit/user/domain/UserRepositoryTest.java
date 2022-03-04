package org.ahpuh.surf.unit.user.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(QuerydslConfig.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("save 메소드는")
    @Nested
    class SaveMethod {

        @DisplayName("유저를 등록할 수 있다.")
        @Test
        void saveUser_Success() {
            // Given
            final User mockUser = createMockUser();

            // When
            userRepository.save(mockUser);

            // Then
            final List<User> users = userRepository.findAll();
            assertAll("유저 등록 테스트",
                    () -> assertThat(users.size()).isEqualTo(1),
                    () -> assertThat(users.get(0).getUserName()).isEqualTo("mock")
            );
        }

        @DisplayName("유저 생성시 username, email, password는 null을 허용하지 않는다.")
        @Test
        void saveUserNullCheck() {
            assertAll("null check",
                    () -> assertThrows(DataIntegrityViolationException.class,
                            () -> userRepository.save(createMockUser(null))),
                    () -> assertThrows(DataIntegrityViolationException.class,
                            () -> userRepository.save(createMockUser("email", null, "name"))),
                    () -> assertThrows(DataIntegrityViolationException.class,
                            () -> userRepository.save(createMockUser("email", "pw", null)))
            );
        }

        @DisplayName("유저 이메일은 중복이 불가능하다.")
        @Test
        void uniqueEmailCheck() {
            // Given
            final User mockUser1 = createMockUser();
            final User mockUser2 = createMockUser();
            testEntityManager.persist(mockUser1);

            // When Then
            assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(mockUser2));
        }
    }

    @DisplayName("findById 메소드는")
    @Nested
    class FindByIdMethod {

        @DisplayName("userId로 조회할 수 있다.")
        @Test
        void findById_Success() {
            // Given
            final User mockUser = createMockUser();
            final User persistUser = testEntityManager.persist(mockUser);

            // When
            final Optional<User> findedUser = userRepository.findById(persistUser.getUserId());

            // Then
            assertAll(
                    () -> assertThat(findedUser).isNotEmpty(),
                    () -> assertThat(findedUser.get()).isSameAs(mockUser)
            );
        }

        @DisplayName("존재하지 않는 id로 조회할 수 없다.")
        @Test
        void findById_Fail() {
            // When
            final Optional<User> findedUser = userRepository.findById(1L);

            // Then
            assertThat(findedUser).isEmpty();
        }
    }

    @DisplayName("findByEmail 메소드는")
    @Nested
    class FindByEmailMethod {

        @DisplayName("이메일로 유저를 조회할 수 있다.")
        @Test
        void findUserByEmail_Success() {
            // Given
            final User mockUser = createMockUser();
            testEntityManager.persist(mockUser);

            // When
            final Optional<User> user = userRepository.findByEmail(mockUser.getEmail());

            // Then
            assertThat(user.get()).isSameAs(mockUser);
        }

        @DisplayName("존재하지 않는 이메일로 유저를 조회할 수 없다.")
        @Test
        void findUserByEmail_Fail() {
            // When
            final Optional<User> user = userRepository.findByEmail("invalid email");

            // Then
            assertThat(user).isEmpty();
        }
    }

    @DisplayName("existsByEmail 메소드는")
    @Nested
    class ExistsByEmailMethod {

        @DisplayName("등록된 이메일의 유저가 있는 경우 true를 반환한다.")
        @Test
        void validEmailExistCheck_ValidUser() {
            // Given
            final User mockUser = createMockUser();
            testEntityManager.persist(mockUser);

            // When
            final Boolean existsByEmail = userRepository.existsByEmail(mockUser.getEmail());

            // Then
            assertThat(existsByEmail).isTrue();
        }

        @DisplayName("등록된 이메일의 유저가 없는 경우 false를 반환한다.")
        @Test
        void validEmailExistCheck_InvalidUser() {
            // When
            final Boolean existsByEmail = userRepository.existsByEmail("notExist");

            // Then
            assertThat(existsByEmail).isFalse();
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("유저가 삭제되고 orphanRemoval가 동작한다.")
        @Test
        void deleteAllMapping() {
            // Given
            final User savedUser = userRepository.save(createMockUser());
            final Category mockCategory = categoryRepository.save(createMockCategory(savedUser));
            postRepository.save(createMockPost(savedUser, mockCategory));
            postRepository.save(createMockPost(savedUser, mockCategory));

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
            final User userEntity = userRepository.getById(savedUser.getUserId());
            userRepository.delete(userEntity);
            testEntityManager.flush();
            testEntityManager.clear();

            // Then
            assertAll("user와 연관된 category, post까지 orphan remove",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(0),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(0),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(0)
            );
        }
    }
}
