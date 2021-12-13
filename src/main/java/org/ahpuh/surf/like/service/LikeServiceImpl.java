package org.ahpuh.surf.like.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.like.converter.LikeConverter;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private final LikeConverter likeConverter;

    @Override
    public Long like(final Long userId, final Long postId) {
        return likeRepository.save(likeConverter.toEntity(userId, postId))
                .getLikeId();
    }

    @Override
    public void unlike(final Long likeId) {
        likeRepository.deleteById(likeId);
    }

}