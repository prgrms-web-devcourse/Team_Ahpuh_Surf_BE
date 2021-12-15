package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.post.converter.PostConverter;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponse;
import org.ahpuh.surf.post.dto.PostRequest;
import org.ahpuh.surf.post.dto.PostResponseDto;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
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
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    private final PostConverter postConverter;

    @Override
    @Transactional
    public PostIdResponse create(final PostRequest request) {
        // TODO: 1. category aop 적용     2. category의 최근 게시글 점수 컬럼 update
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = PostConverter.toEntity(category, request);
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
        return PostConverter.toDto(post);
    }

    @Override
    @Transactional
    public void delete(final Long postId) {
        final Post post = getPostById(postId);
        post.delete();
    }

    @Override
    public List<FollowingPostDto> explore(final Long userId) {
        final List<FollowingPostDto> followingPostDtos = postRepository.followingPosts(userId);
        for (final FollowingPostDto dto : followingPostDtos) {
            dto.likedCheck(likeRepository.findByUserIdAndPostId(userId, dto.getPostId()));
        }
        return followingPostDtos;
    }

    @Override
    public List<PostResponseDto> getPost(final Long userId, final Integer year, final Integer month) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final List<Post> postList = postRepository.findAllByUserAndSelectedDateBetweenOrderBySelectedDate(user, LocalDate.of(year, month, 1), LocalDate.of(year, month, 31));

        return postList.stream()
                .map((Post post) -> PostConverter.toPostResponseDto(post, post.getCategory()))
                .toList();
    }

    @Override
    public CursorResult<PostResponseDto> getAllPost(final Long userId, final Long cursorId, final Pageable page) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));

        final List<Post> postList = cursorId == null ?
                postRepository.findAllByUserOrderBySelectedDateDesc(user, page) :
                postRepository.findByUserAndIdLessThanOrderBySelectedDateDesc(user, cursorId, page);

        final Long lastIdOfIndex = postList.isEmpty() ?
                null : postList.get(postList.size() - 1).getId();

        final List<PostResponseDto> posts = postList.stream()
                .map((Post post) -> PostConverter.toPostResponseDto(post, post.getCategory()))
                .toList();

        return new CursorResult<>(posts, hasNext(lastIdOfIndex));
    }

    @Override
    public CursorResult<PostResponseDto> getAllPostByCategory(final Long userId, final Long categoryId, final Long cursorId, final Pageable page) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));

        final List<Post> postList = cursorId == null ?
                postRepository.findAllByUserAndCategoryOrderBySelectedDateDesc(user, category, page) :
                postRepository.findByUserAndCategoryAndIdLessThanOrderBySelectedDateDesc(user, category, cursorId, page);

        final Long lastIdOfIndex = postList.isEmpty() ?
                null : postList.get(postList.size() - 1).getId();

        final List<PostResponseDto> posts = postList.stream()
                .map((Post post) -> PostConverter.toPostResponseDto(post, category))
                .toList();

        return new CursorResult<>(posts, hasNext(lastIdOfIndex));
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> EntityExceptionHandler.PostNotFound(postId));
    }

    private Boolean hasNext(final Long id) {
        return id != null && postRepository.existsByIdLessThanOrderBySelectedDate(id);
    }

}
