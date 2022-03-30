package org.ahpuh.surf.category.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDetailResponseDto {

    private Long categoryId;
    private String name;
    private Boolean isPublic;
    private String colorCode;
    private int postCount;
    private Integer averageScore;

    @Builder
    @QueryProjection
    public CategoryDetailResponseDto(final Long categoryId,
                                     final String name,
                                     final Boolean isPublic,
                                     final String colorCode,
                                     final int postCount,
                                     final Integer averageScore) {
        this.categoryId = categoryId;
        this.name = name;
        this.isPublic = isPublic;
        this.colorCode = colorCode;
        this.postCount = postCount;
        this.averageScore = averageScore;
    }
}
