package org.ahpuh.surf.s3;

public class FileStatus {
    public String fileUrl;
    public String fileType;

    public FileStatus(final String fileUrl, final String fileType) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }
}
