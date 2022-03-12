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

    public static MockMultipartFile createMultipartFileImage() {
        final File file = createFile("testImage.png");
        return imageToMultipart(file);
    }

    public static MockMultipartFile createMultipartFileText() {
        final File file = createFile("testText.txt");
        return textToMultipart(file);
    }

    public static MockMultipartFile createEmptyImageFile() {
        return new MockMultipartFile(
                FILE_KEY,
                null,
                null,
                new byte[0]
        );
    }

    public static MockMultipartFile createInvalidFile() throws IOException {
        final File file = createFile("invalidFile.invalid");
        return new MockMultipartFile(
                FILE_KEY,
                file.getName(),
                ContentType.DEFAULT_BINARY.getMimeType(),
                Files.readAllBytes(file.toPath())
        );
    }

    public static MockMultipartFile createNullNameImage() {
        return new MockMultipartFile(
                FILE_KEY,
                null,
                ContentType.IMAGE_JPEG.getMimeType(),
                new byte[2]
        );
    }

    private static File createFile(final String fileName) {
        final URL resource = CLASS_LOADER.getResource(fileName);
        Objects.requireNonNull(resource);
        return new File(resource.getFile());
    }

    public static MockMultipartFile imageToMultipart(final File file) {
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

    public static MockMultipartFile textToMultipart(final File file) {
        try {
            return new MockMultipartFile(
                    FILE_KEY,
                    file.getName(),
                    ContentType.TEXT_PLAIN.getMimeType(),
                    Files.readAllBytes(file.toPath())
            );
        } catch (final IOException e) {
            throw new RuntimeException();
        }
    }
}
