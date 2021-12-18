package org.ahpuh.surf.like.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.like.converter.LikeConverter;
import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final LikeConverter likeConverter;

    @Override
    public Long like(final Long userId, final Long postId) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Post postEntity = postRepository.findById(postId)
                .orElseThrow(() -> EntityExceptionHandler.PostNotFound(postId));
        return likeRepository.save(likeConverter.toEntity(userEntity, postEntity))
                .getLikeId();
    }

    @Override
    public void unlike(final Long postId, final Long likeId) {
        final Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요한 기록이 없습니다." + likeId));
        if (!Objects.equals(like.getPost().getPostId(), postId)) {
            throw new IllegalArgumentException("The post ID does not match. " + postId);
        }
        likeRepository.deleteById(likeId);
    }

}
