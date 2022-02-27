package org.ahpuh.surf.integration.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.follow.entity.Follow;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.ExploreDto;
import org.ahpuh.surf.post.dto.QExploreDto;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.ahpuh.surf.follow.entity.QFollow.follow;
import static org.ahpuh.surf.post.entity.QPost.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class PostQueryDslTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private EntityManager entityManager;

    private User user1;
    private Long userId1;
    private Long userId2;
    private Long userId3;

    @BeforeEach
    void setUp() {
        // user1, user2, user3 회원가입 후 userId 반환
        userId1 = userRepository.save(User.builder()
                        .email("test1@naver.com")
                        .password("test1")
                        .userName("name")
                        .build())
                .getUserId();
        userId2 = userRepository.save(User.builder()
                        .email("test2@naver.com")
                        .password("test2")
                        .userName("name")
                        .build())
                .getUserId();
        userId3 = userRepository.save(User.builder()
                        .email("test3@naver.com")
                        .password("test3")
                        .userName("name")
                        .build())
                .getUserId();

        user1 = userRepository.getById(userId1);
        final User user2 = userRepository.getById(userId2);
        final User user3 = userRepository.getById(userId3);

        // user2, user3 카테고리 생성
        final Category category1 = categoryRepository.save(Category.builder()
                .user(user2)
                .name("category 1")
                .build());
        final Category category2 = categoryRepository.save(Category.builder()
                .user(user3)
                .name("category 2")
                .build());

        // post 생성
        postRepository.save(Post.builder()
                .user(user2)
                .category(category1)
                .selectedDate(LocalDate.of(2021, 12, 12))
                .content("content1")
                .score(80)
                .build());
        postRepository.save(Post.builder()
                .user(user3)
                .category(category2)
                .selectedDate(LocalDate.of(2021, 2, 1))
                .content("content2")
                .score(80)
                .build());
        postRepository.save(Post.builder()
                .user(user1)
                .category(category2)
                .selectedDate(LocalDate.of(2021, 3, 3))
                .content("content4")
                .score(80)
                .build());
        postRepository.save(Post.builder()
                .user(user2)
                .category(category1)
                .selectedDate(LocalDate.of(2021, 8, 8))
                .content("content3")
                .score(80)
                .build());

        // Following : user1 -> user2, user3
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user2)
                .build());
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user3)
                .build());
    }

    @Test
    @Transactional
    void testQueryDsl() {
        final JPAQueryFactory query = new JPAQueryFactory(entityManager);
        final PageRequest page = PageRequest.of(0, 10);
        final List<ExploreDto> posts = query
                .select(new QExploreDto(
                        post.user.userId.as("userId"),
                        post.user.userName.as("userName"),
                        post.user.profilePhotoUrl.as("profilePhotoUrl"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.createdAt.as("createdAt")
                ))
                .from(post)
                .leftJoin(follow).on(follow.user.userId.eq(userId1))
                .where(follow.followedUser.userId.eq(post.user.userId), post.isDeleted.eq(false))
                .groupBy(post.postId, follow.followId)
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();

        assertAll("follow한 사용자의 모든 posts by querydsl",
                () -> Assertions.assertThat(posts.size()).isEqualTo(3),
                () -> assertThat(posts.get(0).getContent()).isEqualTo("content1"),
                () -> assertThat(posts.get(0).getUserId()).isEqualTo(userId2),
                () -> assertThat(posts.get(1).getContent()).isEqualTo("content3"),
                () -> assertThat(posts.get(1).getUserId()).isEqualTo(userId2),
                () -> assertThat(posts.get(2).getContent()).isEqualTo("content2"),
                () -> assertThat(posts.get(2).getUserId()).isEqualTo(userId3),
                () -> assertThat(postRepository.findFollowingPosts(userId1, page).size()).isEqualTo(3)
        );
    }

}
