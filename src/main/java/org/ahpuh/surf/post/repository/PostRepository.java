package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderBySelectedDateDesc(Pageable page);

    List<Post> findByUserLessThanOrderByIdDesc(LocalDate date, Pageable page);

    List<Post> findAllByUserAndCategoryOrderBySelectedDateDesc(Pageable page);

    List<Post> findByUserAndCategoryLessThanOrderByIdDesc(Long date, Pageable page);

    List<Post> findAllByUserAndSelectedDateBetweenOrderBySelectedDate(LocalDate start, LocalDate end);

    Boolean existsBySelectedDateLessThan(LocalDate localDate);

}
