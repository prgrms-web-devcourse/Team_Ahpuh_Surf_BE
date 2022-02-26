package org.ahpuh.surf.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    String uploadUserImage(final MultipartFile profilePhoto) throws IOException;

    FileStatus uploadPostFile(final MultipartFile file) throws IOException;

}
