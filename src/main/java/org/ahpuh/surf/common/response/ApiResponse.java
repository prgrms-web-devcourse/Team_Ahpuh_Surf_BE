package org.ahpuh.surf.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private T data;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime serverDatetime;

    public ApiResponse(final int statusCode, final T data) {
        this.statusCode = statusCode;
        this.data = data;
        this.serverDatetime = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ok(final T data) {
        return new ApiResponse<>(200, data);
    }

    public static <T> ApiResponse<T> created(final T data) {
        return new ApiResponse<>(201, data);
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, null);
    }

    public static <T> ApiResponse<T> fail(final int statusCode, final T errData) {
        return new ApiResponse<>(statusCode, errData);
    }

}