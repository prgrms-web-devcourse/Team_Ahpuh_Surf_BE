package org.ahpuh.surf.category.controller;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryCreateResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryUpdateResponseDto;
import org.ahpuh.surf.category.service.CategoryService;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryCreateResponseDto> createCategory(
            @AuthenticationPrincipal final JwtAuthentication authentication,
            @Valid @RequestBody final CategoryCreateRequestDto request
    ) {
        final CategoryCreateResponseDto response = categoryService.createCategory(authentication.userId, request);
        return ResponseEntity.created(URI.create("/api/v1/categories" + response)).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(
            @PathVariable final Long categoryId,
            @Valid @RequestBody final CategoryUpdateRequestDto request
    ) {
        final CategoryUpdateResponseDto response = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok().body(response);
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
