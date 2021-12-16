package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {

    List<Post> findAllByUserOrderBySelectedDateDesc(User user, Pageable page);

    List<Post> findAllByUserAndCategoryOrderBySelectedDateDesc(User user, Category category, Pageable page);

    List<Post> findAllByUserAndSelectedDateBetweenOrderBySelectedDate(User user, LocalDate start, LocalDate end);

    List<Post> findByUserAndPostIdLessThanOrderBySelectedDateDesc(User user, Long cursorId, Pageable page);

    List<Post> findByUserAndCategoryAndPostIdLessThanOrderBySelectedDateDesc(User user, Category category, Long cursorId, Pageable page);

    Boolean existsByPostIdLessThanOrderBySelectedDate(Long cursorId);

    Post findTop1ByCategoryOrderBySelectedDateDesc(Category category);

    List<Post> findByCategory(Category category);

}
