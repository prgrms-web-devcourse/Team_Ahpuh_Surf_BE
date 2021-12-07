package org.ahpuh.backend.category.repository;

import org.ahpuh.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
//    Optional<List<Category>> findByUser(User user);
}
