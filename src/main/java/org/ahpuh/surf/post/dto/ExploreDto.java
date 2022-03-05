package org.ahpuh.surf.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExploreDto {

    private Long userId;

    private String userName;

    private String profilePhotoUrl;

    private String categoryName;

    private String colorCode;

    private Long postId;

    private String content;

    private Integer score;

    private String imageUrl;

    private String fileUrl;

    private LocalDate selectedDate;

    private LocalDateTime createdAt;

    @Builder.Default
    private Long likeId = null;

    @Builder.Default
    private Boolean isLiked = false;

    @QueryProjection
    public ExploreDto(final Long userId,
                      final String userName,
                      final String profilePhotoUrl,
                      final String categoryName,
                      final String colorCode,
                      final Long postId,
                      final String content,
                      final Integer score,
                      final String imageUrl,
                      final String fileUrl,
                      final LocalDate selectedDate,
                      final LocalDateTime createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.profilePhotoUrl = profilePhotoUrl;
        this.categoryName = categoryName;
        this.colorCode = colorCode;
        this.postId = postId;
        this.content = content;
        this.score = score;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.selectedDate = selectedDate;
        this.createdAt = createdAt;
        this.likeId = null;
        this.isLiked = false;
    }

    public void setLiked(final Long likeId) {
        this.likeId = likeId;
        this.isLiked = true;
    }
}
