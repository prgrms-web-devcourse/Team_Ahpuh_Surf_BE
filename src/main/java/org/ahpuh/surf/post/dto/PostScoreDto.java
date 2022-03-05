package org.ahpuh.surf.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostScoreDto {

    private LocalDate x;    // selectedDate
    private int y;          // score

    @QueryProjection
    public PostScoreDto(final LocalDate selectedDate, final int score) {
        this.x = selectedDate;
        this.y = score;
    }
}
