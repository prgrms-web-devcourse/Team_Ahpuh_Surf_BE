package org.ahpuh.backend.post.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.backend.category.entity.Category;
import org.ahpuh.backend.category.repository.CategoryRepository;
import org.ahpuh.backend.common.exception.NotFoundException;
import org.ahpuh.backend.post.converter.PostConverter;
import org.ahpuh.backend.post.dto.PostDto;
import org.ahpuh.backend.post.dto.PostIdResponse;
import org.ahpuh.backend.post.dto.PostRequest;
import org.ahpuh.backend.post.entity.Post;
import org.ahpuh.backend.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Transactional
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostIdResponse create(final PostRequest request) {
        // TODO: 1. category aop 적용     2. category의 최근 게시글 점수 컬럼 update
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = PostConverter.toEntity(category, request);
        final Post saved = postRepository.save(post);

        return new PostIdResponse(saved.getId());
    }

    public PostIdResponse update(final Long postId, final PostRequest request) {
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = getPostById(postId);
        post.editPost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore(), request.getFileUrl());

        return new PostIdResponse(postId);
    }

    @Transactional(readOnly = true)
    public PostDto readOne(final Long postId) {
        final Post post = getPostById(postId);
        return PostConverter.toDto(post);
    }

    public void delete(final Long postId) {
        final Post post = getPostById(postId);
        post.setIsDeleted(true);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category를 찾을 수 없습니다. post id: " + categoryId));
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post를 찾을 수 없습니다. post id: " + postId));
    }

}