package org.ahpuh.surf.post.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.category.NoCategoryFromUserException;
import org.ahpuh.surf.common.exception.post.*;
import org.ahpuh.surf.common.exception.s3.UploadFailException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.PostConverter;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.service.S3Service;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        final User user = getUser(userId);
        final Category category = user.getCategories()
                .stream()
                .filter(categoryFromUser -> categoryFromUser.getCategoryId().equals(request.getCategoryId()))
                .findFirst()
                .orElseThrow(NoCategoryFromUserException::new);
        final Post post = postConverter.toEntity(user, category, request);

        fileUpload(file).ifPresent(post::updateFile);

        return postRepository.save(post)
                .getPostId();
    }

    @Transactional
    public void update(final Long postId, final PostRequestDto request, final MultipartFile file) {
        final Category category = getCategory(request.getCategoryId());
        final Post post = getPost(postId);
        post.updatePost(category, LocalDate.parse(request.getSelectedDate()), request.getContent(), request.getScore());

        fileUpload(file).ifPresent(post::updateFile);
    }

    public PostReadResponseDto readPost(final Long userId, final Long postId) {
        return postRepository.findPost(postId, userId)
                .map(PostReadResponseDto::likeCheck)
                .orElseThrow(PostNotFoundException::new);
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

    public List<PostsOfMonthResponseDto> getPostsOfMonth(final Long userId, final Integer year, final Integer month) {
        if (Objects.isNull(year) | Objects.isNull(month)) {
            throw new InvalidPeriodException();
        } else if (year < 1900 | month > 12 | month < 1) {
            throw new InvalidPeriodException();
        }
        final LocalDate startDate = LocalDate.of(year, month, 1);
        final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return postRepository.findPostsOfMonth(userId, startDate, endDate);
    }

    public PostsRecentScoreResponseDto getRecentScore(final Long categoryId) {
        final Category category = getCategory(categoryId);
        Optional<Post> findedPost = postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category);
        
        return findedPost.isEmpty()
                ? new PostsRecentScoreResponseDto(null)
                : new PostsRecentScoreResponseDto(findedPost.get().getScore());
    }

    public List<PostCountResponseDto> getPostCountsOfYear(final int year, final Long userId) {
        final User user = getUser(userId);
        return postRepository.findEachDateAndCountOfYearByUser(year, user);
    }

    public List<CategorySimpleDto> getScoresOfCategoryByUser(final Long userId) {
        final User user = getUser(userId);
        final List<PostScoreCategoryDto> posts = postRepository.findAllScoreWithCategoryByUser(user);
        final List<Category> categories = categoryRepository.findByUser(user);
        return postConverter.sortPostScoresByCategory(posts, categories);
    }

    public CursorResult<RecentPostResponseDto> recentAllPosts(final Long userId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<RecentPostResponseDto> postDtos = Objects.isNull(findPost)
                ? postRepository.findAllRecentPost(userId, PAGE)
                : postRepository.findAllRecentPostByCursor(userId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(postDtos);
        postDtos.forEach(RecentPostResponseDto::likeCheck);

        return new CursorResult<>(postDtos, hasNext);
    }

    public CursorResult<ExploreResponseDto> followExplore(final Long userId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<ExploreResponseDto> exploreDtos = Objects.isNull(findPost)
                ? postRepository.findFollowingPosts(userId, PAGE)
                : postRepository.findFollowingPostsByCursor(userId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(exploreDtos);
        exploreDtos.forEach(ExploreResponseDto::likeCheck);

        return new CursorResult<>(exploreDtos, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByUser(final Long userId, final Long postUserId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<AllPostResponseDto> postDtos = Objects.isNull(findPost)
                ? postRepository.findAllPostOfUser(userId, postUserId, PAGE)
                : postRepository.findAllPostOfUserByCursor(userId, postUserId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(postDtos);
        postDtos.forEach(AllPostResponseDto::likeCheck);

        return new CursorResult<>(postDtos, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByCategory(final Long userId, final Long categoryId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<AllPostResponseDto> postDtos = Objects.isNull(findPost)
                ? postRepository.findAllPostOfCategory(userId, categoryId, PAGE)
                : postRepository.findAllPostOfCategoryByCursor(userId, categoryId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(postDtos);
        postDtos.forEach(AllPostResponseDto::likeCheck);

        return new CursorResult<>(postDtos, hasNext);
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
        if (postList.size() == PAGE.getPageSize()) {
            postList.remove(PAGE.getPageSize() - 1);
            return true;
        }
        return false;
    }

    private Optional<FileStatus> fileUpload(final MultipartFile file) {
        if (file.isEmpty()) {
            return Optional.empty();
        }

        try {
            return s3Service.uploadPostFile(file);
        } catch (final ApplicationException e) {
            e.printStackTrace();
            throw e;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new UploadFailException();
        }
    }
}
