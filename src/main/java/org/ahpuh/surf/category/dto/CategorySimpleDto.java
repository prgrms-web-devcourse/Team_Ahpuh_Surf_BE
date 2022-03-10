package org.ahpuh.surf.category.dto;

import lombok.*;
import org.ahpuh.surf.post.dto.PostScoreDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class CategorySimpleDto {

    private Long categoryId;

    private String categoryName;

    private String colorCode;

    private List<PostScoreDto> postScores;

}
