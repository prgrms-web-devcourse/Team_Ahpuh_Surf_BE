package org.ahpuh.surf.category.domain.repository;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryQuerydsl {

    List<Category> findByUser(User user);

}
