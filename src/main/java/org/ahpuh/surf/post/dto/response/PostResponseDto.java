package org.ahpuh.surf.post.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PostResponseDto {

    private String categoryName;

    private String colorCode;

    private Long postId;

    private String content;

    private int score;

    private String imageUrl;

    private String fileUrl;

    private String selectedDate;

}
