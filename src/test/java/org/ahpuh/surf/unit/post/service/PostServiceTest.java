package org.ahpuh.surf.unit.post.service;

import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.post.domain.PostConverter;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.service.PostService;
import org.ahpuh.surf.s3.service.S3Service;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private PostConverter postConverter;

    @InjectMocks
    private PostService postService;
}
