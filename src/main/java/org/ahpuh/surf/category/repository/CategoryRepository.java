package org.ahpuh.surf.category.repository;

import org.ahpuh.surf.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
//    Optional<List<Category>> findByUser(User user);
}
