package org.ahpuh.surf.category.repository;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<List<Category>> findByUser(User user);
}
