package com.mall.admin.service;

import com.mall.admin.entity.ProductReview;
import com.mall.admin.dto.request.ProductReviewRequest;
import com.mall.admin.dto.response.ProductReviewResponse;
import com.mall.admin.common.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 商品评价服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IProductReviewService {
    
    /**
     * 获取商品评价列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param productId 商品ID
     * @param userId 用户ID
     * @param rating 评分
     * @return 评价列表
     */
    PageResult<ProductReviewResponse> getReviewList(Integer page, Integer size, Integer productId, Integer userId, Integer rating);
    
    /**
     * 根据ID获取评价详情
     *
     * @param id 评价ID
     * @return 评价详情
     */
    ProductReviewResponse getReviewById(Integer id);
    
    /**
     * 创建商品评价
     *
     * @param request 评价创建请求
     */
    void createReview(ProductReviewRequest request);
    
    /**
     * 更新商品评价
     *
     * @param id 评价ID
     * @param request 评价更新请求
     */
    void updateReview(Integer id, ProductReviewRequest request);
    
    /**
     * 删除商品评价
     *
     * @param id 评价ID
     */
    void deleteReview(Integer id);
    
    /**
     * 根据商品ID获取评价列表
     *
     * @param productId 商品ID
     * @return 评价列表
     */
    List<ProductReviewResponse> getReviewsByProductId(Integer productId);
    
    /**
     * 根据用户ID获取评价列表
     *
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ProductReviewResponse> getReviewsByUserId(Integer userId);
    
    /**
     * 根据商品ID和评分获取评价列表
     *
     * @param productId 商品ID
     * @param rating 评分
     * @return 评价列表
     */
    List<ProductReviewResponse> getReviewsByProductIdAndRating(Integer productId, Integer rating);
    
    /**
     * 获取商品平均评分
     *
     * @param productId 商品ID
     * @return 平均评分
     */
    Double getAverageRating(Integer productId);
    
    /**
     * 统计商品评价数量
     *
     * @param productId 商品ID
     * @return 评价数量
     */
    Long countByProductId(Integer productId);
    
    /**
     * 根据商品ID和评分统计评价数量
     *
     * @param productId 商品ID
     * @param rating 评分
     * @return 评价数量
     */
    Long countByProductIdAndRating(Integer productId, Integer rating);
    
    /**
     * 检查用户是否已评价商品
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 已评价返回true，否则返回false
     */
    boolean existsByUserIdAndProductId(Integer userId, Integer productId);
    
    /**
     * 获取带图片的评价列表
     *
     * @param productId 商品ID
     * @return 带图片的评价列表
     */
    List<ProductReviewResponse> getReviewsWithImagesByProductId(Integer productId);
    
    /**
     * 获取好评列表
     *
     * @param productId 商品ID
     * @return 好评列表
     */
    List<ProductReviewResponse> getPositiveReviews(Integer productId);
    
    /**
     * 获取中评列表
     *
     * @param productId 商品ID
     * @return 中评列表
     */
    List<ProductReviewResponse> getNeutralReviews(Integer productId);
    
    /**
     * 获取差评列表
     *
     * @param productId 商品ID
     * @return 差评列表
     */
    List<ProductReviewResponse> getNegativeReviews(Integer productId);
    
    /**
     * 搜索评价
     *
     * @param keyword 搜索关键词
     * @return 评价列表
     */
    List<ProductReviewResponse> searchReviews(String keyword);
    
    /**
     * 获取评价统计信息
     *
     * @param productId 商品ID
     * @return 统计信息
     */
    Map<String, Object> getReviewStatistics(Integer productId);
    
    /**
     * 批量删除评价
     *
     * @param ids 评价ID列表
     */
    void batchDeleteReviews(List<Integer> ids);
}