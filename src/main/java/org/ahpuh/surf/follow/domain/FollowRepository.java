package org.ahpuh.surf.follow.domain;

import org.ahpuh.surf.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserAndFollowedUser(User me, User followedUser);

    List<Follow> findByUser(User user);

    List<Follow> findByFollowedUser(User user);

    long countByUser(User user);

    long countByFollowedUser(User user);

}
