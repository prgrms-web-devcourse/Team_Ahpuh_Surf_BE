package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderBySelectedDateDesc(User user);

    List<Post> findAllByUserAndCategoryOrderBySelectedDateDesc(User user, Category category);

    List<Post> findAllByUserAndSelectedDateBetweenOrderBySelectedDate(User user, LocalDate start, LocalDate end);

    // TODO: Cursor Paging
//    List<Post> findByUserLessThanOrderByIdDesc(LocalDate date, Pageable page);
//
//    List<Post> findByUserAndCategoryLessThanOrderByIdDesc(Long date, Pageable page);
//
//    Boolean existsBySelectedDateLessThan(LocalDate localDate);

}
