package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.ArticleRequest;
import com.mall.admin.dto.response.ArticleResponse;
import com.mall.admin.entity.Article;
import com.mall.admin.mapper.ArticleMapper;
import com.mall.admin.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    public PageResult<ArticleResponse> getArticleList(Integer page, Integer size, String title, String category, Integer status) {
        log.info("获取文章列表，参数：page={}, size={}, title={}, category={}, status={}", 
                page, size, title, category, status);
        
        // 构建分页对象
        Page<Article> pageObj = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Article::getTitle, title);
        }
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(Article::getCategory, category);
        }
        if (status != null) {
            queryWrapper.eq(Article::getStatus, status);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Article::getCreateTime);
        
        // 执行查询
        IPage<Article> resultPage = articleMapper.selectPage(pageObj, queryWrapper);
        
        // 转换为响应DTO
        List<ArticleResponse> responseList = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(responseList, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public ArticleResponse getArticleById(Integer articleId) {
        log.info("获取文章详情，articleId: {}", articleId);
        
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        return convertToResponse(article);
    }

    @Override
    public Integer createArticle(ArticleRequest request) {
        log.info("创建文章，参数：{}", request);
        
        Article article = new Article();
        BeanUtils.copyProperties(request, article);
        
        // 设置默认值
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        if (article.getLikeCount() == null) {
            article.setLikeCount(0);
        }
        if (article.getStatus() == null) {
            article.setStatus(0); // 默认草稿状态
        }
        
        // 如果状态为发布且没有设置发布时间，则设置为当前时间
        if (article.getStatus() == 1 && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        int result = articleMapper.insert(article);
        if (result > 0) {
            log.info("文章创建成功，articleId: {}", article.getArticleId());
            return article.getArticleId();
        } else {
            throw new RuntimeException("文章创建失败");
        }
    }

    @Override
    public void updateArticle(Integer articleId, ArticleRequest request) {
        log.info("更新文章，articleId: {}, 参数：{}", articleId, request);
        
        Article existingArticle = articleMapper.selectById(articleId);
        if (existingArticle == null) {
            throw new RuntimeException("文章不存在");
        }
        
        Article article = new Article();
        BeanUtils.copyProperties(request, article);
        article.setArticleId(articleId);
        
        // 如果状态变为发布且原来不是发布状态，则设置发布时间
        if (article.getStatus() == 1 && existingArticle.getStatus() != 1 && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        int result = articleMapper.updateById(article);
        if (result > 0) {
            log.info("文章更新成功，articleId: {}", articleId);
        } else {
            throw new RuntimeException("文章更新失败");
        }
    }

    @Override
    public void deleteArticle(Integer articleId) {
        log.info("删除文章，articleId: {}", articleId);
        
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        int result = articleMapper.deleteById(articleId);
        if (result > 0) {
            log.info("文章删除成功，articleId: {}", articleId);
        } else {
            throw new RuntimeException("文章删除失败");
        }
    }

    @Override
    public void updateArticleStatus(Integer articleId, Integer status) {
        log.info("更新文章状态，articleId: {}, status: {}", articleId, status);
        
        // 参数验证
        if (articleId == null || articleId <= 0) {
            throw new IllegalArgumentException("文章ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }
        
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        Article updateArticle = new Article();
        updateArticle.setArticleId(articleId);
        updateArticle.setStatus(status);
        
        // 如果状态变为发布，设置发布时间
        if (status == 1 && article.getStatus() != 1) {
            updateArticle.setPublishTime(LocalDateTime.now());
        }
        
        int result = articleMapper.updateById(updateArticle);
        if (result > 0) {
            log.info("文章状态更新成功，articleId: {}, status: {}", articleId, status);
        } else {
            throw new RuntimeException("文章状态更新失败");
        }
    }

    @Override
    public void incrementViewCount(Integer articleId) {
        log.debug("增加文章浏览量，articleId: {}", articleId);
        
        if (articleId == null || articleId <= 0) {
            throw new IllegalArgumentException("文章ID不能为空");
        }
        
        articleMapper.incrementViewCount(articleId);
    }

    /**
     * 转换为响应DTO
     */
    private ArticleResponse convertToResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(article, response);
        
        // 设置状态名称
        response.setStatusName(getStatusName(article.getStatus()));
        
        return response;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            default:
                return "未知";
        }
    }
}