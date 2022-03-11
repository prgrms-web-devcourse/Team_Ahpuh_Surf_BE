package org.ahpuh.surf.unit.post.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.config.QuerydslConfig;
import org.ahpuh.surf.like.domain.LikeRepository;
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
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLike;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPostWithSelectedDate;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(QuerydslConfig.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LikeRepository likeRepository;

    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 5);

    @DisplayName("save 메소드는")
    @Nested
    class SaveMethod {

        @DisplayName("게시글을 등록할 수 있다.")
        @Test
        void savePost_Success() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category = createMockCategory(user);
            testEntityManager.persist(category);
            final Post post = Post.builder()
                    .user(user)
                    .category(category)
                    .selectedDate(LocalDate.now())
                    .content("content")
                    .score(100)
                    .build();

            // When
            postRepository.save(post);

            // Then
            final List<Post> posts = postRepository.findAll();
            assertAll("게시글 등록 테스트",
                    () -> assertThat(posts.size()).isEqualTo(1),
                    () -> assertThat(posts.get(0).getContent()).isEqualTo("content"),
                    () -> assertThat(posts.get(0).getScore()).isEqualTo(100)
            );
        }

        @DisplayName("게시글을 등록하면 해당 유저, 카테고리와 연관관계가 매핑된다.")
        @Test
        void postUserAndCategoryMappingTest() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category = createMockCategory(user);
            testEntityManager.persist(category);
            final Post post = Post.builder()
                    .user(user)
                    .category(category)
                    .selectedDate(LocalDate.now())
                    .content("content")
                    .score(100)
                    .build();

            // When
            postRepository.save(post);

            // Then
            final List<Post> posts = postRepository.findAll();
            assertAll("연관관계 매핑 테스트",
                    () -> assertThat(posts.size()).isEqualTo(1),
                    () -> assertThat(posts.get(0).getUser()).isEqualTo(user),
                    () -> assertThat(posts.get(0).getCategory()).isEqualTo(category)
            );
        }
    }

    @DisplayName("findById 메소드는")
    @Nested
    class FindByIdMethod {

        @DisplayName("postId로 조회할 수 있다.")
        @Test
        void findById_Success() {
            // Given
            final User user = createMockUser();
            final Category category = createMockCategory(user);
            final Post post = Post.builder()
                    .user(user)
                    .category(category)
                    .selectedDate(LocalDate.now())
                    .content("content")
                    .score(100)
                    .build();
            final Long postId = testEntityManager.persist(post).getPostId();

            // When
            final Optional<Post> findedPost = postRepository.findById(postId);

            // Then
            assertAll(
                    () -> assertThat(findedPost).isNotEmpty(),
                    () -> assertThat(findedPost.get().getContent()).isEqualTo("content"),
                    () -> assertThat(findedPost.get().getScore()).isEqualTo(100)
            );
        }

        @DisplayName("존재하지 않는 id로 조회할 수 없다.")
        @Test
        void findById_Fail() {
            // When
            final Optional<Post> post = postRepository.findById(1L);

            // Then
            assertThat(post).isEmpty();
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("게시글이 삭제되고 orphanRemoval가 동작한다.")
        @Test
        void deleteAllMapping() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);
            final Category category = createMockCategory(user);
            testEntityManager.persist(category);
            final Post postEntity = testEntityManager.persist(createMockPost(user, category));
            testEntityManager.persist(createMockLike(user, postEntity));

            assertAll("post, like save 확인",
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(likeRepository.findAll().size()).isEqualTo(1)
            );

            // When
            postRepository.delete(postEntity);
            testEntityManager.flush();
            testEntityManager.clear();

            // Then
            assertAll("post와 연관된 like까지 orphan remove",
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(0),
                    () -> assertThat(likeRepository.findAll().size()).isEqualTo(0)
            );
        }
    }

    @DisplayName("findAllByUserOrderBySelectedDateDesc 메소드는")
    @Nested
    class FindAllByUserOrderBySelectedDateDescMethod {

        @DisplayName("해당 유저가 작성한 모든 게시글을 내림차순으로 정렬해, 최대 한 페이지 개수의 게시글을 반환할 수 있다.")
        @Test
        void success() {
            // Given
            final User user = userRepository.save(createMockUser());
            final Category category = categoryRepository.save(createMockCategory(user));
            for (int i = 0; i < 6; i++) {
                postRepository.save(createMockPostWithSelectedDate(user,
                        category,
                        LocalDate.of(2022, 1, i + 1)));
            }
            testEntityManager.clear();

            assertAll("user, category, post save 확인",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(6)
            );

            // When
            final List<Post> allPostByUser = postRepository.findAllByUserOrderBySelectedDateDesc(user, PAGE_REQUEST);

            // Then
            assertAll("한 페이지에 5개, 날짜순 조회 확인",
                    () -> assertThat(allPostByUser.size()).isEqualTo(5),
                    () -> assertThat(allPostByUser.get(0).getSelectedDate().getDayOfMonth())
                            .isEqualTo(allPostByUser.get(1).getSelectedDate().getDayOfMonth() + 1),
                    () -> assertThat(allPostByUser.get(3).getSelectedDate().getDayOfMonth())
                            .isEqualTo(allPostByUser.get(4).getSelectedDate().getDayOfMonth() + 1)
            );
        }

        @DisplayName("해당 유저가 작성한 게시글이 없는 경우, 빈 리스트를 반환한다.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            final User user = userRepository.save(createMockUser());
            testEntityManager.clear();

            assertAll("user, post save 확인",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(0)
            );

            // When
            final List<Post> allPostByUser = postRepository.findAllByUserOrderBySelectedDateDesc(user, PAGE_REQUEST);

            // Then
            assertThat(allPostByUser).isEqualTo(List.of());
        }
    }

    @DisplayName("findAllByCategoryOrderBySelectedDateDesc 메소드는")
    @Nested
    class FindAllByCategoryOrderBySelectedDateDescMethod {

        @DisplayName("해당 카테고리의 모든 게시글을 내림차순으로 정렬해, 최대 한 페이지 개수의 게시글을 반환할 수 있다.")
        @Test
        void success() {
            // Given
            final User user = userRepository.save(createMockUser());
            final Category category = categoryRepository.save(createMockCategory(user));
            for (int i = 0; i < 6; i++) {
                postRepository.save(createMockPostWithSelectedDate(user,
                        category,
                        LocalDate.of(2022, 1, i + 1)));
            }
            testEntityManager.clear();

            assertAll("user, category, post save 확인",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(6)
            );

            // When
            final List<Post> allPostByCategory = postRepository.findAllByCategoryOrderBySelectedDateDesc(category, PAGE_REQUEST);

            // Then
            assertAll("한 페이지에 5개, 날짜순 조회 확인",
                    () -> assertThat(allPostByCategory.size()).isEqualTo(5),
                    () -> assertThat(allPostByCategory.get(0).getSelectedDate().getDayOfMonth())
                            .isEqualTo(allPostByCategory.get(1).getSelectedDate().getDayOfMonth() + 1),
                    () -> assertThat(allPostByCategory.get(3).getSelectedDate().getDayOfMonth())
                            .isEqualTo(allPostByCategory.get(4).getSelectedDate().getDayOfMonth() + 1)
            );
        }

        @DisplayName("해당 카테고리의 게시글이 없는 경우, 빈 리스트를 반환한다.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            final User user = userRepository.save(createMockUser());
            final Category category = categoryRepository.save(createMockCategory(user));
            testEntityManager.clear();

            assertAll("user, category, post save 확인",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(0)
            );

            // When
            final List<Post> allPostByCategory = postRepository.findAllByCategoryOrderBySelectedDateDesc(category, PAGE_REQUEST);

            // Then
            assertThat(allPostByCategory).isEqualTo(List.of());
        }
    }

    @DisplayName("findTop1ByCategoryOrderBySelectedDateDesc 메소드는")
    @Nested
    class FindTop1ByCategoryOrderBySelectedDateDescMethod {

        @DisplayName("해당 카테고리의 가장 최근 게시글 하나를 조회한다.")
        @Test
        void findOne_Success() {
            // Given
            final User user = userRepository.save(createMockUser());
            final Category category = categoryRepository.save(createMockCategory(user));
            for (int i = 0; i < 6; i++) {
                postRepository.save(createMockPostWithSelectedDate(user,
                        category,
                        LocalDate.of(2022, 1, i + 1)));
            }
            testEntityManager.clear();

            assertAll("user, category, post save 확인",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(6)
            );

            // When
            final Optional<Post> findOne = postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category);

            // Then
            assertAll("날짜순 최신 게시글 하나 조회",
                    () -> assertThat(findOne).isNotEmpty(),
                    () -> assertThat(findOne.get().getSelectedDate())
                            .isEqualTo(LocalDate.of(2022, 1, 6))
            );
        }

        @DisplayName("해당 카테고리  게시글이 없는 경우 optional.empty를 반환한다.")
        @Test
        void findOne_Empty() {
            // Given
            final User user = userRepository.save(createMockUser());
            final Category category = categoryRepository.save(createMockCategory(user));
            testEntityManager.clear();

            assertAll("user, category, post save 확인",
                    () -> assertThat(userRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(categoryRepository.findAll().size()).isEqualTo(1),
                    () -> assertThat(postRepository.findAll().size()).isEqualTo(0)
            );

            // When
            final Optional<Post> findOne = postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category);

            // Then
            assertThat(findOne).isEmpty();
        }
    }
}
