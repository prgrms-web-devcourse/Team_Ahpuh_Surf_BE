package org.ahpuh.surf.category.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryDetailResponseDto {

    private Long categoryId;

    private String name;

    private int averageScore;

    private Boolean isPublic;

    private String colorCode;

    private int postCount;
}
