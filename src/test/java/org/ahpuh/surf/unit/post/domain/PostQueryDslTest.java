package org.ahpuh.surf.unit.post.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.config.QuerydslConfig;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
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
import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollow;
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLike;
import static org.ahpuh.surf.common.factory.MockPostFactory.*;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(QuerydslConfig.class)
@DataJpaTest
public class PostQueryDslTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 5);

    @DisplayName("findPost 메소드는")
    @Nested
    class FindPostMethod {

        @DisplayName("하나의 게시글 상세 정보를 조회할 수 있다_좋아요 O")
        @Test
        void findPostInfoWithLike_Success() {
            // Given
            final User user1 = createMockUser("user1@naver.com");
            final User user2 = createMockUser("user2@naver.com");
            final Long userId1 = testEntityManager.persist(user1).getUserId();
            testEntityManager.persist(user2);

            final Category category = createMockCategory(user2);
            testEntityManager.persist(category);

            final Post post = createMockPost(user2, category);
            final Long postId = testEntityManager.persist(post).getPostId();

            final Like like = createMockLike(user1, post);
            final Long likeId = testEntityManager.persist(like).getLikeId();

            // When
            final Optional<PostReadResponseDto> postDto = postRepository.findPost(postId, userId1);

            // Then
            assertAll("게시글 상세 조회 테스트",
                    () -> assertThat(postDto).isNotEmpty(),
                    () -> assertThat(postDto.get().getPostId()).isEqualTo(postId),
                    () -> assertThat(postDto.get().getSelectedDate()).isEqualTo(post.getSelectedDate()),
                    () -> assertThat(postDto.get().getLikeId()).isEqualTo(likeId)
            );
        }

        @DisplayName("해당 게시글이 없는 경우 empty 값을 반환한다.")
        @Test
        void noPostGetEmpty() {
            // When
            final Optional<PostReadResponseDto> postDto = postRepository.findPost(1L, 1L);

            // Then
            assertThat(postDto).isEmpty();
        }
    }

    @DisplayName("findPostsOfMonth 메소드는")
    @Nested
    class FindPostsOfMonthMethod {

        @DisplayName("해당 유저의 특정 한달 게시글을 지정 날짜(selectedDate) 내림차순으로 전체 조회할 수 있다.")
        @Test
        void findPostsOfMonth_Success() {
            // Given
            final User user = createMockUser();
            final Long userId = testEntityManager.persist(user).getUserId();

            final Category category = createMockCategory(user);
            testEntityManager.persist(category);

            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 1)));
            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 15)));
            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 31)));
            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 2, 1)));

            final LocalDate startDate = LocalDate.of(2022, 1, 1);
            final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // When
            final List<PostsOfMonthResponseDto> responseDtos = postRepository.findPostsOfMonth(userId, startDate, endDate);

            // Then
            assertAll(
                    () -> assertThat(responseDtos.size()).isEqualTo(3),
                    () -> assertThat(responseDtos.get(0).getSelectedDate().getDayOfMonth()).isEqualTo(31),
                    () -> assertThat(responseDtos.get(1).getSelectedDate().getDayOfMonth()).isEqualTo(15),
                    () -> assertThat(responseDtos.get(2).getSelectedDate().getDayOfMonth()).isEqualTo(1)
            );
        }

        @DisplayName("같은 날짜(selectedDate)로 지정되어 작성됐다면 생성 날짜(createdAt)를 기준으로 내림차순 정렬한다.")
        @Test
        void sameSelectedDate_orderByCreatedAt() throws InterruptedException {
            // Given
            final User user = createMockUser();
            final Long userId = testEntityManager.persist(user).getUserId();

            final Category category = createMockCategory(user);
            testEntityManager.persist(category);

            testEntityManager.persist(
                    createMockPostWithContent(user, category, "post1"));
            Thread.sleep(0, 1);
            testEntityManager.persist(
                    createMockPostWithContent(user, category, "post2"));
            Thread.sleep(0, 1);
            testEntityManager.persist(
                    createMockPostWithContent(user, category, "post3"));

            final LocalDate startDate = LocalDate.of(2022, 1, 1);
            final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // When
            final List<PostsOfMonthResponseDto> responseDtos = postRepository.findPostsOfMonth(userId, startDate, endDate);

            // Then
            assertAll(
                    () -> assertThat(responseDtos.size()).isEqualTo(3),
                    () -> assertThat(responseDtos.get(0).getContent()).isEqualTo("post3"),
                    () -> assertThat(responseDtos.get(1).getContent()).isEqualTo("post2"),
                    () -> assertThat(responseDtos.get(2).getContent()).isEqualTo("post1")
            );
        }

        @DisplayName("해당 유저가 없는 경우 빈 배열을 반환한다.")
        @Test
        void noUser_ReturnEmptyList() {
            // Given
            final LocalDate startDate = LocalDate.of(2022, 1, 1);
            final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // When
            final List<PostsOfMonthResponseDto> responseDtos = postRepository.findPostsOfMonth(1L, startDate, endDate);

            // Then
            assertThat(responseDtos).isEqualTo(List.of());
        }

        @DisplayName("게시글이 없는 경우 빈 배열을 반환한다.")
        @Test
        void noPost_ReturnEmptyList() {
            // Given
            final Long userId = testEntityManager.persist(createMockUser()).getUserId();

            final LocalDate startDate = LocalDate.of(2022, 1, 1);
            final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // When
            final List<PostsOfMonthResponseDto> responseDtos = postRepository.findPostsOfMonth(userId, startDate, endDate);

            // Then
            assertThat(responseDtos).isEqualTo(List.of());
        }
    }

    @DisplayName("findEachDateAndCountOfYearByUser 메소드는")
    @Nested
    class FindEachDateAndCountOfYearByUserMethod {

        @DisplayName("해당 유저의 일년 각 날마다의 게시글 count를 반환한다.")
        @Test
        void findEachDateAndCountOfYear() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);

            final Category category = createMockCategory(user);
            testEntityManager.persist(category);

            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2021, 12, 31)));
            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 1)));
            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 2)));
            testEntityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 2)));

            // When
            final List<PostCountResponseDto> responseDtos = postRepository.findEachDateAndCountOfYearByUser(2022, user);

            // Then
            assertAll(
                    () -> assertThat(responseDtos.size()).isEqualTo(2),
                    () -> assertThat(responseDtos.get(0).getDate()).isEqualTo(LocalDate.of(2022, 1, 1)),
                    () -> assertThat(responseDtos.get(0).getCount()).isEqualTo(1),
                    () -> assertThat(responseDtos.get(1).getDate()).isEqualTo(LocalDate.of(2022, 1, 2)),
                    () -> assertThat(responseDtos.get(1).getCount()).isEqualTo(2)
            );
        }

        @DisplayName("게시글이 없는 경우 빈 배열을 반환한다.")
        @Test
        void noPost_ReturnEmptyList() {
            // Given
            final User user = createMockUser();
            testEntityManager.persist(user);

            // When
            final List<PostCountResponseDto> responseDtos = postRepository.findEachDateAndCountOfYearByUser(2022, user);

            // Then
            assertThat(responseDtos).isEqualTo(List.of());
        }
    }

    @DisplayName("findAllScoreWithCategoryByUser 메소드는")
    @Nested
    class FindAllScoreWithCategoryByUserMethod {

        @DisplayName("해당 유저의 각 카테고리마다 게시글 점수들을 반환한다.")
        @Test
        void findAllScoreOfCategory() {
            // Given
            final User user = createMockUser();
            final Long userId = testEntityManager.persist(user).getUserId();

            final Category category1 = createMockCategory(user);
            final Category category2 = createMockCategory(user);
            final Long categoryId1 = testEntityManager.persist(category1).getCategoryId();
            final Long categoryId2 = testEntityManager.persist(category2).getCategoryId();

            testEntityManager.persist(createMockPostWithScore(user, category1, 80));
            testEntityManager.persist(createMockPostWithScore(user, category1, 85));
            testEntityManager.persist(createMockPostWithScore(user, category2, 90));
            testEntityManager.persist(createMockPostWithScore(user, category2, 95));
            testEntityManager.persist(createMockPostWithScore(user, category2, 100));

            // When
            final List<CategorySimpleDto> responseDtos = postRepository.findAllScoreWithCategoryByUser(userId);

            // Then
            assertAll("카테고리1 -> 2개 게시글, 카테고리2 -> 3개 게시글",
                    () -> assertThat(responseDtos.size()).isEqualTo(2),
                    () -> assertThat(responseDtos.get(0).getCategoryId()).isEqualTo(categoryId1),
                    () -> assertThat(responseDtos.get(0).getPostScores().size()).isEqualTo(2),
                    () -> assertThat(responseDtos.get(0).getPostScores().get(0).getScore()).isEqualTo(80),
                    () -> assertThat(responseDtos.get(0).getPostScores().get(1).getScore()).isEqualTo(85),
                    () -> assertThat(responseDtos.get(1).getCategoryId()).isEqualTo(categoryId2),
                    () -> assertThat(responseDtos.get(1).getPostScores().size()).isEqualTo(3),
                    () -> assertThat(responseDtos.get(1).getPostScores().get(0).getScore()).isEqualTo(90),
                    () -> assertThat(responseDtos.get(1).getPostScores().get(1).getScore()).isEqualTo(95),
                    () -> assertThat(responseDtos.get(1).getPostScores().get(2).getScore()).isEqualTo(100)
            );
        }

        @DisplayName("게시글이 없는 경우 빈 리스트를 반환한다.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            final User user = createMockUser();
            final Long userId = testEntityManager.persist(user).getUserId();

            testEntityManager.persist(createMockCategory(user));
            testEntityManager.persist(createMockCategory(user));

            // When
            final List<CategorySimpleDto> responseDtos = postRepository.findAllScoreWithCategoryByUser(userId);

            // Then
            assertThat(responseDtos).isEqualTo(List.of());
        }

        @DisplayName("카테고리가 없는 경우 빈 리스트를 반환한다.")
        @Test
        void noCategoryReturnEmptyList() {
            // Given
            final Long userId = testEntityManager.persist(createMockUser()).getUserId();

            // When
            final List<CategorySimpleDto> responseDtos = postRepository.findAllScoreWithCategoryByUser(userId);

            // Then
            assertThat(responseDtos).isEqualTo(List.of());
        }
    }

    @DisplayName("최신 모든 게시글 커서 페이징 조회")
    @Nested
    class FindAllRecentPost {

        @DisplayName("7개의 게시글이 등록되어 있는 경우")
        @Nested
        class SevenPosts {

            private Long userId;
            private User user;

            @BeforeEach
            void setUp() {
                user = testEntityManager.persist(createMockUser());
                userId = user.getUserId();

                final Category category = createMockCategory(this.user);
                testEntityManager.persist(category);

                for (int i = 0; i < 7; i++) {
                    testEntityManager.persist(
                            createMockPostWithSelectedDate(this.user, category, LocalDate.of(2022, 1, i + 1)));
                }
            }

            @DisplayName("findAllRecentPost()는 한 페이지(5개)의 게시글을 최신순으로 반환한다.")
            @Test
            void findRecentPostOfPage() {
                // When
                final List<RecentPostResponseDto> responseDtos = postRepository.findAllRecentPost(userId, PAGE_REQUEST);

                // Then
                assertAll("최대 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 7)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 6)),
                        () -> assertThat(responseDtos.get(2).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 5)),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3))
                );
            }

            @DisplayName("findAllRecentPostByCursor()는 그 다음 한 페이지(최대 5개)를 최신순으로 반환한다.")
            @Test
            void findRecentPostOfPageByCursor() {
                // When
                final List<RecentPostResponseDto> responseDtos = postRepository.findAllRecentPostByCursor(
                        userId,
                        5L,
                        LocalDate.of(2022, 1, 3),
                        PAGE_REQUEST);

                // Then
                assertAll("다음 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(2),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 2)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 1))
                );
            }

            @DisplayName("내가 좋아요를 누른 게시글은 likeId를 반환하고 아닌 게시글은 likeId에 null을 반환한다.")
            @Test
            void returnLikeId() {
                // Given
                final Post post3 = postRepository.findAll().get(2);
                assertThat(post3.getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 3));

                final Long likeId = testEntityManager.persist(createMockLike(user, post3)).getLikeId();

                // When
                final List<RecentPostResponseDto> responseDtos = postRepository.findAllRecentPost(userId, PAGE_REQUEST);

                // Then
                assertAll("좋아요를 누른 게시글은 likeId 반환, 안누른 게시글은 likeId = null 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3)),
                        () -> assertThat(responseDtos.get(4).getLikeId()).isEqualTo(likeId),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(3).getLikeId()).isNull()
                );
            }

            @DisplayName("내가 팔로우한 유저의 게시글은 followId를 반환하고 아닌 게시글은 followId에 null을 반환한다.")
            @Test
            void returnFollowId() {
                // Given
                final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
                final Category category2 = testEntityManager.persist(createMockCategory(user2));
                testEntityManager.persist(
                        createMockPostWithSelectedDate(user2, category2, LocalDate.of(2022, 1, 10)));
                final Long followId = testEntityManager.persist(createMockFollow(user, user2))
                        .getFollowId();

                // When
                final List<RecentPostResponseDto> responseDtos = postRepository.findAllRecentPost(userId, PAGE_REQUEST);

                // Then
                assertAll("팔로우한 유저의 게시글은 followId 반환, 아닌 게시글은 followId = null 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 10)),
                        () -> assertThat(responseDtos.get(0).getFollowId()).isEqualTo(followId),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 7)),
                        () -> assertThat(responseDtos.get(1).getFollowId()).isNull()
                );
            }
        }

        @DisplayName("게시글이 없는 경우")
        @Nested
        class NoPost {

            @DisplayName("findAllRecentPost()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // Given
                final User user = createMockUser();
                final Long userId = testEntityManager.persist(user).getUserId();

                final Category category = createMockCategory(user);
                testEntityManager.persist(category);

                // When
                final List<RecentPostResponseDto> responseDtos = postRepository.findAllRecentPost(userId, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }

        @DisplayName("유저가 없는 경우")
        @Nested
        class NoUser {

            @DisplayName("findAllRecentPost()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // When
                final List<RecentPostResponseDto> responseDtos = postRepository.findAllRecentPost(1L, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }
    }

    @DisplayName("내가 팔로잉한 유저들의 모든 게시글 최신순으로 커서 페이징 조회")
    @Nested
    class FindFollowingPost {

        @DisplayName("7개의 게시글을 작성한 유저1을 유저2가 팔로우한 경우")
        @Nested
        class FirstUserSevenPostsSecondUserOnePost {

            private User user1;
            private User user2;
            private Long userId2;

            @BeforeEach
            void setUp() {
                user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
                user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
                userId2 = user2.getUserId();

                final Category category1 = createMockCategory(user1);
                testEntityManager.persist(category1);
                final Category category2 = createMockCategory(user2);
                testEntityManager.persist(category2);

                // user1 -> 게시글 7개
                for (int i = 0; i < 7; i++) {
                    testEntityManager.persist(
                            createMockPostWithSelectedDate(user1, category1, LocalDate.of(2022, 1, i + 1)));
                }
                // user2 -> 게시글 1개
                testEntityManager.persist(
                        createMockPostWithSelectedDate(user2, category2, LocalDate.of(2022, 1, 10)));

                // user2 -> user1 팔로우
                testEntityManager.persist(createMockFollow(user2, user1));

                testEntityManager.flush();
                testEntityManager.clear();
            }

            @DisplayName("findFollowingPosts()는 팔로우한 유저의 게시글을 최대 한페이지(5개) 최신순으로 반환한다.")
            @Test
            void findFollowingPostOfPage() {
                // When
                final List<ExploreResponseDto> responseDtos = postRepository.findFollowingPosts(userId2, PAGE_REQUEST);

                // Then
                assertAll("최대 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 7)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 6)),
                        () -> assertThat(responseDtos.get(2).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 5)),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3))
                );
            }

            @DisplayName("findFollowingPostsByCursor()는 그 다음 한 페이지(최대 5개)를 최신순으로 반환한다.")
            @Test
            void findFollowingPostOfPageByCursor() {
                // When
                final List<ExploreResponseDto> responseDtos = postRepository.findFollowingPostsByCursor(
                        userId2,
                        5L,
                        LocalDate.of(2022, 1, 3),
                        PAGE_REQUEST);

                // Then
                assertAll("다음 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(2),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 2)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 1))
                );
            }

            @DisplayName("내가 좋아요를 누른 게시글은 likeId를 반환하고 아닌 게시글은 likeId에 null을 반환한다.")
            @Test
            void returnLikeId() {
                // Given
                final Post post3 = postRepository.findAll().get(2);
                assertThat(post3.getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 3));

                final Long likeId = testEntityManager.persist(createMockLike(user2, post3)).getLikeId();

                // When
                final List<ExploreResponseDto> responseDtos = postRepository.findFollowingPosts(userId2, PAGE_REQUEST);

                // Then
                assertAll("좋아요를 누른 게시글은 likeId 반환, 안누른 게시글은 likeId = null 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3)),
                        () -> assertThat(responseDtos.get(4).getLikeId()).isEqualTo(likeId),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(3).getLikeId()).isNull()
                );
            }
        }

        @DisplayName("팔로잉한 유저의 게시글이 없는 경우")
        @Nested
        class NoFollowingUserPost {

            @DisplayName("findFollowingPosts()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // Given
                final Long userId = testEntityManager.persist(createMockUser("user1@naver.com"))
                        .getUserId();

                // When
                final List<ExploreResponseDto> responseDtos = postRepository.findFollowingPosts(userId, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }

        @DisplayName("해당 유저가 없는 경우")
        @Nested
        class NoUser {

            @DisplayName("findFollowingPosts()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // When
                final List<ExploreResponseDto> responseDtos = postRepository.findFollowingPosts(5L, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }
    }

    @DisplayName("해당 유저의 모든 게시글을 최신순으로 커서 페이징 조회")
    @Nested
    class FindAllPostOfUser {

        @DisplayName("유저1이 7개의 게시글을 작성한 경우")
        @Nested
        class FirstUserSevenPosts {

            private User user1;
            private Long userId1;
            private User user2;
            private Long userId2;

            @BeforeEach
            void setUp() {
                user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
                userId1 = user1.getUserId();
                user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
                userId2 = user2.getUserId();

                final Category category1 = createMockCategory(user1);
                testEntityManager.persist(category1);
                final Category category2 = createMockCategory(user2);
                testEntityManager.persist(category2);

                // user1 -> 게시글 7개
                for (int i = 0; i < 7; i++) {
                    testEntityManager.persist(
                            createMockPostWithSelectedDate(user1, category1, LocalDate.of(2022, 1, i + 1)));
                }
                // user2 -> 게시글 1개
                testEntityManager.persist(
                        createMockPostWithSelectedDate(user2, category2, LocalDate.of(2022, 1, 10)));

                testEntityManager.flush();
                testEntityManager.clear();
            }

            @DisplayName("findAllPostOfUser()는 팔로우한 유저의 게시글을 최대 한페이지(5개) 최신순으로 반환한다.")
            @Test
            void findAllPostOfPage() {
                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfUser(userId2, userId1, PAGE_REQUEST);

                // Then
                assertAll("최대 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 7)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 6)),
                        () -> assertThat(responseDtos.get(2).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 5)),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3))
                );
            }

            @DisplayName("findAllPostOfUserByCursor()는 그 다음 한 페이지(최대 5개)를 최신순으로 반환한다.")
            @Test
            void findAllPostOfPageByCursor() {
                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfUserByCursor(
                        userId2,
                        userId1,
                        5L,
                        LocalDate.of(2022, 1, 3),
                        PAGE_REQUEST);

                // Then
                assertAll("다음 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(2),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 2)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 1))
                );
            }

            @DisplayName("내가 좋아요를 누른 게시글은 likeId를 반환하고 아닌 게시글은 likeId에 null을 반환한다.")
            @Test
            void returnLikeId() {
                // Given
                final Post post3 = postRepository.findAll().get(2);
                assertThat(post3.getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 3));

                final Long likeId = testEntityManager.persist(createMockLike(user2, post3)).getLikeId();

                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfUser(userId2, userId1, PAGE_REQUEST);

                // Then
                assertAll("좋아요를 누른 게시글은 likeId 반환, 안누른 게시글은 likeId = null 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3)),
                        () -> assertThat(responseDtos.get(4).getLikeId()).isEqualTo(likeId),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(3).getLikeId()).isNull()
                );
            }
        }

        @DisplayName("해당 유저의 게시글이 없는 경우")
        @Nested
        class NoUserPost {

            @DisplayName("findAllPostOfUser()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // Given
                final Long userId1 = testEntityManager.persist(createMockUser("user1@naver.com"))
                        .getUserId();
                final Long userId2 = testEntityManager.persist(createMockUser("user2@naver.com"))
                        .getUserId();

                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfUser(userId2, userId1, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }

        @DisplayName("해당 유저가 없는 경우")
        @Nested
        class NoUser {

            @DisplayName("findAllPostOfUser()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfUser(1L, 5L, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }
    }

    @DisplayName("해당 카테고리의 모든 게시글을 최신순으로 커서 페이징 조회")
    @Nested
    class FindAllPostOfCategory {

        @DisplayName("카테고리1에 해당하는 7개의 게시글이 작성된 경우")
        @Nested
        class FirstCategorySevenPosts {

            private User user2;
            private Long userId2;
            private Long categoryId1;

            @BeforeEach
            void setUp() {
                final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
                user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
                userId2 = user2.getUserId();

                final Category category1 = createMockCategory(user1);
                categoryId1 = testEntityManager.persist(category1)
                        .getCategoryId();
                final Category category2 = createMockCategory(user2);
                testEntityManager.persist(category2);

                // category1 -> 게시글 7개
                for (int i = 0; i < 7; i++) {
                    testEntityManager.persist(
                            createMockPostWithSelectedDate(user1, category1, LocalDate.of(2022, 1, i + 1)));
                }
                // category2 -> 게시글 1개
                testEntityManager.persist(
                        createMockPostWithSelectedDate(user2, category2, LocalDate.of(2022, 1, 10)));

                testEntityManager.flush();
                testEntityManager.clear();
            }

            @DisplayName("findAllPostOfCategory()는 해당 카테고리의 게시글을 최대 한페이지(5개) 최신순으로 반환한다.")
            @Test
            void findAllPostOfPage() {
                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfCategory(userId2, categoryId1, PAGE_REQUEST);

                // Then
                assertAll("최대 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 7)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 6)),
                        () -> assertThat(responseDtos.get(2).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 5)),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3))
                );
            }

            @DisplayName("findAllPostOfCategoryByCursor()는 그 다음 한 페이지(최대 5개)를 최신순으로 반환한다.")
            @Test
            void findAllPostOfPageByCursor() {
                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfCategoryByCursor(
                        userId2,
                        categoryId1,
                        5L,
                        LocalDate.of(2022, 1, 3),
                        PAGE_REQUEST);

                // Then
                assertAll("다음 한 페이지의 게시글 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(2),
                        () -> assertThat(responseDtos.get(0).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 2)),
                        () -> assertThat(responseDtos.get(1).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 1))
                );
            }

            @DisplayName("내가 좋아요를 누른 게시글은 likeId를 반환하고 아닌 게시글은 likeId에 null을 반환한다.")
            @Test
            void returnLikeId() {
                // Given
                final Post post3 = postRepository.findAll().get(2);
                assertThat(post3.getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 3));

                final Long likeId = testEntityManager.persist(createMockLike(user2, post3)).getLikeId();

                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfCategory(userId2, categoryId1, PAGE_REQUEST);

                // Then
                assertAll("좋아요를 누른 게시글은 likeId 반환, 안누른 게시글은 likeId = null 반환",
                        () -> assertThat(responseDtos.size()).isEqualTo(5),
                        () -> assertThat(responseDtos.get(4).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 3)),
                        () -> assertThat(responseDtos.get(4).getLikeId()).isEqualTo(likeId),
                        () -> assertThat(responseDtos.get(3).getSelectedDate())
                                .isEqualTo(LocalDate.of(2022, 1, 4)),
                        () -> assertThat(responseDtos.get(3).getLikeId()).isNull()
                );
            }
        }

        @DisplayName("해당 카테고리의 게시글이 없는 경우")
        @Nested
        class NoPostOfCategory {

            @DisplayName("findAllPostOfCategory()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // Given
                final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
                final Long categoryId1 = testEntityManager.persist(createMockCategory(user1))
                        .getCategoryId();
                final Long userId2 = testEntityManager.persist(createMockUser("user2@naver.com"))
                        .getUserId();

                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfCategory(userId2, categoryId1, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }

        @DisplayName("해당 카테고리가 없는 경우")
        @Nested
        class NoCategory {

            @DisplayName("findAllPostOfCategory()는 빈 리스트를 반환한다.")
            @Test
            void returnEmptyList() {
                // When
                final List<AllPostResponseDto> responseDtos = postRepository.findAllPostOfCategory(1L, 5L, PAGE_REQUEST);

                // Then
                assertThat(responseDtos).isEqualTo(List.of());
            }
        }
    }
}
