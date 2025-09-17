package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 购物车数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    
    /**
     * 根据用户ID获取购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ShoppingCart> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和商品ID查询购物车项
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND product_id = #{productId}")
    ShoppingCart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    /**
     * 更新购物车商品数量
     *
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 更新行数
     */
    @Update("UPDATE shopping_cart SET quantity = #{quantity}, update_time = NOW() WHERE cart_id = #{cartId}")
    int updateQuantity(@Param("cartId") Integer cartId, @Param("quantity") Integer quantity);
    
    /**
     * 增加购物车商品数量
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 增加的数量
     * @return 更新行数
     */
    @Update("UPDATE shopping_cart SET quantity = quantity + #{quantity}, update_time = NOW() WHERE user_id = #{userId} AND product_id = #{productId}")
    int increaseQuantity(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("quantity") Integer quantity);
    
    /**
     * 根据用户ID统计购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品总数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM shopping_cart WHERE user_id = #{userId}")
    Integer countItemsByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID统计购物车商品种类数
     *
     * @param userId 用户ID
     * @return 商品种类数
     */
    @Select("SELECT COUNT(*) FROM shopping_cart WHERE user_id = #{userId}")
    Long countProductsByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID删除购物车所有商品
     *
     * @param userId 用户ID
     * @return 删除行数
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和商品ID列表删除购物车商品
     *
     * @param userId 用户ID
     * @param productIds 商品ID列表
     * @return 删除行数
     */
    int deleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIds") List<Integer> productIds);
    
    /**
     * 检查购物车商品是否存在
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 存在返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM shopping_cart WHERE user_id = #{userId} AND product_id = #{productId}")
    boolean existsByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    /**
     * 获取用户购物车中的有效商品（商品状态正常且库存充足）
     *
     * @param userId 用户ID
     * @return 有效购物车列表
     */
    @Select("SELECT sc.* FROM shopping_cart sc INNER JOIN products p ON sc.product_id = p.product_id WHERE sc.user_id = #{userId} AND p.status = 1 AND p.stock_quantity >= sc.quantity ORDER BY sc.create_time DESC")
    List<ShoppingCart> selectValidItemsByUserId(@Param("userId") Integer userId);
    
    /**
     * 清理过期的购物车数据（超过30天未更新）
     *
     * @return 清理行数
     */
    @Delete("DELETE FROM shopping_cart WHERE update_time < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int cleanExpiredItems();
}