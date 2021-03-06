package org.ahpuh.surf.s3.service;

import org.ahpuh.surf.common.exception.s3.InvalidExtensionException;
import org.ahpuh.surf.common.exception.s3.InvalidFileNameException;
import org.ahpuh.surf.common.exception.s3.UploadFailException;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.domain.FileType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Profile("test")
@Service
public class MockS3Service implements S3Service {

    private static final List<String> PERMISSION_IMG_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif", "tif", "ico", "svg", "bmp", "webp", "tiff", "jfif");
    private static final List<String> PERMISSION_FILE_EXTENSIONS = List.of("doc", "docx", "xls", "xlsx", "hwp", "pdf", "txt", "md", "ppt", "pptx", "key");

    @Transactional
    public Optional<String> uploadUserImage(final MultipartFile profilePhoto) {
        return profilePhoto.isEmpty()
                ? Optional.empty()
                : Optional.of(uploadImg(profilePhoto));
    }

    @Transactional
    public Optional<FileStatus> uploadPostFile(final MultipartFile file) {
        if (file.isEmpty()) {
            return Optional.empty();
        }

        String fileUrl = uploadFile(file);
        if (isNotEmpty(fileUrl)) {
            return Optional.of(new FileStatus(fileUrl, FileType.FILE));
        }
        fileUrl = uploadImg(file);
        if (isNotEmpty(fileUrl)) {
            return Optional.of(new FileStatus(fileUrl, FileType.IMAGE));
        }

        throw new UploadFailException();
    }

    public String uploadImg(final MultipartFile file) {
        final String fileName = getFileName(file);
        final String extension = getFileExtension(fileName);
        validateImageExtension(extension);

        return "mock upload";
    }

    public String uploadFile(final MultipartFile file) {
        final String fileName = getFileName(file);
        final String extension = getFileExtension(fileName);

        if (validateFileExtension(extension)) {
            return "mock upload";
        } else {
            return null;
        }
    }

    public String getFileName(final MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        if (isEmpty(fileName)) {
            throw new InvalidFileNameException();
        }
        return fileName;
    }

    public String getFileExtension(final String fileName) {
        final int index = fileName.lastIndexOf(".");
        if (index > 0 && fileName.length() > index + 1) {
            return fileName.substring(index + 1);
        } else {
            throw new InvalidExtensionException();
        }
    }

    public void validateImageExtension(final String extension) {
        if (!PERMISSION_IMG_EXTENSIONS.contains(extension)) {
            throw new InvalidExtensionException();
        }
    }

    public boolean validateFileExtension(final String extension) {
        return PERMISSION_FILE_EXTENSIONS.contains(extension);
    }
}
