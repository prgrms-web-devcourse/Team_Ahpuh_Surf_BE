package org.ahpuh.backend.user.repository;

import org.ahpuh.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
