package org.ahpuh.surf.category.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class AllCategoryByUserResponseDto {

    private Long categoryId;

    private String name;

    private Boolean isPublic;

    private String colorCode;

}
