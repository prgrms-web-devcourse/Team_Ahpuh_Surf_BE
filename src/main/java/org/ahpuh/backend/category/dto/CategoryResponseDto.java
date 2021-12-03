package org.ahpuh.backend.category.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;

    private String name;

    private boolean isPublic;

    private String colorCode;

}
