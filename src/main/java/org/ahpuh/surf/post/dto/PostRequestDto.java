package org.ahpuh.surf.post.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostRequestDto {

    @NotNull(message = "Invalid category ID.")
    private Long categoryId;

    @NotBlank(message = "Selected Date type must be Date (yyyy-mm-dd).")
    private String selectedDate;

    @NotBlank
    @Size(max = 500, message = "Post contents length must within 500.")
    private String content;

    @Size(max = 100, message = "Score must be 0 ~ 100.")
    private int score;

}
