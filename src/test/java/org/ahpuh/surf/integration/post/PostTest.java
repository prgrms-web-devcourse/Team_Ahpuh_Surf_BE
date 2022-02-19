package org.ahpuh.surf.integration.post;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.post.service.PostService;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostTest {

    private Long userId2;
    private Category category2;
    private Category category3;
    private int year;

    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        year = 2021;

        final Long userId1 = saveUser("test1@naver.com", "test1");
        userId2 = saveUser("test2@naver.com", "test2");

        final User user1 = userRepository.getById(userId1);
        final User user2 = userRepository.getById(userId2);

        final Category category1 = saveCategory(user1, "category 1");
        category2 = saveCategory(user2, "category 2");
        category3 = saveCategory(user2, "category 3");

        // post 생성
        savePost(user1, category1, LocalDate.now(), "content111", 0);

        savePost(user2, category3, LocalDate.of(2020, 12, 12), "content5", 90);
        savePost(user2, category3, LocalDate.of(year, 12, 31), "content6", 50);
        savePost(user2, category3, LocalDate.of(year, 1, 1), "content7", 100);

        savePost(user2, category2, LocalDate.of(year, 12, 10), "content1", 80);
        savePost(user2, category2, LocalDate.of(2022, 12, 23), "content2", 90);
        savePost(user2, category2, LocalDate.of(year, 12, 31), "content3", 50);
        savePost(user2, category2, LocalDate.of(year, 12, 4), "content4", 100);
    }

    @Test
    @DisplayName("해당년도 게시글 개수 정보 조회")
    @Transactional
    void getCountsPerDayWithYear() {
        // when
        final List<PostCountDto> response = postService.getCountsPerDayWithYear(year, userId2);

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(4),
                () -> assertThat(response.get(0).getDate()).isEqualTo(LocalDate.of(year, 1, 1)),
                () -> assertThat(response.get(0).getCount()).isEqualTo(1),
                () -> assertThat(response.get(2).getDate()).isEqualTo(LocalDate.of(year, 12, 10)),
                () -> assertThat(response.get(2).getCount()).isEqualTo(1),
                () -> assertThat(response.get(3).getDate()).isEqualTo(LocalDate.of(year, 12, 31)),
                () -> assertThat(response.get(3).getCount()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("일년치 게시글 점수 조회")
    @Transactional
    void getScoresWithCategoryByUserId() {
        // when
        final List<CategorySimpleDto> response = postService.getScoresWithCategoryByUserId(userId2);

        final CategorySimpleDto categorySimpleDto1 = response.get(0);
        final CategorySimpleDto categorySimpleDto2 = response.get(1);

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(2),

                () -> assertThat(categorySimpleDto1.getCategoryId()).isEqualTo(category2.getCategoryId()),
                () -> assertThat(categorySimpleDto1.getPostScores().size()).isEqualTo(4),
                () -> assertThat(categorySimpleDto1.getPostScores().get(0).getY()).isEqualTo(100),
                () -> assertThat(categorySimpleDto1.getPostScores().get(1).getY()).isEqualTo(80),
                () -> assertThat(categorySimpleDto1.getPostScores().get(2).getY()).isEqualTo(50),
                () -> assertThat(categorySimpleDto1.getPostScores().get(3).getY()).isEqualTo(90),

                () -> assertThat(categorySimpleDto2.getCategoryId()).isEqualTo(category3.getCategoryId()),
                () -> assertThat(categorySimpleDto2.getPostScores().size()).isEqualTo(3),
                () -> assertThat(categorySimpleDto2.getPostScores().get(0).getY()).isEqualTo(90),
                () -> assertThat(categorySimpleDto2.getPostScores().get(1).getY()).isEqualTo(100),
                () -> assertThat(categorySimpleDto2.getPostScores().get(2).getY()).isEqualTo(50)
        );
    }

    private Long saveUser(final String email, final String pw) {
        return userController.join(UserJoinRequestDto.builder()
                        .email(email)
                        .password(pw)
                        .userName("name")
                        .build())
                .getBody();
    }

    private Category saveCategory(final User user, final String categoryName) {
        return categoryRepository.save(Category.builder()
                .user(user)
                .name(categoryName)
                .build());
    }

    private void savePost(final User user, final Category category, final LocalDate selectedDate, final String content,
                          final int score) {
        postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .selectedDate(selectedDate)
                .content(content)
                .score(score)
                .build());
    }

}
