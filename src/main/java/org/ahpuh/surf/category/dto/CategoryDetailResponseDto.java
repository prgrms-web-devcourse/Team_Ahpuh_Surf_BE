package org.ahpuh.surf.category.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryDetailResponseDto {

    private Long id;

    private String name;

    private int averageScore;

    private boolean isPublic;

    private String colorCode;

    private int postCount;
}
