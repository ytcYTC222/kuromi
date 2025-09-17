package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取已发布的文章列表
     */
    List<Article> findPublishedArticles();

    /**
     * 根据分类获取文章列表
     */
    List<Article> findByCategory(@Param("category") String category);

    /**
     * 获取热门文章（根据浏览量排序）
     */
    List<Article> findPopularArticles(@Param("limit") Integer limit);

    /**
     * 搜索文章（支持标题和内容搜索）
     */
    IPage<Article> searchArticles(Page<Article> page, @Param("keyword") String keyword);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(@Param("articleId") Integer articleId);
}