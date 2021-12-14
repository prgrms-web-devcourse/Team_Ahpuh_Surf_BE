package org.ahpuh.surf.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.follow.entity.Follow;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.QFollowingPostDto;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.ahpuh.surf.follow.entity.QFollow.follow;
import static org.ahpuh.surf.post.entity.QPost.post;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class PostRepositoryTest {

    User user1;
    Long userId1;
    Long userId2;
    Long userId3;
    String userToken1;
    @Autowired
    private UserController userController;
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

    @BeforeEach
    void setUp() {
        // user1, user2, user3 회원가입 후 userId 반환
        userId1 = userController.join(UserJoinRequestDto.builder()
                        .email("test1@naver.com")
                        .password("test1")
                        .userName("name")
                        .build())
                .getBody();
        userId2 = userController.join(UserJoinRequestDto.builder()
                        .email("test2@naver.com")
                        .password("test2")
                        .userName("name")
                        .build())
                .getBody();
        userId3 = userController.join(UserJoinRequestDto.builder()
                        .email("test3@naver.com")
                        .password("test3")
                        .userName("name")
                        .build())
                .getBody();

        // user1 로그인 후 토큰 발급
        userToken1 = userController.login(UserLoginRequestDto.builder()
                        .email("test1@naver.com")
                        .password("test1")
                        .build())
                .getBody()
                .getToken();

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
                .selectedDate(LocalDate.now())
                .content("content1")
                .score(80)
                .build());
        postRepository.save(Post.builder()
                .user(user3)
                .category(category2)
                .selectedDate(LocalDate.now())
                .content("content2")
                .score(80)
                .build());
        postRepository.save(Post.builder()
                .user(user1)
                .category(category2)
                .selectedDate(LocalDate.now())
                .content("content4")
                .score(80)
                .build());
        postRepository.save(Post.builder()
                .user(user2)
                .category(category1)
                .selectedDate(LocalDate.now())
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
        // Querydsl test
        final JPAQueryFactory query = new JPAQueryFactory(entityManager);
        final List<FollowingPostDto> posts = query
                .select(new QFollowingPostDto(
                        post.user.userId.as("userId"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.id.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.updatedAt.as("updatedAt")
                ))
                .from(post)
                .leftJoin(follow).on(follow.user.userId.eq(userId1))
                .where(follow.followedUser.userId.eq(post.user.userId))
                .groupBy(post.id, follow.followId)
                .orderBy(post.updatedAt.desc())
                .fetch();

        assertAll("follow한 사용자의 모든 posts by querydsl",
                () -> assertThat(posts.size(), is(3)),
                () -> assertThat(posts.get(0).getContent(), is("content3")),
                () -> assertThat(posts.get(0).getUserId(), is(userId2)),
                () -> assertThat(posts.get(1).getContent(), is("content2")),
                () -> assertThat(posts.get(1).getUserId(), is(userId3)),
                () -> assertThat(posts.get(2).getContent(), is("content1")),
                () -> assertThat(posts.get(2).getUserId(), is(userId2))
        );

        // JpaRepository에 Querydsl 적용 test
        final List<FollowingPostDto> findByJpaRepo = postRepository.followingPosts(userId1);

        assertAll("follow한 사용자의 모든 posts in repository",
                () -> assertThat(findByJpaRepo.size(), is(3)),
                () -> assertThat(findByJpaRepo.get(0).getContent(), is("content3")),
                () -> assertThat(findByJpaRepo.get(0).getUserId(), is(userId2)),
                () -> assertThat(findByJpaRepo.get(1).getContent(), is("content2")),
                () -> assertThat(findByJpaRepo.get(1).getUserId(), is(userId3)),
                () -> assertThat(findByJpaRepo.get(2).getContent(), is("content1")),
                () -> assertThat(findByJpaRepo.get(2).getUserId(), is(userId2))
        );
    }

}
