package org.ahpuh.surf.follow.domain.repository;

import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryQuerydsl {

    boolean existsBySourceAndTarget(User source, User target);

}
