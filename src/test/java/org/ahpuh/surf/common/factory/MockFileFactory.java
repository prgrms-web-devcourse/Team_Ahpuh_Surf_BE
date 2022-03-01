package org.ahpuh.surf.common.factory;

import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class MockFileFactory {

    private static final ClassLoader CLASS_LOADER = MockFileFactory.class.getClassLoader();
    private static final String FILE_KEY = "file";

    public static MockMultipartFile createMultipartFileImage1() {
        final File file = createFile("testImage1.png");
        return fileToMultipart(file);
    }

    public static MockMultipartFile createMultipartFileImage2() {
        final File file = createFile("testImage2.png");
        return fileToMultipart(file);
    }

    public static File createFileImage1() {
        return createFile("testImage1.png");
    }

    public static File createFileImage2() {
        return createFile("testImage2.png");
    }

    public static MockMultipartFile createEmptyImageFile() {
        return new MockMultipartFile(
                FILE_KEY,
                null,
                null,
                new byte[0]
        );
    }

    private static File createFile(final String fileName) {
        final URL resource = CLASS_LOADER.getResource(fileName);
        Objects.requireNonNull(resource);
        return new File(resource.getFile());
    }

    public static MockMultipartFile fileToMultipart(final File file) {
        try {
            return new MockMultipartFile(
                    FILE_KEY,
                    file.getName(),
                    ContentType.IMAGE_JPEG.getMimeType(),
                    Files.readAllBytes(file.toPath())
            );
        } catch (final IOException e) {
            throw new RuntimeException();
        }
    }
}
