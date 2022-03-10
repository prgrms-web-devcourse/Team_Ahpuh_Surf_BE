package org.ahpuh.surf.post.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostScoreDto {

    private LocalDate selectedDate;

    private int score;

}
