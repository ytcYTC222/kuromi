package com.mall.admin.service;

import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.ArticleRequest;
import com.mall.admin.dto.response.ArticleResponse;

/**
 * 文章服务接口
 * 
 * @author Admin
 * @since 2024-09-11
 */
public interface ArticleService {

    /**
     * 获取文章列表（分页）
     */
    PageResult<ArticleResponse> getArticleList(Integer page, Integer size, String title, String category, Integer status);

    /**
     * 根据ID获取文章详情
     */
    ArticleResponse getArticleById(Integer articleId);

    /**
     * 创建文章
     */
    Integer createArticle(ArticleRequest request);

    /**
     * 更新文章
     */
    void updateArticle(Integer articleId, ArticleRequest request);

    /**
     * 删除文章
     */
    void deleteArticle(Integer articleId);

    /**
     * 更新文章状态
     */
    void updateArticleStatus(Integer articleId, Integer status);

    /**
     * 增加文章浏览量
     */
    void incrementViewCount(Integer articleId);
}