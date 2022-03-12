package org.ahpuh.surf.unit.like.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.config.QuerydslConfig;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.like.domain.LikeRepository;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
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
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLike;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(QuerydslConfig.class)
@DataJpaTest
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("save 메소드는")
    @Nested
    class SaveMethod {

        @DisplayName("좋아요를 등록할 수 있다.")
        @Test
        void saveLike_Success() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));

            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Category categoryByUser2 = testEntityManager.persist(createMockCategory(user2));
            final Post postByUser2 = testEntityManager.persist(createMockPost(user2, categoryByUser2));

            final Like like = createMockLike(user1, postByUser2);

            // When
            likeRepository.save(like);

            // Then
            final List<Like> likes = likeRepository.findAll();
            assertAll("좋아요 등록 테스트",
                    () -> assertThat(likes.size()).isEqualTo(1),
                    () -> assertThat(likes.get(0).getUser().getEmail()).isEqualTo(user1.getEmail()),
                    () -> assertThat(likes.get(0).getPost().getPostId()).isEqualTo(postByUser2.getPostId())
            );
        }

        @DisplayName("좋아요를 등록하면 해당 유저와 게시글에 연관관계가 매핑된다.")
        @Test
        void likeMappingTest() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));

            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Category categoryByUser2 = testEntityManager.persist(createMockCategory(user2));
            final Post postByUser2 = testEntityManager.persist(createMockPost(user2, categoryByUser2));

            final Like like = createMockLike(user1, postByUser2);

            // When
            likeRepository.save(like);

            // Then
            final List<Like> likes = likeRepository.findAll();
            assertAll("좋아요 연관관계 매핑 테스트",
                    () -> assertThat(likes.size()).isEqualTo(1),
                    () -> assertThat(likes.get(0).getUser().getLikes().size()).isEqualTo(1),
                    () -> assertThat(likes.get(0).getUser().getLikes().get(0).getPost().getPostId()).isEqualTo(postByUser2.getPostId()),
                    () -> assertThat(likes.get(0).getPost().getLikes().size()).isEqualTo(1),
                    () -> assertThat(likes.get(0).getPost().getLikes().get(0).getUser().getEmail()).isEqualTo(user1.getEmail())
            );
        }
    }

    @DisplayName("findById 메소드는")
    @Nested
    class FindByIdMethod {

        @DisplayName("likeId에 해당하는 Like 엔티티를 조회할 수 있다.")
        @Test
        void findByLikeId_Success() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));

            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Category categoryByUser2 = testEntityManager.persist(createMockCategory(user2));
            final Post postByUser2 = testEntityManager.persist(createMockPost(user2, categoryByUser2));

            final Like like = likeRepository.save(createMockLike(user1, postByUser2));

            // When
            final Optional<Like> findedlike = likeRepository.findById(like.getLikeId());

            // Then
            assertAll("팔로우한 유저 정보 확인",
                    () -> assertThat(findedlike).isNotEmpty(),
                    () -> assertThat(findedlike.get().getUser().getEmail()).isEqualTo(user1.getEmail()),
                    () -> assertThat(findedlike.get().getPost().getPostId()).isEqualTo(postByUser2.getPostId())
            );
        }

        @DisplayName("likeId에 해당하는 좋아요가 없다면 optional.empty()를 반환한다.")
        @Test
        void findByLikeId_Empty() {
            // When
            final Optional<Like> findedlike = likeRepository.findById(1L);

            // Then
            assertThat(findedlike).isEmpty();
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("좋아요를 삭제할 수 있다.")
        @Test
        void deleteLikeTest() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));

            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Category categoryByUser2 = testEntityManager.persist(createMockCategory(user2));
            final Post postByUser2 = testEntityManager.persist(createMockPost(user2, categoryByUser2));

            likeRepository.save(createMockLike(user1, postByUser2));

            final List<Like> allLikes = likeRepository.findAll();
            assertThat(allLikes.size()).isEqualTo(1);

            // When
            likeRepository.delete(allLikes.get(0));
            testEntityManager.flush();
            testEntityManager.clear();

            // Then
            assertThat(likeRepository.findAll().size()).isEqualTo(0);
        }
    }

    @DisplayName("existsByUserAndPost 메소드는")
    @Nested
    class ExistsByUserAndPostMethod {

        @DisplayName("user -> post 좋아요 기록이 있다면 true를 반환한다.")
        @Test
        void testLikeExistCheck_True() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));

            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Category categoryByUser2 = testEntityManager.persist(createMockCategory(user2));
            final Post postByUser2 = testEntityManager.persist(createMockPost(user2, categoryByUser2));

            likeRepository.save(createMockLike(user1, postByUser2));

            final List<Like> allLikes = likeRepository.findAll();
            assertThat(allLikes.size()).isEqualTo(1);

            // When
            final boolean existCheck = likeRepository.existsByUserAndPost(user1, postByUser2);

            // Then
            assertThat(existCheck).isTrue();
        }

        @DisplayName("팔로우 기록이 없다면 false를 반환한다.")
        @Test
        void testLikeExistCheck_False() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));

            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Category categoryByUser2 = testEntityManager.persist(createMockCategory(user2));
            final Post postByUser2 = testEntityManager.persist(createMockPost(user2, categoryByUser2));

            // When
            final boolean existCheck = likeRepository.existsByUserAndPost(user1, postByUser2);

            // Then
            assertThat(existCheck).isFalse();
        }
    }
}
