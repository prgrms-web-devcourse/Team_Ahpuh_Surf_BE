package org.ahpuh.surf.category.domain.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.response.QCategoryDetailResponseDto;

import java.util.List;

import static org.ahpuh.surf.category.domain.QCategory.category;
import static org.ahpuh.surf.post.domain.QPost.post;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryDetailResponseDto> getCategoryDashboard(final Long userId) {
        return queryFactory
                .select(new QCategoryDetailResponseDto(
                        category.categoryId.as("categoryId"),
                        category.name.as("name"),
                        category.isPublic.as("isPublic"),
                        category.colorCode.as("colorCode"),
                        category.posts.size().as("postCount"),
                        JPAExpressions.select(post.score.avg().round().intValue().as("averageScore"))
                                .from(post)
                                .where(post.category.eq(category))))
                .from(category)
                .where(category.user.userId.eq(userId))
                .groupBy(category.categoryId)
                .fetch();
    }
}
