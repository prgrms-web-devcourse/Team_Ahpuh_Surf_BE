package org.ahpuh.surf.like.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.exception.like.DuplicatedLikeException;
import org.ahpuh.surf.common.exception.like.LikeNotFoundException;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
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

@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeConverter likeConverter;

    public LikeResponseDto like(final Long userId, final Long postId) {
        final User user = getUser(userId);
        final Post post = getPost(postId);
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new DuplicatedLikeException();
        }
        final Like likeEntity = likeRepository.save(likeConverter.toEntity(user, post));

        return new LikeResponseDto(likeEntity.getLikeId());
    }

    public void unlike(final Long likeId) {
        final Like like = likeRepository.findById(likeId)
                .orElseThrow(LikeNotFoundException::new);

        likeRepository.delete(like);
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Post getPost(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }
}
