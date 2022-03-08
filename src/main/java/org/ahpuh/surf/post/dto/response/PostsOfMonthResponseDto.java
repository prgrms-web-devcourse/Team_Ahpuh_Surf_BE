package org.ahpuh.surf.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PostsOfMonthResponseDto {

    private String categoryName;
    private String colorCode;
    private Long postId;
    private String content;
    private Integer score;
    private String imageUrl;
    private String fileUrl;
    private LocalDate selectedDate;

    @QueryProjection
    public PostsOfMonthResponseDto(final String categoryName,
                                   final String colorCode,
                                   final Long postId,
                                   final String content,
                                   final Integer score,
                                   final String imageUrl,
                                   final String fileUrl,
                                   final LocalDate selectedDate) {
        this.categoryName = categoryName;
        this.colorCode = colorCode;
        this.postId = postId;
        this.content = content;
        this.score = score;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.selectedDate = selectedDate;
    }
}
