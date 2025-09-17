package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 根据父分类ID获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND is_active = 1 ORDER BY sort_order ASC")
    List<Category> selectByParentId(@Param("parentId") Integer parentId);
    
    /**
     * 获取所有顶级分类
     *
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_id = 0 AND is_active = 1 ORDER BY sort_order ASC")
    List<Category> selectTopCategories();
    
    /**
     * 根据分类层级获取分类列表
     *
     * @param level 分类层级
     * @return 分类列表
     */
    @Select("SELECT * FROM categories WHERE category_level = #{level} AND is_active = 1 ORDER BY sort_order ASC")
    List<Category> selectByLevel(@Param("level") Integer level);
    
    /**
     * 检查分类名称是否存在
     *
     * @param categoryName 分类名称
     * @param parentId 父分类ID
     * @return 存在返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM categories WHERE category_name = #{categoryName} AND parent_id = #{parentId}")
    boolean existsByCategoryName(@Param("categoryName") String categoryName, @Param("parentId") Integer parentId);
    
    /**
     * 统计分类下的商品数量
     *
     * @param categoryId 分类ID
     * @return 商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE category_id = #{categoryId} AND is_active = 1")
    Long countProductsByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    @Select("SELECT * FROM categories WHERE is_active = 1 ORDER BY category_level ASC, sort_order ASC")
    List<Category> selectCategoryTree();
    
    /**
     * 根据分类名称模糊查询
     *
     * @param categoryName 分类名称
     * @return 分类列表
     */
    @Select("SELECT * FROM categories WHERE category_name LIKE CONCAT('%', #{categoryName}, '%') AND is_active = 1 ORDER BY sort_order ASC")
    List<Category> searchByCategoryName(@Param("categoryName") String categoryName);
}