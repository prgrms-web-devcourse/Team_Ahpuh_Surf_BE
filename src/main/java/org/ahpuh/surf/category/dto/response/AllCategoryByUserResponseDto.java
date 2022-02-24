package org.ahpuh.surf.category.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AllCategoryByUserResponseDto {

    private Long categoryId;

    private String name;

    private Boolean isPublic;

    private String colorCode;

}
