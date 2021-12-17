package org.ahpuh.surf.common.s3;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

@Service
@NoArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    final String[] PERMISSION_IMG_EXTENSIONS = {"png", "jpg", "jpeg", "gif", "tif", "ico", "svg", "bmp", "webp", "tiff", "jfif"};
    final String[] PERMISSION_FILE_EXTENSIONS = {"doc", "docx", "xls", "xlsx", "hwp", "pdf", "txt", "md", "ppt", "pptx", "key"};

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

        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucket, fileName).toString();
    }

    public String uploadFile(final MultipartFile file) throws IOException {
        final String fileName = file.getOriginalFilename();
        final String extension = Objects.requireNonNull(fileName).split("\\.")[1];

        if (invalidFileExtension(extension)) {
            return null;
        }

        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucket, fileName).toString();
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

    public static class FileStatus {
        public String fileUrl;
        public String fileType;

        public FileStatus(final String fileUrl, final String fileType) {
            this.fileUrl = fileUrl;
            this.fileType = fileType;
        }
    }

}
