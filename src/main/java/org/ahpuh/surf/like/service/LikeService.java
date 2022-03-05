package org.ahpuh.surf.like.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.like.domain.LikeConverter;
import org.ahpuh.surf.like.domain.LikeRepository;
import org.ahpuh.surf.like.dto.response.LikeResponseDto;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeConverter likeConverter;

    public LikeResponseDto like(final Long userId, final Long postId) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Post postEntity = postRepository.findById(postId)
                .orElseThrow(() -> EntityExceptionHandler.PostNotFound(postId));

        final Long likeId = likeRepository.save(likeConverter.toEntity(userEntity, postEntity))
                .getLikeId();

        return new LikeResponseDto(likeId);
    }

    public void unlike(final Long postId, final Long likeId) {
        final Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요한 기록이 없습니다." + likeId));
        if (!Objects.equals(like.getPost().getPostId(), postId)) {
            throw new IllegalArgumentException("The post ID does not match. " + postId);
        }
        likeRepository.deleteById(likeId);
    }
}
