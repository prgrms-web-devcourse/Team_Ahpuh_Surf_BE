package org.ahpuh.surf.unit.s3;

import org.ahpuh.surf.common.exception.s3.InvalidExtensionException;
import org.ahpuh.surf.common.exception.s3.InvalidFileNameException;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.domain.FileType;
import org.ahpuh.surf.s3.service.MockS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockFileFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MockS3ServiceTest {

    private final MockS3Service s3Service = new MockS3Service();

    @DisplayName("uploadUserImage 메소드는")
    @Nested
    class UploadUserImageMethod {

        @DisplayName("허용된 확장자의 multipart 이미지 파일이 들어오면 이미지를 저장하고 url을 반환한다.")
        @Test
        void uploadImage_Success() {
            // Given
            final MockMultipartFile image = createMultipartFileImage();

            // When
            final Optional<String> response = s3Service.uploadUserImage(image);

            // Then
            assertAll(
                    () -> assertThat(response).isNotEmpty(),
                    () -> assertThat(response.get()).isEqualTo("mock upload")
            );
        }

        @DisplayName("multipart 파일이 empty면 optional.empty() 값을 반환한다.")
        @Test
        void emptyFile_ReturnEmpty() {
            // Given
            final MockMultipartFile emptyFile = createEmptyImageFile();

            // When
            final Optional<String> response = s3Service.uploadUserImage(emptyFile);

            // Then
            assertThat(response).isEmpty();
        }

        @DisplayName("multipart 파일의 fileName이 null이면 예외를 발생시킨다.")
        @Test
        void invalidFileNameException() {
            // Given
            final MockMultipartFile nullNameImage = createNullNameImage();

            // When Then
            assertThatThrownBy(() -> s3Service.uploadUserImage(nullNameImage))
                    .isInstanceOf(InvalidFileNameException.class)
                    .hasMessage("파일 이름이 없습니다.");
        }

        @DisplayName("허용된 이미지 확장자가 아니면 예외를 발생시킨다.")
        @Test
        void invalidExtensionException() {
            // Given
            final MockMultipartFile textFile = createMultipartFileText();

            // When Then
            assertThatThrownBy(() -> s3Service.uploadUserImage(textFile))
                    .isInstanceOf(InvalidExtensionException.class)
                    .hasMessage("파일의 확장자가 잘못되었습니다.");
        }
    }

    @DisplayName("uploadPostFile 메소드는")
    @Nested
    class UploadPostFileMethod {

        @DisplayName("허용된 확장자의 이미지가 들어오면 이미지를 저장하고 파일 타입(IMAGE)과 파일 url을 반환한다.")
        @Test
        void uploadImage_Success() {
            // Given
            final MockMultipartFile image = createMultipartFileImage();

            // When
            final Optional<FileStatus> response = s3Service.uploadPostFile(image);

            // Then
            assertAll(
                    () -> assertThat(response).isNotEmpty(),
                    () -> assertThat(response.get().fileType()).isEqualTo(FileType.IMAGE),
                    () -> assertThat(response.get().fileUrl()).isEqualTo("mock upload")
            );
        }

        @DisplayName("허용된 확장자의 파일이 들어오면 파일을 저장하고 파일 타입(FILE)과 파일 url을 반환한다.")
        @Test
        void uploadFile_Success() {
            // Given
            final MockMultipartFile file = createMultipartFileText();

            // When
            final Optional<FileStatus> response = s3Service.uploadPostFile(file);

            // Then
            assertAll(
                    () -> assertThat(response).isNotEmpty(),
                    () -> assertThat(response.get().fileType()).isEqualTo(FileType.FILE),
                    () -> assertThat(response.get().fileUrl()).isEqualTo("mock upload")
            );
        }

        @DisplayName("multipart 파일이 empty면 optional.empty() 값을 반환한다.")
        @Test
        void emptyFile_ReturnEmpty() {
            // Given
            final MockMultipartFile emptyFile = createEmptyImageFile();

            // When
            final Optional<FileStatus> response = s3Service.uploadPostFile(emptyFile);

            // Then
            assertThat(response).isEmpty();
        }

        @DisplayName("multipart 파일의 fileName이 null이면 예외를 발생시킨다.")
        @Test
        void invalidFileNameException() {
            // Given
            final MockMultipartFile nullNameImage = createNullNameImage();

            // When Then
            assertThatThrownBy(() -> s3Service.uploadPostFile(nullNameImage))
                    .isInstanceOf(InvalidFileNameException.class)
                    .hasMessage("파일 이름이 없습니다.");
        }

        @DisplayName("허용된 이미지 확장자가 아니면 예외를 발생시킨다.")
        @Test
        void invalidExtensionException() throws IOException {
            // Given
            final MockMultipartFile invalidFile = createInvalidFile();

            // When Then
            assertThatThrownBy(() -> s3Service.uploadPostFile(invalidFile))
                    .isInstanceOf(InvalidExtensionException.class)
                    .hasMessage("파일의 확장자가 잘못되었습니다.");
        }
    }
}
