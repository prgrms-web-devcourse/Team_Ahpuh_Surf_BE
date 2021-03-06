package org.ahpuh.surf.category.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.service.CategoryService;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> createCategory(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestBody final CategoryCreateRequestDto request
    ) {
        final Long categoryId = categoryService.createCategory(authentication.userId, request);
        return ResponseEntity.created(URI.create("/api/v1/categories/" + categoryId)).build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable final Long categoryId,
            @Valid @RequestBody final CategoryUpdateRequestDto request
    ) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable final Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AllCategoryByUserResponseDto>> findAllCategoryByUser(
            @AuthenticationPrincipal final JwtAuthentication authentication
    ) {
        final Long userId = authentication.userId;
        return ResponseEntity.ok().body(categoryService.findAllCategoryByUser(userId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<CategoryDetailResponseDto>> getCategoryDashboard(
            @RequestParam final Long userId
    ) {
        return ResponseEntity.ok().body(categoryService.getCategoryDashboard(userId));
    }
}
