package org.ahpuh.backend.post.repository;

import org.ahpuh.backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
