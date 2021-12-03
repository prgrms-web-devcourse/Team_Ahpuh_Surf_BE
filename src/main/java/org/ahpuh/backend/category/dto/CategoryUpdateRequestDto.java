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
public class CategoryUpdateRequestDto {

    @NotBlank
    private String name;

    private boolean isPublic;

    private String colorCode;

}
