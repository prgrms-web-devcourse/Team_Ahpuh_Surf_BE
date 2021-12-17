package org.ahpuh.surf.follow.repository;

import org.ahpuh.surf.follow.entity.Follow;
import org.ahpuh.surf.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserAndAndFollowedUser(User me, User followdUser);

    List<Follow> findByUser(User user);

    List<Follow> findByFollowedUser(User user);

}
