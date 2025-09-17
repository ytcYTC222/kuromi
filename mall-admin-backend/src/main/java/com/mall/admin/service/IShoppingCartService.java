package com.mall.admin.service;

import com.mall.admin.entity.ShoppingCart;
import com.mall.admin.dto.request.ShoppingCartRequest;
import com.mall.admin.dto.response.ShoppingCartResponse;
import com.mall.admin.common.PageResult;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IShoppingCartService {
    
    /**
     * 获取购物车列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID
     * @return 购物车列表
     */
    PageResult<ShoppingCartResponse> getCartList(Integer page, Integer size, Integer userId);
    
    /**
     * 根据ID获取购物车详情
     *
     * @param id 购物车ID
     * @return 购物车详情
     */
    ShoppingCartResponse getCartById(Integer id);
    
    /**
     * 添加商品到购物车
     *
     * @param request 购物车添加请求
     */
    void addToCart(ShoppingCartRequest request);
    
    /**
     * 更新购物车商品数量
     *
     * @param id 购物车ID
     * @param quantity 商品数量
     */
    void updateCartQuantity(Integer id, Integer quantity);
    
    /**
     * 从购物车删除商品
     *
     * @param id 购物车ID
     */
    void removeFromCart(Integer id);
    
    /**
     * 根据用户ID获取购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<ShoppingCartResponse> getCartByUserId(Integer userId);
    
    /**
     * 根据用户ID和商品ID获取购物车项
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    ShoppingCartResponse getCartByUserIdAndProductId(Integer userId, Integer productId);
    
    /**
     * 检查购物车中是否存在指定商品
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 存在返回true，否则返回false
     */
    boolean existsByUserIdAndProductId(Integer userId, Integer productId);
    
    /**
     * 统计用户购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品数量
     */
    Long countByUserId(Integer userId);
    
    /**
     * 清空用户购物车
     *
     * @param userId 用户ID
     */
    void clearCartByUserId(Integer userId);
    
    /**
     * 批量删除购物车商品
     *
     * @param ids 购物车ID列表
     */
    void batchRemoveFromCart(List<Integer> ids);
    
    /**
     * 批量更新购物车商品数量
     *
     * @param cartItems 购物车商品列表
     */
    void batchUpdateCartQuantity(List<ShoppingCartRequest> cartItems);
    
    /**
     * 获取用户购物车总金额
     *
     * @param userId 用户ID
     * @return 总金额
     */
    Double getTotalAmountByUserId(Integer userId);
    
    /**
     * 获取用户购物车总商品数量
     *
     * @param userId 用户ID
     * @return 总商品数量
     */
    Integer getTotalQuantityByUserId(Integer userId);
    
    /**
     * 合并购物车（用户登录后合并游客购物车）
     *
     * @param guestCartItems 游客购物车商品列表
     * @param userId 用户ID
     */
    void mergeCart(List<ShoppingCartRequest> guestCartItems, Integer userId);
    
    /**
     * 检查购物车商品库存
     *
     * @param userId 用户ID
     * @return 库存不足的商品列表
     */
    List<ShoppingCartResponse> checkCartStock(Integer userId);
    
    /**
     * 更新购物车商品价格（商品价格变动时）
     *
     * @param productId 商品ID
     * @param newPrice 新价格
     */
    void updateCartPrice(Integer productId, Double newPrice);
}