package org.ahpuh.surf.common.s3;

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
    public String uploadUserImg(final MultipartFile profilePhoto) {
        if (exist(profilePhoto)) {
            return uploadImg(profilePhoto);
        }
        return null;
    }

    @Transactional
    public S3ServiceImpl.FileStatus uploadPostFile(final MultipartFile file) {
        if (exist(file)) {
            String fileUrl = uploadFile(file);
            if (fileUrl != null) {
                return new S3ServiceImpl.FileStatus(fileUrl, "file");
            }

            fileUrl = uploadImg(file);
            if (fileUrl != null) {
                return new S3ServiceImpl.FileStatus(fileUrl, "img");
            }
        }
        return null;
    }

    public String uploadImg(final MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        final String extension = Objects.requireNonNull(fileName).split("\\.")[1];

        if (!PERMISSION_IMG_EXTENSIONS.contains(extension)) {
            log.info("{}은(는) 지원하지 않는 확장자입니다.", extension);
            return null;
        }
        return "mock upload";
    }

    public String uploadFile(final MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        final String extension = Objects.requireNonNull(fileName).split("\\.")[1];

        if (!PERMISSION_FILE_EXTENSIONS.contains(extension)) {
            return null;
        }
        return "mock upload";
    }

    public boolean exist(final MultipartFile file) {
        return !file.isEmpty();
    }

}
