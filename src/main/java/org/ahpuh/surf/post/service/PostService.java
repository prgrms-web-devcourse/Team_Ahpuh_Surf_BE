package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.category.NoCategoryFromUserException;
import org.ahpuh.surf.common.exception.post.CancelFavoriteFailException;
import org.ahpuh.surf.common.exception.post.MakeFavoriteFailException;
import org.ahpuh.surf.common.exception.post.NotMatchingPostByUserException;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.common.exception.s3.UploadFailException;
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
import org.springframework.data.domain.PageRequest;
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

    private final Pageable PAGE = PageRequest.of(0, 11);

    @Transactional
    public Long create(final Long userId, final PostRequestDto request, final MultipartFile file) {
        final FileStatus fileStatus = fileUpload(file);
        final User user = getUser(userId);
        final Category category = user.getCategories()
                .stream()
                .filter(categoryFromUser -> categoryFromUser.getCategoryId().equals(request.getCategoryId()))
                .findFirst()
                .orElseThrow(NoCategoryFromUserException::new);

        final Post post = postConverter.toEntity(user, category, request);
        updateFileInfo(fileStatus, post);

        return postRepository.save(post)
                .getPostId();
    }

    @Transactional
    public void update(final Long postId, final PostRequestDto request, final MultipartFile file) {
        final Category category = getCategory(request.getCategoryId());
        final Post post = getPost(postId);
        post.updatePost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore());

        final FileStatus fileStatus = fileUpload(file);
        updateFileInfo(fileStatus, post);
    }

    public PostReadResponseDto readOne(final Long userId, final Long postId) {
        final Post post = getPost(postId);
        return postConverter.toPostReadResponseDto(post, userId);
    }

    @Transactional
    public void delete(final Long userId, final Long postId) {
        final Post post = getPost(postId);
        if (!post.getUser().getUserId().equals(userId)) {
            throw new NotMatchingPostByUserException();
        }
        postRepository.delete(post);
    }

    @Transactional
    public void makeFavorite(final Long userId, final Long postId) {
        final Post post = getPost(postId);
        if (post.getFavorite()) {
            throw new MakeFavoriteFailException();
        }
        post.updateFavorite(userId);
    }

    @Transactional
    public void cancelFavorite(final Long userId, final Long postId) {
        final Post post = getPost(postId);
        if (!post.getFavorite()) {
            throw new CancelFavoriteFailException();
        }
        post.updateFavorite(userId);
    }

    public List<PostCountDto> getCountsPerDayWithYear(final int year, final Long userId) {
        final User user = getUser(userId);
        return postRepository.findAllDateAndCountBetween(year, user);
    }

    public List<CategorySimpleDto> getScoresWithCategoryByUser(final Long userId) {
        final User user = getUser(userId);
        final List<PostScoreCategoryDto> posts = postRepository.findAllScoreWithCategoryByUser(user);
        final List<Category> categories = categoryRepository.findByUser(user);
        return postConverter.sortPostScoresByCategory(posts, categories);
    }

    public List<PostResponseDto> getPostOfPeriod(final Long userId, final Integer year, final Integer month) {
        final User user = getUser(userId);
        final LocalDate start = LocalDate.of(year, month, 1);
        final LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        final List<Post> postList = postRepository.findAllByUserAndSelectedDateBetweenOrderBySelectedDate(user, start, end);

        return postList.stream()
                .map((Post post) -> postConverter.toPostResponseDto(post, post.getCategory()))
                .toList();
    }

    public PostsRecentScoreResponseDto getRecentScore(final Long categoryId) {
        final Category category = getCategory(categoryId);
        final Integer recentScore = postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category)
                .getScore();

        return new PostsRecentScoreResponseDto(recentScore);
    }

    public CursorResult<ExploreDto> followingExplore(final Long userId, final Long cursorId) {
        final User user = getUser(userId);
        if (user.getFollowing().isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<ExploreDto> exploreDtos = findPost == null
                ? postRepository.findFollowingPosts(userId, PAGE)
                : postRepository.findNextFollowingPosts(userId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);
        if (exploreDtos.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }
        final boolean hasNext = hasNextCheck(exploreDtos);

        for (final ExploreDto dto : exploreDtos) {
            user.getLikes()
                    .stream()
                    .filter(like -> like.getPost().getPostId().equals(dto.getPostId()))
                    .findFirst()
                    .ifPresent(like -> dto.setLiked(like.getLikeId()));
        }
        return new CursorResult<>(exploreDtos, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByUser(final Long userId, final Long postUserId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));

        final List<AllPostResponseDto> postList = findPost == null
                ? postRepository.findAllPostResponse(userId, postUserId, PAGE)
                : postRepository.findAllPostResponseByCursor(userId, postUserId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(postList);
        postList.forEach(AllPostResponseDto::likeCheck);

        return new CursorResult<>(postList, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByCategory(final Long myId, final Long categoryId, final Long cursorId) {
        final Category category = getCategory(categoryId);
        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null
                ? postRepository.findAllByCategoryOrderBySelectedDateDesc(category, PAGE)
                : postRepository.findByCategoryAndSelectedDateLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(category, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);
        if (postList.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final boolean hasNext = hasNextCheck(postList);
        final List<AllPostResponseDto> posts = postList.stream()
                .map(post -> postConverter.toAllPostResponseDto(post, myId))
                .toList();

        return new CursorResult<>(posts, hasNext);
    }

    public CursorResult<RecentPostDto> recentAllPosts(final Long myId, final Long cursorId) {
        final User me = getUser(myId);
        final Post findPost = postRepository.findById(cursorId).orElse(null);

        final List<Post> postList = findPost == null
                ? postRepository.findTop10ByCreatedAtIsLessThanEqualOrderByCreatedAtDesc(LocalDateTime.now(), PAGE)
                : postRepository.findTop10ByCreatedAtIsLessThanOrderByCreatedAtDesc(findPost.getCreatedAt(), PAGE);
        if (postList.isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }
        final boolean hasNext = hasNextCheck(postList);

        final List<RecentPostDto> posts = postList.stream()
                .map(postEntity -> postConverter.toRecentAllPosts(postEntity, me))
                .toList();

        return new CursorResult<>(posts, hasNext);
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Category getCategory(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private Post getPost(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private boolean hasNextCheck(final List<?> postList) {
        boolean hasNext = false;
        if (postList.size() == 11) {
            hasNext = true;
            postList.remove(10);
        }
        return hasNext;
    }

    private void updateFileInfo(final FileStatus fileStatus, final Post post) {
        if (fileStatus != null) {
            post.updateFile(fileStatus);
        }
    }

    private FileStatus fileUpload(final MultipartFile file) {
        FileStatus fileStatus = null;
        if (file != null) {
            try {
                fileStatus = s3Service.uploadPostFile(file);
            } catch (final IOException e) {
                log.info("파일이 존재하지 않습니다.");
                e.printStackTrace();
            } catch (final Exception e) {
                throw new UploadFailException();
            }
        }
        return fileStatus;
    }
}
