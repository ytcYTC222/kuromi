package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 获取商品总数
     */
    @Select("SELECT COUNT(*) FROM products")
    Long selectTotalProducts();
    
    /**
     * 更新商品库存
     */
    @Update("UPDATE products SET stock_quantity = stock_quantity - #{quantity} WHERE product_id = #{productId} AND stock_quantity >= #{quantity}")
    int updateStock(Integer productId, Integer quantity);
    
    /**
     * 根据商品编码查找商品
     */
    @Select("SELECT * FROM products WHERE product_code = #{productCode}")
    Product findByProductCode(String productCode);
}