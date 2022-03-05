package org.ahpuh.surf.user.domain.follow;

import org.ahpuh.surf.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findBySourceAndTarget(User source, User target);

    List<Follow> findBySource(User source);

    List<Follow> findByTarget(User target);

    long countBySource(User source);

    long countByTarget(User target);

}
