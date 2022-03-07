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
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.*;
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

    public List<PostCountResponseDto> getCountsPerDayWithYear(final int year, final Long userId) {
        final User user = getUser(userId);
        return postRepository.findAllDateAndCountBetween(year, user);
    }

    public List<CategorySimpleDto> getScoresOfCategoryByUser(final Long userId) {
        final User user = getUser(userId);
        final List<PostScoreCategoryDto> posts = postRepository.findAllScoreWithCategoryByUser(user);
        final List<Category> categories = categoryRepository.findByUser(user);
        return postConverter.sortPostScoresByCategory(posts, categories);
    }

    public CursorResult<RecentPostResponseDto> recentAllPosts(final Long userId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<RecentPostResponseDto> postDtos = findPost == null
                ? postRepository.findAllRecentPost(userId, PAGE)
                : postRepository.findAllRecentPostByCursor(userId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(postDtos);
        postDtos.forEach(RecentPostResponseDto::likeCheck);

        return new CursorResult<>(postDtos, hasNext);
    }

    public CursorResult<ExploreResponseDto> followExplore(final Long userId, final Long cursorId) {
        final User user = getUser(userId);
        if (user.getFollowing().isEmpty()) {
            return new CursorResult<>(List.of(), false);
        }

        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<ExploreResponseDto> exploreDtos = findPost == null
                ? postRepository.findFollowingPosts(userId, PAGE)
                : postRepository.findFollowingPostsByCursor(userId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(exploreDtos);
        exploreDtos.forEach(ExploreResponseDto::likeCheck);

        return new CursorResult<>(exploreDtos, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByUser(final Long userId, final Long postUserId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<AllPostResponseDto> postDtos = findPost == null
                ? postRepository.findAllPostOfUser(userId, postUserId, PAGE)
                : postRepository.findAllPostOfUserByCursor(userId, postUserId, findPost.getSelectedDate(), findPost.getCreatedAt(), PAGE);

        final boolean hasNext = hasNextCheck(postDtos);
        postDtos.forEach(AllPostResponseDto::likeCheck);

        return new CursorResult<>(postDtos, hasNext);
    }

    public CursorResult<AllPostResponseDto> getAllPostByCategory(final Long userId, final Long categoryId, final Long cursorId) {
        final Post findPost = (cursorId == 0 ? null : getPost(cursorId));
        final List<AllPostResponseDto> postDtos = findPost == null
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
        boolean hasNext = false;
        if (postList.size() == PAGE.getPageSize()) {
            hasNext = true;
            postList.remove(PAGE.getPageSize() - 1);
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
