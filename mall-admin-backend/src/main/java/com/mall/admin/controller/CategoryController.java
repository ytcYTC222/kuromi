package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.CategoryRequest;
import com.mall.admin.dto.response.CategoryResponse;
import com.mall.admin.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "分类相关的API接口")
public class CategoryController {
    
    private final ICategoryService categoryService;
    
    /**
     * 获取分类列表
     */
    @Operation(summary = "获取分类列表", description = "分页查询分类列表，支持多条件筛选")
    @GetMapping
    public ApiResponse<PageResult<CategoryResponse>> getCategoryList(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "父分类ID") @RequestParam(required = false) Integer parentId) {
        try {
            PageResult<CategoryResponse> result = categoryService.getCategoryList(page, size, keyword, parentId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ApiResponse.error("获取分类列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取分类详情
     */
    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详细信息")
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id) {
        try {
            CategoryResponse category = categoryService.getCategoryById(id);
            return ApiResponse.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败，分类ID: {}", id, e);
            return ApiResponse.error("获取分类详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建分类
     */
    @Operation(summary = "创建分类", description = "创建新的分类")
    @PostMapping
    public ApiResponse<Void> createCategory(
            @Parameter(description = "分类创建请求", required = true) @Valid @RequestBody CategoryRequest request) {
        try {
            categoryService.createCategory(request);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("创建分类失败，分类名称: {}", request.getCategoryName(), e);
            return ApiResponse.error("创建分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新分类
     */
    @Operation(summary = "更新分类", description = "根据分类ID更新分类信息")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id,
            @Parameter(description = "分类更新请求", required = true) @Valid @RequestBody CategoryRequest request) {
        try {
            categoryService.updateCategory(id, request);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("更新分类失败，分类ID: {}", id, e);
            return ApiResponse.error("更新分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除分类
     */
    @Operation(summary = "删除分类", description = "根据分类ID删除分类")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("删除分类失败，分类ID: {}", id, e);
            return ApiResponse.error("删除分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取顶级分类
     */
    @Operation(summary = "获取顶级分类", description = "获取所有顶级分类列表")
    @GetMapping("/top")
    public ApiResponse<List<CategoryResponse>> getTopCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getTopCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("获取顶级分类失败", e);
            return ApiResponse.error("获取顶级分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据父分类ID获取子分类
     */
    @Operation(summary = "获取子分类", description = "根据父分类ID获取子分类列表")
    @GetMapping("/parent/{parentId}")
    public ApiResponse<List<CategoryResponse>> getCategoriesByParentId(
            @Parameter(description = "父分类ID", required = true) @PathVariable Integer parentId) {
        try {
            List<CategoryResponse> categories = categoryService.getCategoriesByParentId(parentId);
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("根据父分类ID获取子分类失败，父分类ID: {}", parentId, e);
            return ApiResponse.error("获取子分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取分类树结构
     */
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    @GetMapping("/tree")
    public ApiResponse<List<CategoryResponse>> getCategoryTree() {
        try {
            List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
            return ApiResponse.success(categoryTree);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return ApiResponse.error("获取分类树失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新分类状态
     */
    @Operation(summary = "批量更新分类状态", description = "批量更新多个分类的启用状态")
    @PatchMapping("/batch/status")
    public ApiResponse<Void> batchUpdateStatus(
            @Parameter(description = "请求体", required = true) @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            Boolean isActive = (Boolean) request.get("isActive");
            
            categoryService.batchUpdateStatus(ids, isActive);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量更新分类状态失败", e);
            return ApiResponse.error("批量更新分类状态失败：" + e.getMessage());
        }
    }
}