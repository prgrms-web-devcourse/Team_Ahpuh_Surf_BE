package org.ahpuh.surf.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCountDto {

    private String date;
    private Long count;

    @QueryProjection
    public PostCountDto(final LocalDate date, final Long count) {
        this.date = date.toString();
        this.count = count;
    }
}
