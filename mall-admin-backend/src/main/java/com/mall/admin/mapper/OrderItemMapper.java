package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 订单商品数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID获取订单商品列表
     *
     * @param orderId 订单ID
     * @return 订单商品列表
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Integer orderId);
    
    /**
     * 根据商品ID获取订单商品列表
     *
     * @param productId 商品ID
     * @return 订单商品列表
     */
    @Select("SELECT * FROM order_items WHERE product_id = #{productId}")
    List<OrderItem> selectByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据用户ID获取购买过的商品
     *
     * @param userId 用户ID
     * @return 订单商品列表
     */
    @Select("SELECT oi.* FROM order_items oi " +
            "INNER JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.user_id = #{userId} AND o.order_status = 4 " +
            "ORDER BY oi.create_time DESC")
    List<OrderItem> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 更新评价状态
     *
     * @param itemId 订单商品ID
     * @param isReviewed 是否已评价
     * @return 更新行数
     */
    @Update("UPDATE order_items SET is_reviewed = #{isReviewed}, update_time = NOW() WHERE item_id = #{itemId}")
    int updateReviewStatus(@Param("itemId") Integer itemId, @Param("isReviewed") Integer isReviewed);
    
    /**
     * 获取商品销售统计
     *
     * @param productId 商品ID
     * @return 销售统计
     */
    @Select("SELECT " +
            "SUM(quantity) as total_sales, " +
            "SUM(subtotal) as total_amount, " +
            "COUNT(DISTINCT order_id) as order_count " +
            "FROM order_items oi " +
            "INNER JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE oi.product_id = #{productId} AND o.order_status = 4")
    Map<String, Object> getProductSalesStatistics(@Param("productId") Integer productId);
    
    /**
     * 获取热销商品排行
     *
     * @param limit 限制数量
     * @return 热销商品列表
     */
    @Select("SELECT " +
            "oi.product_id, " +
            "oi.product_name, " +
            "SUM(oi.quantity) as total_sales, " +
            "SUM(oi.subtotal) as total_amount " +
            "FROM order_items oi " +
            "INNER JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.order_status = 4 " +
            "GROUP BY oi.product_id, oi.product_name " +
            "ORDER BY total_sales DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getHotSellingProducts(@Param("limit") Integer limit);
    
    /**
     * 获取用户购买的商品列表（用于推荐）
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 商品ID列表
     */
    @Select("SELECT DISTINCT oi.product_id " +
            "FROM order_items oi " +
            "INNER JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.user_id = #{userId} AND o.order_status = 4 " +
            "ORDER BY oi.create_time DESC " +
            "LIMIT #{limit}")
    List<Integer> getUserPurchasedProducts(@Param("userId") Integer userId, @Param("limit") Integer limit);
    
    /**
     * 统计订单商品数量
     *
     * @param orderId 订单ID
     * @return 商品数量
     */
    @Select("SELECT COUNT(*) FROM order_items WHERE order_id = #{orderId}")
    Long countByOrderId(@Param("orderId") Integer orderId);
    
    /**
     * 获取待评价的订单商品
     *
     * @param userId 用户ID
     * @return 待评价商品列表
     */
    @Select("SELECT oi.* FROM order_items oi " +
            "INNER JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.user_id = #{userId} AND o.order_status = 4 AND oi.is_reviewed = 0 " +
            "ORDER BY oi.create_time DESC")
    List<OrderItem> getPendingReviewItems(@Param("userId") Integer userId);
}