package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品图片数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    
    /**
     * 根据商品ID获取图片列表
     *
     * @param productId 商品ID
     * @return 图片列表
     */
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} ORDER BY sort_order ASC")
    List<ProductImage> selectByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据商品ID获取主图
     *
     * @param productId 商品ID
     * @return 主图信息
     */
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND is_main = 1 LIMIT 1")
    ProductImage selectMainImageByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据商品ID获取图片URL列表
     *
     * @param productId 商品ID
     * @return 图片URL列表
     */
    @Select("SELECT image_url FROM product_images WHERE product_id = #{productId} ORDER BY sort_order ASC")
    List<String> selectImageUrlsByProductId(@Param("productId") Integer productId);
}