package org.ahpuh.backend.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequestDto {

    @NotBlank
    private String name;

    @Builder.Default
    private boolean isPublic = true;

    private String colorCode;

}
