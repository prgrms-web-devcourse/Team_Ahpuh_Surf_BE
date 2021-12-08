package org.ahpuh.surf.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryCreateRequestDto {

    @NotBlank(message = "Category name is mandatory")
    @Size(min = 1, max = 40)
    private String name;

    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$")
    private String colorCode;

}
