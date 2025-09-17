package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品评价数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {
    
    /**
     * 根据商品ID获取评价列表
     *
     * @param productId 商品ID
     * @return 评价列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} ORDER BY create_time DESC")
    List<ProductReview> selectByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据用户ID获取评价列表
     *
     * @param userId 用户ID
     * @return 评价列表
     */
    @Select("SELECT * FROM product_reviews WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ProductReview> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据评分获取评价列表
     *
     * @param productId 商品ID
     * @param rating 评分
     * @return 评价列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND rating = #{rating} ORDER BY create_time DESC")
    List<ProductReview> selectByProductIdAndRating(@Param("productId") Integer productId, @Param("rating") Integer rating);
    
    /**
     * 获取商品平均评分
     *
     * @param productId 商品ID
     * @return 平均评分
     */
    @Select("SELECT COALESCE(AVG(rating), 0) FROM product_reviews WHERE product_id = #{productId}")
    Double getAverageRating(@Param("productId") Integer productId);
    
    /**
     * 统计商品评价数量
     *
     * @param productId 商品ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM product_reviews WHERE product_id = #{productId}")
    Long countByProductId(@Param("productId") Integer productId);
    
    /**
     * 统计各评分的数量
     *
     * @param productId 商品ID
     * @param rating 评分
     * @return 该评分的数量
     */
    @Select("SELECT COUNT(*) FROM product_reviews WHERE product_id = #{productId} AND rating = #{rating}")
    Long countByProductIdAndRating(@Param("productId") Integer productId, @Param("rating") Integer rating);
    
    /**
     * 检查用户是否已评价该商品
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 已评价返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM product_reviews WHERE user_id = #{userId} AND product_id = #{productId}")
    boolean existsByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    /**
     * 获取有图片的评价
     *
     * @param productId 商品ID
     * @return 有图片的评价列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND images IS NOT NULL AND images != '' ORDER BY create_time DESC")
    List<ProductReview> selectWithImagesByProductId(@Param("productId") Integer productId);
    
    /**
     * 获取好评列表（4-5星）
     *
     * @param productId 商品ID
     * @return 好评列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND rating >= 4 ORDER BY create_time DESC")
    List<ProductReview> selectGoodReviewsByProductId(@Param("productId") Integer productId);
    
    /**
     * 获取中评列表（3星）
     *
     * @param productId 商品ID
     * @return 中评列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND rating = 3 ORDER BY create_time DESC")
    List<ProductReview> selectMediumReviewsByProductId(@Param("productId") Integer productId);
    
    /**
     * 获取差评列表（1-2星）
     *
     * @param productId 商品ID
     * @return 差评列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND rating <= 2 ORDER BY create_time DESC")
    List<ProductReview> selectBadReviewsByProductId(@Param("productId") Integer productId);
    
    /**
     * 更新评价点赞数
     *
     * @param reviewId 评价ID
     * @param likes 点赞数增量
     * @return 更新行数
     */
    @Update("UPDATE product_reviews SET likes = likes + #{likes}, update_time = NOW() WHERE review_id = #{reviewId}")
    int updateLikes(@Param("reviewId") Integer reviewId, @Param("likes") Integer likes);
    
    /**
     * 根据关键词搜索评价内容
     *
     * @param productId 商品ID
     * @param keyword 关键词
     * @return 评价列表
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND content LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<ProductReview> searchByKeyword(@Param("productId") Integer productId, @Param("keyword") String keyword);
}