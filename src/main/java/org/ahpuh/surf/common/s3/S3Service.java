package org.ahpuh.surf.common.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    String uploadUserImg(final MultipartFile profilePhoto) throws IOException;

    S3ServiceImpl.FileStatus uploadPostFile(final MultipartFile file) throws IOException;

    String uploadImg(final MultipartFile file) throws IOException;

    String uploadFile(final MultipartFile file) throws IOException;

    boolean exist(final MultipartFile file);

}
