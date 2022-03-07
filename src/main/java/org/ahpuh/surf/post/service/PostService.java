package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.PostConverter;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.ExploreDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.RecentPostDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.AllPostResponseDto;
import org.ahpuh.surf.post.dto.response.PostReadResponseDto;
import org.ahpuh.surf.post.dto.response.PostResponseDto;
import org.ahpuh.surf.post.dto.response.PostsRecentScoreResponseDto;
import org.ahpuh.surf.s3.FileStatus;
import org.ahpuh.surf.s3.S3Service;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PostConverter postConverter;

    @Transactional
    public Long create(final Long userId, final PostRequestDto request, final MultipartFile file) {
        FileStatus fileStatus = null;
        if (file != null) {
            try {
                fileStatus = s3Service.uploadPostFile(file);
            } catch (final IOException e) {
                log.info("파일이 존재하지 않습니다.");
                e.printStackTrace();
            }
        }
        final User user = getUserById(userId);
        final Category category = getCategoryById(request.getCategoryId());

        final Post post = postConverter.toEntity(user, category, request);
        if (fileStatus != null) {
            post.updateFile(fileStatus);
        }
        return postRepository.save(post)
                .getPostId();
    }

    @Transactional
    public void update(final Long postId, final PostRequestDto request, final MultipartFile file) {
        FileStatus fileStatus = null;
        if (file != null) {
            try {
                fileStatus = s3Service.uploadPostFile(file);
            } catch (final IOException e) {
                log.info("파일이 존재하지 않습니다.");
                e.printStackTrace();
            }
        }
        final Category category = getCategoryById(request.getCategoryId());
        final Post post = getPostById(postId);

        post.updatePost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore());
        if (fileStatus != null) {
            post.updateFile(fileStatus);
        }
    }

    public PostReadResponseDto readOne(final Long myId, final Long postId) {
        return postConverter.toPostReadResponseDto(getPostById(postId), myId);
    }

    @Transactional
    public void delete(final Long postId) {
        final Post post = getPostById(postId);
        postRepository.delete(post);
    }

    @Transactional
    public void makeFavorite(final Long userId, final Long postId) {
        final Post post = getPostById(postId);
        post.updateFavorite(userId);
    }

    @Transactional
    public void cancelFavorite(final Long userId, final Long postId) {
        final Post post = getPostById(postId);
        post.updateFavorite(userId);
    }

    public CursorResult<ExploreDto> followingExplore(final Long myId, final Long cursorId, final Pageable page) {
        final User me = userRepository.findById(myId)
                .orElseThrow(UserNotFoundException::new);
        if (me.getFollowing().isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<ExploreDto> exploreDtos = findPost == null
                ? postRepository.findFollowingPosts(myId, page)
                : postRepository.findNextFollowingPosts(myId, findPost.getSelectedDate(), findPost.getCreatedAt(), page);

        for (final ExploreDto dto : exploreDtos) {
            me.getLikes()
                    .stream()
                    .filter(like -> like.getPost().getPostId().equals(dto.getPostId()))
                    .findFirst()
                    .ifPresent(like -> dto.setLiked(like.getLikeId()));
        }

        if (exploreDtos.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        } else {
            final ExploreDto lastExploreDto = exploreDtos.get(exploreDtos.size() - 1);
            final boolean hasNext = !postRepository.findNextFollowingPosts(myId, lastExploreDto.getSelectedDate(), lastExploreDto.getCreatedAt(), page).isEmpty();
            return new CursorResult<>(exploreDtos, hasNext);
        }

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
                .orElseThrow(UserNotFoundException::new);
    }

    public List<PostResponseDto> getPostOfPeriod(final Long userId, final Integer year, final Integer month) {
        final User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        final LocalDate start = LocalDate.of(year, month, 1);
        final LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        final List<Post> postList = postRepository.findAllByUserAndSelectedDateBetweenOrderBySelectedDate(user, start, end);

        return postList.stream()
                .map((Post post) -> postConverter.toPostResponseDto(post, post.getCategory()))
                .toList();
    }

    public CursorResult<AllPostResponseDto> getAllPost(final Long myId, final Long userId, final Long cursorId, final Pageable page) {
        final User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null
                ? postRepository.findAllByUserOrderBySelectedDateDesc(user, page)
                : postRepository.findByUserAndSelectedDateIsLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(user, findPost.getSelectedDate(), findPost.getCreatedAt(), page);

        if (postList.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final List<AllPostResponseDto> posts = postList.stream()
                .map(post -> postConverter.toAllPostResponseDto(post, myId))
                .toList();

        final Post lastPost = postList.get(postList.size() - 1);
        final boolean hasNext = !postRepository.findByUserAndSelectedDateIsLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(
                        user,
                        lastPost.getSelectedDate(),
                        lastPost.getCreatedAt(),
                        page)
                .isEmpty();

        return new CursorResult<>(posts, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByCategory(final Long myId, final Long userId, final Long categoryId, final Long cursorId, final Pageable page) {
        final User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null
                ? postRepository.findAllByUserAndCategoryOrderBySelectedDateDesc(user, category, page)
                : postRepository.findByUserAndCategoryAndSelectedDateLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(user, category, findPost.getSelectedDate(), findPost.getCreatedAt(), page);

        if (postList.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final List<AllPostResponseDto> posts = postList.stream()
                .map(post -> postConverter.toAllPostResponseDto(post, myId))
                .toList();

        final Post lastPost = postList.get(postList.size() - 1);
        final boolean hasNext = !postRepository.findByUserAndCategoryAndSelectedDateLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(
                        user,
                        category,
                        lastPost.getSelectedDate(),
                        lastPost.getCreatedAt(),
                        page)
                .isEmpty();

        return new CursorResult<>(posts, hasNext);
    }

    public PostsRecentScoreResponseDto getRecentScore(final Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
        final Integer recentScore = postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category)
                .getScore();

        return new PostsRecentScoreResponseDto(recentScore);
    }

    private Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    public CursorResult<RecentPostDto> recentAllPosts(final Long myId, final Long cursorId, final Pageable page) {
        final User me = userRepository.findById(myId)
                .orElseThrow(UserNotFoundException::new);
        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null
                ? postRepository.findTop10ByCreatedAtIsLessThanEqualOrderByCreatedAtDesc(LocalDateTime.now(), page)
                : postRepository.findTop10ByCreatedAtIsLessThanOrderByCreatedAtDesc(findPost.getCreatedAt(), page);

        if (postList.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final List<RecentPostDto> posts = postList.stream()
                .map(postEntity -> postConverter.toRecentAllPosts(postEntity, me))
                .toList();

        final Post lastPost = postList.get(postList.size() - 1);
        final boolean hasNext = !postRepository.findTop10ByCreatedAtIsLessThanOrderByCreatedAtDesc(
                        lastPost.getCreatedAt(),
                        page)
                .isEmpty();

        return new CursorResult<>(posts, hasNext);
    }
}
