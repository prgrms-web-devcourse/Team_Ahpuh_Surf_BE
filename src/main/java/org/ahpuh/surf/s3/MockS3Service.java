package org.ahpuh.surf.s3;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@NoArgsConstructor
@Slf4j
@Profile("test")
public class MockS3Service implements S3Service {

    private static final List<String> PERMISSION_IMG_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif", "tif", "ico", "svg", "bmp", "webp", "tiff", "jfif");
    private static final List<String> PERMISSION_FILE_EXTENSIONS = List.of("doc", "docx", "xls", "xlsx", "hwp", "pdf", "txt", "md", "ppt", "pptx", "key");

    @Transactional
    public String uploadUserImage(final MultipartFile profilePhoto) {
        return profilePhoto.isEmpty()
                ? null
                : uploadImg(profilePhoto);
    }

    @Transactional
    public FileStatus uploadPostFile(final MultipartFile file) {
        if (!file.isEmpty()) {
            String fileUrl = uploadFile(file);
            if (fileUrl != null) {
                return new FileStatus(fileUrl, FileType.FILE);
            }

            fileUrl = uploadImg(file);
            if (fileUrl != null) {
                return new FileStatus(fileUrl, FileType.IMG);
            }
        }
        return null;
    }

    public String uploadImg(final MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        Objects.requireNonNull(fileName);
        final String extension = getFileExtension(fileName);

        return validateImageExtension(extension)
                ? "mock upload"
                : null;
    }

    public String uploadFile(final MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        Objects.requireNonNull(fileName);
        final String extension = getFileExtension(fileName);

        return validateFileExtension(extension)
                ? "mock upload"
                : null;
    }

    private String getFileExtension(final String fileName) {
        final int index = fileName.lastIndexOf(".");
        return (index > 0)
                ? fileName.substring(index + 1)
                : null;
    }

    public boolean validateImageExtension(final String extension) {
        if (!PERMISSION_IMG_EXTENSIONS.contains(extension)) {
            log.info("{}은(는) 지원하지 않는 파일 확장자입니다.", extension);
            return false;
        }
        return true;
    }

    public boolean validateFileExtension(final String extension) {
        return PERMISSION_FILE_EXTENSIONS.contains(extension);
    }
}
