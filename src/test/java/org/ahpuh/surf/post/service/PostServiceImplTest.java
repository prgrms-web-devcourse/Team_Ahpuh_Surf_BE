package org.ahpuh.surf.post.service;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.NotFoundException;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponse;
import org.ahpuh.surf.post.dto.PostRequest;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private Category category;

    private Long postId;
    private Long categoryId;
    private String selectedDate;
    private String content;
    private int score;

    @BeforeEach
    void setUp() {
        postId = 1L;
        categoryId = 1L;
        selectedDate = "2021-12-06";
        content = "어푸";
        score = 100;

        category = Category.builder().build();
        post = Post.builder()
                .id(postId)
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
        final PostRequest request = PostRequest.builder()
                .categoryId(categoryId)
                .selectedDate(selectedDate)
                .content(content)
                .score(score)
                .build();
        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        // when
        final PostIdResponse response = postService.create(request);

        // then
        assertAll(
                () -> verify(postRepository, times(1)).save(any(Post.class)),
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getId()).isEqualTo(postId)
        );
    }

    @Test
    @DisplayName("post 조회")
    void readOne() {
        // given
        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));

        // when
        final PostDto postDto = postService.readOne(postId);

        // then
        assertAll(
                () -> verify(postRepository, times(1)).findById(postId),
                () -> assertThat(postDto).isNotNull(),
                () -> assertThat(postDto.getPostId()).isEqualTo(postId),
                () -> assertThat(postDto.getContent()).isEqualTo(content)
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
        assertThatThrownBy(() -> postService.readOne(invalidPostId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("post를 찾을 수 없습니다. post id: " + invalidPostId);
    }

}
