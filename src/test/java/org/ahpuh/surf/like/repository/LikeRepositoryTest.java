package org.ahpuh.surf.like.repository;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.post.dto.FollowingPostDto;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    private Long userId1;
    private Long userId2;
    private Long postId1;
    private Long postId2;
    private Long likeId1;

    @BeforeEach
    void setUp() {
        // user1, user2 회원가입 후 userId 반환
        userId1 = userRepository.save(User.builder()
                        .email("user1@naver.com")
                        .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                        .build())
                .getUserId();
        userId2 = userRepository.save(User.builder()
                        .email("user2@naver.com")
                        .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                        .build())
                .getUserId();

        // user2 카테고리 생성
        final User user2 = userRepository.getById(userId2);
        final Category category1 = categoryRepository.save(Category.builder()
                .user(user2)
                .name("category 1")
                .build());

        // post 생성
        postId1 = postRepository.save(Post.builder()
                        .user(user2)
                        .category(category1)
                        .selectedDate(LocalDate.now())
                        .content("content1")
                        .score(80)
                        .build())
                .getId();
        postId2 = postRepository.save(Post.builder()
                        .user(user2)
                        .category(category1)
                        .selectedDate(LocalDate.now())
                        .content("content2")
                        .score(80)
                        .build())
                .getId();

        // user1이 post2를 좋아요 누름
        likeId1 = likeRepository.save(Like.builder()
                        .userId(userId1)
                        .postId(postId2)
                        .build())
                .getLikeId();
    }

    @Test
    @DisplayName("userId와 postId에 해당하는 좋아요를 조회할 수 있다.")
    @Transactional
    void testFindIdByUserIdAndPostId() {
        final Optional<Like> falseReq = likeRepository.findByUserIdAndPostId(userId1, postId1);
        final Optional<Like> trueReq = likeRepository.findByUserIdAndPostId(userId1, postId2);

        assertAll(
                () -> assertThat(falseReq.isEmpty(), is(true)),
                () -> assertThat(trueReq.get().getLikeId(), is(likeId1))
        );
    }

    @Test
    @DisplayName("FollowingPostDto의 likedCheck 메소드가 잘 동작하는지 확인")
    @Transactional
    void testLikedCheck() {
        final FollowingPostDto dto = FollowingPostDto.builder().build();

        // not liked
        dto.likedCheck(likeRepository.findByUserIdAndPostId(userId1, postId1));
        assertAll(
                () -> assertThat(dto.getLikeId(), is(nullValue())),
                () -> assertThat(dto.getIsLiked(), is(false))
        );

        // liked
        final Optional<Like> trueReq = likeRepository.findByUserIdAndPostId(userId1, postId2);
        dto.likedCheck(trueReq);
        assertAll(
                () -> assertThat(dto.getLikeId(), is(trueReq.get().getLikeId())),
                () -> assertThat(dto.getIsLiked(), is(true))
        );
    }

}