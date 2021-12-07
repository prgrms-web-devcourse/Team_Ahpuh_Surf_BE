package org.ahpuh.surf.post.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String selectedDate;

    @NotBlank
    private String content;

    @Min(value = 0)
    @Max(value = 100)
    private int score;

    private String fileUrl;
}
