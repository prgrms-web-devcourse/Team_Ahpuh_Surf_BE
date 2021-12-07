package org.ahpuh.surf.post.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PostDto {

    private Long postId;
    private Long categoryId;
    private String selectedDate;
    private String content;
    private int score;
    private String fileUrl;

}
