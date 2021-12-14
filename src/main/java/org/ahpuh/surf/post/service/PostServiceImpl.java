package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.ahpuh.surf.post.converter.PostConverter;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;
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
    private final LikeRepository likeRepository;

    @Transactional
    public Long create(final Long userId, final PostRequestDto request) {
        final User user = getUserById(userId);
        final Category category = getCategoryById(request.getCategoryId());

        final Post post = PostConverter.toEntity(user, category, request);
        final Post saved = postRepository.save(post);

        return saved.getPostId();
    }

    @Transactional
    public Long update(final Long postId, final PostRequestDto request) {
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = getPostById(postId);
        post.editPost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore(), request.getFileUrl());

        return postId;
    }

    public PostDto readOne(final Long postId) {
        final Post post = getPostById(postId);
        return PostConverter.toDto(post);
    }

    @Transactional
    public void delete(final Long postId) {
        final Post post = getPostById(postId);
        post.delete();
    }

    @Transactional
    public Long clickFavorite(final Long userId, final Long postId) {
        final Post post = getPostById(postId);
        post.updateFavorite(userId);
        return post.getPostId();
    }

    @Override
    public List<FollowingPostDto> explore(final Long userId) {
        final List<FollowingPostDto> followingPostDtos = postRepository.followingPosts(userId);
        for (final FollowingPostDto dto : followingPostDtos) {
            dto.likedCheck(likeRepository.findByUserIdAndPostId(userId, dto.getPostId()));
        }
        return followingPostDtos;
    }

    private User getUserById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
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
