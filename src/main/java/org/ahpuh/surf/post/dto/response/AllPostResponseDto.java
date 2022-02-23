package org.ahpuh.surf.post.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AllPostResponseDto {

    private String categoryName;

    private String colorCode;

    private Long postId;

    private String content;

    private int score;

    private String imageUrl;

    private String fileUrl;

    private String selectedDate;

    @Builder.Default
    private Long likeId = null;

    @Builder.Default
    private Boolean isLiked = false;

    public void setLiked(final Long likeId) {
        this.likeId = likeId;
        this.isLiked = true;
    }

}
