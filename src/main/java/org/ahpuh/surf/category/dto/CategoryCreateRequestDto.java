package org.ahpuh.surf.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryCreateRequestDto {

    @NotBlank
    private String name;

    @Builder.Default
    private boolean isPublic = true;

    private String colorCode;

}
