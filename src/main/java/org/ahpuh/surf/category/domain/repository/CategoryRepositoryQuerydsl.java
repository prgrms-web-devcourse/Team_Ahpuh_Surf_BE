package org.ahpuh.surf.category.domain.repository;

import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;

import java.util.List;

public interface CategoryRepositoryQuerydsl {

    List<CategoryDetailResponseDto> getCategoryDashboard(Long userId);

}
