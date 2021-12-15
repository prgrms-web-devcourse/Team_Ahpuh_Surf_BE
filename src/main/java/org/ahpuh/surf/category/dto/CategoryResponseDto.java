package org.ahpuh.surf.category.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryResponseDto {

    private Long categoryId;

    private String name;

    private Boolean isPublic;

    private String colorCode;

}
