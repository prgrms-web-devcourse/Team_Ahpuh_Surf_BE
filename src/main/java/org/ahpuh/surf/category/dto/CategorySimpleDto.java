package org.ahpuh.surf.category.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ahpuh.surf.post.dto.PostScoreDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategorySimpleDto {

    private Long categoryId;
    private String categoryName;
    private String colorCode;
    private List<PostScoreDto> postScores;

    @QueryProjection
    public CategorySimpleDto(final Long categoryId,
                             final String categoryName,
                             final String colorCode,
                             final List<PostScoreDto> postScores) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.colorCode = colorCode;
        this.postScores = postScores;
    }
}
