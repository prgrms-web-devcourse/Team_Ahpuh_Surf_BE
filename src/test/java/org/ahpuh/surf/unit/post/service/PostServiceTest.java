package org.ahpuh.surf.unit.post.service;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.post.converter.PostConverter;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.post.service.PostService;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostConverter postConverter;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private Post post;
    private Category category;
    private User user;

    private Long postId;
    private Long categoryId;
    private String selectedDate;
    private String content;
    private int score;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("ah-puh")
                .email("aaa@gmail.com")
                .password("pswd")
                .build();

        Mockito.lenient().when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        postId = 1L;
        categoryId = 1L;
        selectedDate = "2021-12-06";
        content = "어푸";
        score = 100;

        category = Category.builder().build();
        post = Post.builder()
                .category(category)
                .selectedDate(LocalDate.parse(selectedDate))
                .content(content)
                .score(score)
                .build();

        Mockito.lenient().when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));
    }

    @Test
    @DisplayName("post 생성")
    void create() {
        // given
        final Long userId = 1L;
        final PostRequestDto request = PostRequestDto.builder()
                .categoryId(categoryId)
                .selectedDate(selectedDate)
                .content(content)
                .score(score)
                .build();
        when(postConverter.toEntity(any(), any(), any(), any()))
                .thenReturn(post);
        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        // when
        postService.create(userId, request, null);

        // then
        assertAll(
                () -> verify(postRepository, times(1)).save(any(Post.class))
        );
    }

    @Test
    @DisplayName("존재하지 않는 id로 post 조회")
    void throwException_getPostById() {
        // given
        final Long invalidPostId = -1L;
        when(postRepository.findById(invalidPostId))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> postService.readOne(1L, invalidPostId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Post with given id not found. Invalid id is " + invalidPostId);
    }

}
