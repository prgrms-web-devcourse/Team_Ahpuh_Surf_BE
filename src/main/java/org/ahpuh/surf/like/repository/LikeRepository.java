package org.ahpuh.surf.like.repository;

import org.ahpuh.surf.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

//    List<Like> findAllByUserId(Long userId);

}
