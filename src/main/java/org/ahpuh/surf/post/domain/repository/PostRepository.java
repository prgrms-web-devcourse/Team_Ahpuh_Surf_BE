package org.ahpuh.surf.post.domain.repository;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {

    List<Post> findAllByUserOrderBySelectedDateDesc(User user, Pageable page);

    List<Post> findAllByCategoryOrderBySelectedDateDesc(Category category, Pageable page);

    List<Post> findAllByUserAndSelectedDateBetweenOrderBySelectedDate(User user, LocalDate start, LocalDate end);

    Post findTop1ByCategoryOrderBySelectedDateDesc(Category category);

}
