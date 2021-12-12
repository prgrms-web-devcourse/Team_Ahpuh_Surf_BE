package org.ahpuh.surf.follow.repository;

import org.ahpuh.surf.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
