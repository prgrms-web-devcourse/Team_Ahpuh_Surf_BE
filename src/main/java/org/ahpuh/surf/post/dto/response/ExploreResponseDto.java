package org.ahpuh.surf.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExploreResponseDto {

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
    private Long likeId;
    private Boolean isLiked;

    @QueryProjection
    public ExploreResponseDto(final Long userId,
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
                              final LocalDateTime createdAt,
                              final Long likeId) {
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
        this.likeId = likeId;
        this.isLiked = false;
    }

    public void likeCheck() {
        isLiked = likeId != null;
    }
}
