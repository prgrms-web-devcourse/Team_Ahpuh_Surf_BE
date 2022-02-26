package org.ahpuh.surf.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@NoArgsConstructor
@Slf4j
@Profile("!test")
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
    public void setS3Client() {
        final AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    @Transactional
    public String uploadUserImage(final MultipartFile profilePhoto) throws IOException {
        return profilePhoto.isEmpty()
                ? null
                : uploadImg(profilePhoto);
    }

    @Transactional
    public FileStatus uploadPostFile(final MultipartFile file) throws IOException {
        if (file.isEmpty()) {
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
        Objects.requireNonNull(fileName);
        final String extension = getFileExtension(fileName);

        return validateImageExtension(extension)
                ? upload(file, fileName)
                : null;
    }

    public String uploadFile(final MultipartFile file) throws IOException {
        final String fileName = file.getOriginalFilename();
        Objects.requireNonNull(fileName);
        final String extension = getFileExtension(fileName);

        return validateFileExtension(extension)
                ? upload(file, fileName)
                : null;
    }

    private String upload(final MultipartFile file, final String fileName) throws IOException {
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString();
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
