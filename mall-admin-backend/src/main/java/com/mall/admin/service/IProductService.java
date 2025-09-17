package com.mall.admin.service;

import com.mall.admin.entity.Product;
import com.mall.admin.dto.request.ProductRequest;
import com.mall.admin.dto.request.ProductQueryDTO;
import com.mall.admin.dto.response.ProductResponse;
import com.mall.admin.common.PageResult;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IProductService {
    
    /**
     * 获取商品列表
     *
     * @param queryDTO 查询参数
     * @return 商品列表
     */
    PageResult<ProductResponse> getProductList(ProductQueryDTO queryDTO);
    
    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    ProductResponse getProductById(Integer id);
    
    /**
     * 创建商品
     *
     * @param request 商品创建请求
     */
    void createProduct(ProductRequest request);
    
    /**
     * 更新商品
     *
     * @param id 商品ID
     * @param request 商品更新请求
     */
    void updateProduct(Integer id, ProductRequest request);
    
    /**
     * 删除商品
     *
     * @param id 商品ID
     */
    void deleteProduct(Integer id);
    
    /**
     * 更新商品状态
     *
     * @param id 商品ID
     * @param status 新状态：0下架，1上架
     */
    void updateProductStatus(Integer id, Integer status);
    
    /**
     * 批量更新商品状态
     *
     * @param ids 商品ID列表
     * @param status 新状态
     */
    void batchUpdateStatus(List<Integer> ids, Integer status);
    
    /**
     * 获取热门商品
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<ProductResponse> getHotProducts(Integer limit);
    
    /**
     * 获取推荐商品
     *
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<ProductResponse> getRecommendedProducts(Integer limit);
    
    /**
     * 根据分类ID获取商品
     *
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<ProductResponse> getProductsByCategoryId(Integer categoryId);
    
    /**
     * 搜索商品
     *
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    List<ProductResponse> searchProducts(String keyword);
    
    /**
     * 更新商品库存
     *
     * @param productId 商品ID
     * @param quantity 数量
     * @return 更新是否成功
     */
    boolean updateStock(Integer productId, Integer quantity);
    
    /**
     * 检查商品编码是否存在
     *
     * @param productCode 商品编码
     * @return 存在返回true，否则返回false
     */
    boolean existsByProductCode(String productCode);
}