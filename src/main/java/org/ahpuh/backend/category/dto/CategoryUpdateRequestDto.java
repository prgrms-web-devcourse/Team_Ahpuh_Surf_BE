package org.ahpuh.backend.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryUpdateRequestDto {

    @NotBlank
    private String name;

    private boolean isPublic;

    private String colorCode;

}
