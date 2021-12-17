package org.ahpuh.surf.mockaws;

import lombok.extern.slf4j.Slf4j;
import org.ahpuh.surf.common.s3.S3Service;
import org.ahpuh.surf.common.s3.S3ServiceImpl.FileStatus;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@TestConfiguration
@Slf4j
public class MockAwsS3Service {

    @Bean
    public S3Service s3Service() {
        return new S3Service() {

            final String[] PERMISSION_IMG_EXTENSIONS = {"png", "jpg", "jpeg", "gif", "tif", "ico", "svg", "bmp", "webp", "tiff", "jfif"};
            final String[] PERMISSION_FILE_EXTENSIONS = {"doc", "docx", "xls", "xlsx", "hwp", "pdf", "txt", "md", "ppt", "pptx", "key"};

            public String uploadUserImg(final MultipartFile profilePhoto) throws IOException {
                if (exist(profilePhoto)) {
                    return uploadImg(profilePhoto);
                }
                return null;
            }

            public FileStatus uploadPostFile(final MultipartFile file) throws IOException {
                if (exist(file)) {
                    String fileUrl = uploadFile(file);
                    if (fileUrl != null) {
                        return new FileStatus(fileUrl, "file");
                    }

                    fileUrl = uploadImg(file);
                    if (fileUrl != null) {
                        return new FileStatus(fileUrl, "img");
                    }
                }
                return null;
            }

            public String uploadImg(final MultipartFile file) throws IOException {
                final String fileName = file.getOriginalFilename();
                final String extension = Objects.requireNonNull(fileName).split("\\.")[1];

                if (invalidImageExtension(extension)) {
                    log.info("{}은(는) 지원하지 않는 확장자입니다.", extension);
                    return null;
                }
                return "mock";
            }

            public String uploadFile(final MultipartFile file) throws IOException {
                final String fileName = file.getOriginalFilename();
                final String extension = Objects.requireNonNull(fileName).split("\\.")[1];

                if (invalidFileExtension(extension)) {
                    return null;
                }
                return "mock";
            }

            public boolean exist(final MultipartFile file) {
                return !file.isEmpty();
            }

            public boolean invalidImageExtension(final String extension) {
                for (final String permissionExtension : PERMISSION_IMG_EXTENSIONS) {
                    if (extension.equals(permissionExtension)) {
                        return false;
                    }
                }
                return true;
            }

            public boolean invalidFileExtension(final String extension) {
                for (final String permissionExtension : PERMISSION_FILE_EXTENSIONS) {
                    if (extension.equals(permissionExtension)) {
                        return false;
                    }
                }
                return true;
            }

        };
    }

}
