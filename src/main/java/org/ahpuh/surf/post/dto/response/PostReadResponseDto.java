package org.ahpuh.surf.post.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PostReadResponseDto {

    private Long postId;

    private Long userId;

    private Long categoryId;

    private String selectedDate;

    private String content;

    private int score;

    private String imageUrl;

    private String fileUrl;

    private String createdAt;

    private Boolean favorite;

    private Long likeId;

    private Boolean isLiked;

    public void setLiked(final Long likeId) {
        this.likeId = likeId;
        this.isLiked = true;
    }
}
