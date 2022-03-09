package org.ahpuh.surf.post.domain.repository;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {

    List<Post> findAllByUserOrderBySelectedDateDesc(User user, Pageable page);

    List<Post> findAllByCategoryOrderBySelectedDateDesc(Category category, Pageable page);

    Optional<Post> findTop1ByCategoryOrderBySelectedDateDesc(Category category);

}
