package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.ArticleRequest;
import com.mall.admin.dto.response.ArticleResponse;
import com.mall.admin.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 文章管理控制器
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 获取文章列表
     */
    @GetMapping
    public ApiResponse<PageResult<ArticleResponse>> getArticleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        
        log.info("获取文章列表接口调用，参数：page={}, size={}, title={}, category={}, status={}", 
                page, size, title, category, status);
        
        PageResult<ArticleResponse> result = articleService.getArticleList(page, size, title, category, status);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ArticleResponse> getArticleById(@PathVariable Integer id) {
        log.info("获取文章详情接口调用，articleId: {}", id);
        
        ArticleResponse result = articleService.getArticleById(id);
        return ApiResponse.success(result);
    }

    /**
     * 创建文章
     */
    @PostMapping
    public ApiResponse<Integer> createArticle(@Validated @RequestBody ArticleRequest request) {
        log.info("创建文章接口调用，参数：{}", request);
        
        Integer articleId = articleService.createArticle(request);
        return ApiResponse.success(articleId);
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> updateArticle(@PathVariable Integer id, 
                                          @Validated @RequestBody ArticleRequest request) {
        log.info("更新文章接口调用，articleId: {}, 参数：{}", id, request);
        
        articleService.updateArticle(id, request);
        return ApiResponse.success();
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable Integer id) {
        log.info("删除文章接口调用，articleId: {}", id);
        
        articleService.deleteArticle(id);
        return ApiResponse.success();
    }

    /**
     * 更新文章状态
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateArticleStatus(@PathVariable Integer id,
                                                @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        log.info("更新文章状态接口调用，articleId: {}, status: {}", id, status);
        
        articleService.updateArticleStatus(id, status);
        return ApiResponse.success();
    }

    /**
     * 增加文章浏览量
     */
    @PostMapping("/{id}/view")
    public ApiResponse<Void> incrementViewCount(@PathVariable Integer id) {
        log.debug("增加文章浏览量接口调用，articleId: {}", id);
        
        articleService.incrementViewCount(id);
        return ApiResponse.success();
    }
}