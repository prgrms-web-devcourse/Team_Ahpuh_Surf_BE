package org.ahpuh.surf.common.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Test
    @Transactional
    void testUpload() throws IOException {
        final MockMultipartFile image = new MockMultipartFile("file-data", "filename-1.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());
        s3Service.upload(image);
    }

}