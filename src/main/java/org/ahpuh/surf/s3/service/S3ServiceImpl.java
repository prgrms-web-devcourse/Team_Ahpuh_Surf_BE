package org.ahpuh.surf.s3.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.ahpuh.surf.common.exception.s3.InvalidExtensionException;
import org.ahpuh.surf.common.exception.s3.InvalidFileNameException;
import org.ahpuh.surf.common.exception.s3.UploadFailException;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.domain.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Profile("!test")
@Service
public class S3ServiceImpl implements S3Service {

    private static final List<String> PERMISSION_IMG_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif", "tif", "ico", "svg", "bmp", "webp", "tiff", "jfif");
    private static final List<String> PERMISSION_FILE_EXTENSIONS = List.of("doc", "docx", "xls", "xlsx", "hwp", "pdf", "txt", "md", "ppt", "pptx", "key");

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private AmazonS3 s3Client;

    @PostConstruct
    private void setS3Client() {
        final AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    @Transactional
    public Optional<String> uploadUserImage(final MultipartFile profilePhoto) throws IOException {
        return profilePhoto.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(uploadImg(profilePhoto));
    }

    @Transactional
    public Optional<FileStatus> uploadPostFile(final MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Optional.empty();
        }

        String fileUrl = uploadFile(file);
        if (!Objects.isNull(fileUrl)) {
            return Optional.of(new FileStatus(fileUrl, FileType.FILE));
        }
        fileUrl = uploadImg(file);
        if (!Objects.isNull(fileUrl)) {
            return Optional.of(new FileStatus(fileUrl, FileType.IMAGE));
        }

        throw new UploadFailException();
    }

    private String uploadImg(final MultipartFile file) throws IOException {
        final String fileName = getFileName(file);
        final String extension = getFileExtension(fileName);
        validateImageExtension(extension);

        return upload(file, fileName);
    }

    private String uploadFile(final MultipartFile file) throws IOException {
        final String fileName = getFileName(file);
        final String extension = getFileExtension(fileName);

        if (validateFileExtension(extension)) {
            return upload(file, fileName);
        } else {
            return null;
        }
    }

    private String upload(final MultipartFile file, final String fileName) throws IOException {
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return String.valueOf(s3Client.getUrl(bucket, fileName));
    }

    private String getFileName(final MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        if (Objects.isNull(fileName) | fileName.isEmpty()) {
            throw new InvalidFileNameException();
        }
        return fileName;
    }

    private String getFileExtension(final String fileName) {
        final int index = fileName.lastIndexOf(".");
        if (index > 0 && fileName.length() > index + 1) {
            return fileName.substring(index + 1);
        } else {
            throw new InvalidExtensionException();
        }
    }

    private void validateImageExtension(final String extension) {
        if (!PERMISSION_IMG_EXTENSIONS.contains(extension)) {
            throw new InvalidExtensionException();
        }
    }

    private boolean validateFileExtension(final String extension) {
        return PERMISSION_FILE_EXTENSIONS.contains(extension);
    }
}
