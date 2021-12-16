package org.ahpuh.surf.like.repository;

import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPost(Long userId, Post post);

}
