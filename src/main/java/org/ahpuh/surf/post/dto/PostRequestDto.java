package org.ahpuh.surf.post.dto;

import lombok.*;

import javax.validation.constraints.*;

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

    @Min(0)
    @Max(100)
    private Integer score;

}
