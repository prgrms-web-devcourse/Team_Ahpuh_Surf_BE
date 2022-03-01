package org.ahpuh.surf.post.domain.repository;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {

    List<Post> findAllByUserOrderBySelectedDateDesc(User user, Pageable page);

    List<Post> findAllByUserAndCategoryOrderBySelectedDateDesc(User user, Category category, Pageable page);

    List<Post> findAllByUserAndSelectedDateBetweenOrderBySelectedDate(User user, LocalDate start, LocalDate end);

    List<Post> findByUserAndSelectedDateIsLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(User user, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    List<Post> findByUserAndCategoryAndSelectedDateLessThanEqualAndCreatedAtLessThanOrderBySelectedDateDesc(User user, Category category, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    Post findTop1ByCategoryOrderBySelectedDateDesc(Category category);

    List<Post> findByCategory(Category category);

    List<Post> findTop10ByCreatedAtIsLessThanEqualOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable page);

    List<Post> findTop10ByCreatedAtIsLessThanOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable page);

}