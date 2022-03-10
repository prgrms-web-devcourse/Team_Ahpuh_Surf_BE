package org.ahpuh.surf.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllPostResponseDto {

    private String categoryName;
    private String colorCode;
    private Long postId;
    private String content;
    private Integer score;
    private String imageUrl;
    private String fileUrl;
    private LocalDate selectedDate;
    private Long likeId;
    private Boolean isLiked;

    @Builder
    @QueryProjection
    public AllPostResponseDto(final String categoryName,
                              final String colorCode,
                              final Long postId,
                              final String content,
                              final Integer score,
                              final String imageUrl,
                              final String fileUrl,
                              final LocalDate selectedDate,
                              final Long likeId) {
        this.categoryName = categoryName;
        this.colorCode = colorCode;
        this.postId = postId;
        this.content = content;
        this.score = score;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.selectedDate = selectedDate;
        this.likeId = likeId;
    }

    public void likeCheck() {
        isLiked = !Objects.isNull(likeId);
    }
}
