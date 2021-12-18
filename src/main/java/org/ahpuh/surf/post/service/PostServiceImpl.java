package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.common.s3.S3ServiceImpl.FileStatus;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.ahpuh.surf.post.converter.PostConverter;
import org.ahpuh.surf.post.dto.*;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final PostConverter postConverter;

    @Transactional
    public Long create(final Long userId, final PostRequestDto request, final FileStatus fileStatus) {
        final User user = getUserById(userId);
        final Category category = getCategoryById(request.getCategoryId());

        final Post post = postConverter.toEntity(user, category, request, fileStatus);
        final Post saved = postRepository.save(post);

        return saved.getPostId();
    }

    @Transactional
    public Long update(final Long postId, final PostRequestDto request, final FileStatus fileStatus) {
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = getPostById(postId);
        post.editPost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore());
        if (fileStatus != null) {
            post.editFile(fileStatus);
        }

        return postId;
    }

    public PostDto readOne(final Long myId, final Long postId) {
        final Post post = getPostById(postId);
        return postConverter.toDto(post, likeRepository.findByUserIdAndPost(myId, post));
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
    public CursorResult<ExploreDto> followingExplore(final Long myId, final Long cursorId, final Pageable page) {
        final User me = userRepository.findById(myId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(myId));
        if (followRepository.findByUser(me).isEmpty()) {
            final List<ExploreDto> emptyList = new ArrayList<>();
            emptyList.add(ExploreDto.builder().build());
            return new CursorResult<>(emptyList, false);
        }

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<ExploreDto> exploreDtos = findPost == null ?
                postRepository.findFollowingPosts(myId, page) :
                postRepository.findNextFollowingPosts(myId, findPost.getSelectedDate(), findPost.getCreatedAt(), page);

        for (final ExploreDto dto : exploreDtos) {
            likeRepository.findByUserIdAndPost(myId, getPostById(dto.getPostId()))
                    .ifPresent(like -> dto.setLiked(like.getLikeId()));
        }

        final long lastIdOfIndex = exploreDtos.isEmpty() ? 0 : exploreDtos.get(exploreDtos.size() - 1).getPostId();

        final boolean hasNext = !postRepository.findNextFollowingPosts(
                myId,
                exploreDtos
                        .stream()
                        .filter(post -> post.getPostId().equals(lastIdOfIndex))
                        .findFirst()
                        .get()
                        .getSelectedDate(),
                exploreDtos
                        .stream()
                        .filter(post -> post.getPostId().equals(lastIdOfIndex))
                        .findFirst()
                        .get()
                        .getCreatedAt(),
                page).isEmpty();

        return new CursorResult<>(exploreDtos, hasNext);
    }

    public List<PostCountDto> getCountsPerDayWithYear(final int year, final Long userId) {
        final User user = getUserById(userId);
        return postRepository.findAllDateAndCountBetween(year, user);
    }

    public List<CategorySimpleDto> getScoresWithCategoryByUserId(final Long userId) {
        final User user = getUserById(userId);
        final List<PostScoreCategoryDto> posts = postRepository.findAllScoreWithCategoryByUser(user);
        final List<Category> categories = categoryRepository.findAll();
        return postConverter.sortPostScoresByCategory(posts, categories);
    }

    private User getUserById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
    }

    @Override
    public List<PostResponseDto> getPost(final Long userId, final Integer year, final Integer month) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final LocalDate start = LocalDate.of(year, month, 1);
        final LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        final List<Post> postList = postRepository.findAllByUserAndSelectedDateBetweenOrderBySelectedDate(user, start, end);

        return postList.stream()
                .map((Post post) -> postConverter.toPostResponseDto(post, post.getCategory()))
                .toList();
    }

    @Override
    public CursorResult<AllPostResponseDto> getAllPost(final Long myId, final Long userId, final Long cursorId, final Pageable page) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null ?
                postRepository.findAllByUserOrderBySelectedDateDesc(user, page) :
                postRepository.findByUserAndSelectedDateLessThanAndCreatedAtLessThanOrderBySelectedDateDesc(user, findPost.getSelectedDate(), findPost.getCreatedAt(), page);

        final long lastIdOfIndex = postList.isEmpty() ? 0 : postList.get(postList.size() - 1).getPostId();

        final List<AllPostResponseDto> posts = postList.stream()
                .map(post -> postConverter.toAllPostResponseDto(post, likeRepository.findByUserIdAndPost(myId, post)))
                .toList();

        final boolean hasNext = !postRepository.findByUserAndSelectedDateLessThanAndCreatedAtLessThanOrderBySelectedDateDesc(
                user,
                postList
                        .stream()
                        .filter(post -> post.getPostId().equals(lastIdOfIndex))
                        .findFirst()
                        .get()
                        .getSelectedDate(),
                postList
                        .stream()
                        .filter(post -> post.getPostId().equals(lastIdOfIndex))
                        .findFirst()
                        .get()
                        .getCreatedAt(),
                page).isEmpty();

        return new CursorResult<>(posts, hasNext);
    }

    @Override
    public CursorResult<AllPostResponseDto> getAllPostByCategory(final Long myId, final Long userId, final Long categoryId, final Long cursorId, final Pageable page) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null ?
                postRepository.findAllByUserAndCategoryOrderBySelectedDateDesc(user, category, page) :
                postRepository.findByUserAndCategoryAndSelectedDateLessThanAndCreatedAtLessThanOrderBySelectedDateDesc(user, category, findPost.getSelectedDate(), findPost.getCreatedAt(), page);

        final long lastIdOfIndex = postList.isEmpty() ? 0 : postList.get(postList.size() - 1).getPostId();

        final List<AllPostResponseDto> posts = postList.stream()
                .map(post -> postConverter.toAllPostResponseDto(post, likeRepository.findByUserIdAndPost(myId, post)))
                .toList();

        final boolean hasNext = !postRepository.findByUserAndCategoryAndSelectedDateLessThanAndCreatedAtLessThanOrderBySelectedDateDesc(
                user,
                category,
                postList
                        .stream()
                        .filter(post -> post.getPostId().equals(lastIdOfIndex))
                        .findFirst()
                        .get()
                        .getSelectedDate(),
                postList
                        .stream()
                        .filter(post -> post.getPostId().equals(lastIdOfIndex))
                        .findFirst()
                        .get()
                        .getCreatedAt(),
                page).isEmpty();

        return new CursorResult<>(posts, hasNext);
    }

    public int getRecentScore(final Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
        final Post post = postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category);

        return post.getScore();
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> EntityExceptionHandler.PostNotFound(postId));
    }

    public List<ExploreDto> recentAllPosts(final Long myId) {
        return postRepository.findTop100ByIsDeletedIsFalseOrderByCreatedAtDesc(PageRequest.of(0, 100))
                .stream()
                .map(postEntity -> postConverter.toRecentAllPosts(postEntity, likeRepository.findByUserIdAndPost(myId, postEntity)))
                .collect(Collectors.toList());
    }

}
