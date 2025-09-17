package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.ProductRequest;
import com.mall.admin.dto.request.ProductQueryDTO;
import com.mall.admin.dto.response.ProductResponse;
import com.mall.admin.service.IProductService;
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
 * 商品控制器
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品相关的API接口")
public class ProductController {
    
    private final IProductService productService;
    
    /**
     * 获取商品列表
     */
    @Operation(summary = "获取商品列表", description = "分页查询商品列表，支持多条件筛选")
    @GetMapping
    public ApiResponse<PageResult<ProductResponse>> getProductList(ProductQueryDTO queryDTO) {
        try {
            PageResult<ProductResponse> result = productService.getProductList(queryDTO);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return ApiResponse.error("获取商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取商品详情
     */
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ApiResponse.success(product);
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID: {}", id, e);
            return ApiResponse.error("获取商品详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建商品
     */
    @Operation(summary = "创建商品", description = "创建新的商品")
    @PostMapping
    public ApiResponse<Void> createProduct(
            @Parameter(description = "商品创建请求", required = true) @Valid @RequestBody ProductRequest request) {
        try {
            productService.createProduct(request);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("创建商品失败，商品名称: {}", request.getProductName(), e);
            return ApiResponse.error("创建商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品
     */
    @Operation(summary = "更新商品", description = "根据商品ID更新商品信息")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id,
            @Parameter(description = "商品更新请求", required = true) @Valid @RequestBody ProductRequest request) {
        try {
            productService.updateProduct(id, request);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("更新商品失败，商品ID: {}", id, e);
            return ApiResponse.error("更新商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @Operation(summary = "删除商品", description = "根据商品ID删除商品（逻辑删除）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("删除商品失败，商品ID: {}", id, e);
            return ApiResponse.error("删除商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品状态
     */
    @Operation(summary = "更新商品状态", description = "更新指定商品的上下架状态")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateProductStatus(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id,
            @Parameter(description = "新状态：0下架，1上架", required = true) @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            productService.updateProductStatus(id, status);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("更新商品状态失败，商品ID: {}", id, e);
            return ApiResponse.error("更新商品状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新商品状态
     */
    @Operation(summary = "批量更新商品状态", description = "批量更新多个商品的状态")
    @PutMapping("/batch/status")
    public ApiResponse<Void> batchUpdateStatus(
            @Parameter(description = "商品ID列表", required = true) @RequestParam List<Integer> ids,
            @Parameter(description = "新状态：0下架，1上架", required = true) @RequestParam Integer status) {
        try {
            productService.batchUpdateStatus(ids, status);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量更新商品状态失败，商品ID列表: {}, 状态: {}", ids, status, e);
            return ApiResponse.error("批量更新商品状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取热门商品
     */
    @Operation(summary = "获取热门商品", description = "获取热门商品列表")
    @GetMapping("/hot")
    public ApiResponse<List<ProductResponse>> getHotProducts(
            @Parameter(description = "限制数量", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductResponse> products = productService.getHotProducts(limit);
            return ApiResponse.success(products);
        } catch (Exception e) {
            log.error("获取热门商品失败", e);
            return ApiResponse.error("获取热门商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取推荐商品
     */
    @Operation(summary = "获取推荐商品", description = "获取推荐商品列表")
    @GetMapping("/recommended")
    public ApiResponse<List<ProductResponse>> getRecommendedProducts(
            @Parameter(description = "限制数量", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductResponse> products = productService.getRecommendedProducts(limit);
            return ApiResponse.success(products);
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            return ApiResponse.error("获取推荐商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据分类ID获取商品
     */
    @Operation(summary = "根据分类获取商品", description = "根据分类ID获取该分类下的所有商品")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductResponse>> getProductsByCategoryId(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer categoryId) {
        try {
            List<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
            return ApiResponse.success(products);
        } catch (Exception e) {
            log.error("根据分类ID获取商品失败，分类ID: {}", categoryId, e);
            return ApiResponse.error("根据分类ID获取商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索商品
     */
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
        try {
            List<ProductResponse> products = productService.searchProducts(keyword);
            return ApiResponse.success(products);
        } catch (Exception e) {
            log.error("搜索商品失败，关键词: {}", keyword, e);
            return ApiResponse.error("搜索商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品库存
     */
    @Operation(summary = "更新商品库存", description = "更新指定商品的库存数量")
    @PutMapping("/{id}/stock")
    public ApiResponse<Void> updateStock(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id,
            @Parameter(description = "库存数量", required = true) @RequestParam Integer quantity) {
        try {
            boolean success = productService.updateStock(id, quantity);
            if (success) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("库存更新失败");
            }
        } catch (Exception e) {
            log.error("更新商品库存失败，商品ID: {}, 数量: {}", id, quantity, e);
            return ApiResponse.error("更新商品库存失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查商品编码是否存在
     */
    @Operation(summary = "检查商品编码", description = "检查商品编码是否已存在")
    @GetMapping("/check-code")
    public ApiResponse<Boolean> checkProductCode(
            @Parameter(description = "商品编码", required = true) @RequestParam String productCode) {
        try {
            boolean exists = productService.existsByProductCode(productCode);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            log.error("检查商品编码失败，商品编码: {}", productCode, e);
            return ApiResponse.error("检查商品编码失败：" + e.getMessage());
        }
    }
}