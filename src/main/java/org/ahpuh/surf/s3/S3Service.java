package org.ahpuh.surf.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface S3Service {

    Optional<String> uploadUserImage(final MultipartFile profilePhoto) throws IOException;

    Optional<FileStatus> uploadPostFile(final MultipartFile file) throws IOException;

}
