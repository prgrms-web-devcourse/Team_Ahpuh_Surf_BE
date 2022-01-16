package org.ahpuh.surf.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ahpuh.surf.category.entity.Category;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostScoreCategoryDto {

    private Category category;
    private LocalDate selectedDate;
    private int score;

    @QueryProjection
    public PostScoreCategoryDto(final Category category, final LocalDate selectedDate, final int score) {
        this.category = category;
        this.selectedDate = selectedDate;
        this.score = score;
    }

}
