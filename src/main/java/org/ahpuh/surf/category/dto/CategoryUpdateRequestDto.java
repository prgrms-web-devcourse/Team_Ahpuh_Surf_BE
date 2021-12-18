package org.ahpuh.surf.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryUpdateRequestDto {

    @NotBlank(message = "Category name length must be 1 ~ 30.")
    @Size(min = 1, max = 30)
    private String name;

    @NotNull(message = "No is_public input.")
    private Boolean isPublic;

    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$", message = "Invalid colorCode.")
    private String colorCode;

}
