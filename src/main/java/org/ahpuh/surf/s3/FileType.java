package org.ahpuh.surf.s3;

public enum FileType {
    IMG("img"),
    FILE("file");

    private final String fileType;

    FileType(final String fileType) {
        this.fileType = fileType;
    }
}
