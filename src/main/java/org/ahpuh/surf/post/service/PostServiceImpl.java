package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.post.converter.PostConverter;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponse;
import org.ahpuh.surf.post.dto.PostRequest;
import org.ahpuh.surf.post.dto.PostResponseDto;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final PostConverter postConverter;

    @Override
    @Transactional
    public PostIdResponse create(final PostRequest request) {
        // TODO: 1. category aop 적용     2. category의 최근 게시글 점수 컬럼 update
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = postConverter.toEntity(category, request);
        final Post saved = postRepository.save(post);

        return new PostIdResponse(saved.getId());
    }

    @Override
    @Transactional
    public PostIdResponse update(final Long postId, final PostRequest request) {
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = getPostById(postId);
        post.editPost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore(), request.getFileUrl());

        return new PostIdResponse(postId);
    }

    @Override
    public PostDto readOne(final Long postId) {
        final Post post = getPostById(postId);
        return postConverter.toDto(post);
    }

    @Override
    @Transactional
    public void delete(final Long postId) {
        final Post post = getPostById(postId);
        post.delete();
    }

    @Override
    public List<PostResponseDto> getPost(final Long userId, final Integer year, final Integer month) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final List<Post> postList = postRepository.findAllByUserAndSelectedDateBetweenOrderBySelectedDate(user, LocalDate.of(year, month, 1), LocalDate.of(year, month, 31));

        return postList.stream()
                .map((Post post) -> postConverter.toPostResponseDto(post, post.getCategory()))
                .toList();
    }

    @Override
    public List<PostResponseDto> getAllPost(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));

        final List<Post> postList = postRepository.findAllByUserOrderBySelectedDateDesc(user);

        return postList.stream()
                .map((Post post) -> postConverter.toPostResponseDto(post, post.getCategory()))
                .toList();
    }

    @Override
    public List<PostResponseDto> getAllPostByCategory(final Long userId, final Long categoryId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));

        final List<Post> postList = postRepository.findAllByUserAndCategoryOrderBySelectedDateDesc(user, category);

        return postList.stream()
                .map((Post post) -> postConverter.toPostResponseDto(post, category))
                .toList();
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> EntityExceptionHandler.PostNotFound(postId));
    }
}
