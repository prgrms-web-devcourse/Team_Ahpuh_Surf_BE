package org.ahpuh.surf.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostReadResponseDto {

    private Long postId;
    private Long userId;
    private Long categoryId;
    private LocalDate selectedDate;
    private String content;
    private Integer score;
    private String imageUrl;
    private String fileUrl;
    private LocalDateTime createdAt;
    private Boolean favorite;
    private Long likeId;
    private Boolean isLiked;

    @QueryProjection
    public PostReadResponseDto(final Long postId,
                               final Long userId,
                               final Long categoryId,
                               final LocalDate selectedDate,
                               final String content,
                               final Integer score,
                               final String imageUrl,
                               final String fileUrl,
                               final LocalDateTime createdAt,
                               final Boolean favorite,
                               final Long likeId) {
        this.postId = postId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
        this.favorite = favorite;
        this.likeId = likeId;
    }

    public PostReadResponseDto likeCheck() {
        isLiked = (likeId != null);
        return this;
    }
}
