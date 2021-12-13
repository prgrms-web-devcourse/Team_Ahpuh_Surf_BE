package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {
}
