package org.ahpuh.surf.post.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostRequestDto {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String selectedDate;

    @NotBlank
    @Size(max = 500)
    private String content;

    @Min(value = 0)
    @Max(value = 100)
    private int score;

}
