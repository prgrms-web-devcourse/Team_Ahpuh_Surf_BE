package org.ahpuh.surf.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.ahpuh.surf.like.entity.Like;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FollowingPostDto {

    private Long userId;
    private String categoryName;
    private String colorCode;
    private Long postId;
    private String content;
    private Integer score;
    private String fileUrl;
    private LocalDate selectedDate;
    private LocalDateTime updatedAt;
    private Long likeId;
    private Boolean isLiked;

    @QueryProjection
    public FollowingPostDto(final Long userId,
                            final String categoryName,
                            final String colorCode,
                            final Long postId,
                            final String content,
                            final Integer score,
                            final String fileUrl,
                            final LocalDate selectedDate,
                            final LocalDateTime updatedAt) {
        this.userId = userId;
        this.categoryName = categoryName;
        this.colorCode = colorCode;
        this.postId = postId;
        this.content = content;
        this.score = score;
        this.fileUrl = fileUrl;
        this.selectedDate = selectedDate;
        this.updatedAt = updatedAt;
        this.likeId = null;
        this.isLiked = false;
    }

    public void likedCheck(final Optional<Like> likeId) {
        if (likeId.isPresent()) {
            this.likeId = likeId.get().getLikeId();
            this.isLiked = true;
        } else {
            this.likeId = null;
            this.isLiked = false;
        }
    }

}
