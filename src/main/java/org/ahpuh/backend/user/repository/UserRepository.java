package org.ahpuh.backend.user.repository;

import org.ahpuh.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //    @Query("select u from User u join Permission where u.email = :email")
    Optional<User> findByEmail(String email);

}
