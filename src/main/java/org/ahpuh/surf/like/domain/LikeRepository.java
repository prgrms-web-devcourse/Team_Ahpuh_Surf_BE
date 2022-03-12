package org.ahpuh.surf.like.domain;

import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndPost(User user, Post post);

}
