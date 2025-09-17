package com.mall.admin.service;

import com.mall.admin.entity.Category;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.CategoryRequest;
import com.mall.admin.dto.response.CategoryResponse;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface ICategoryService {
    
    /**
     * 获取分类列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param parentId 父分类ID
     * @return 分类列表
     */
    PageResult<CategoryResponse> getCategoryList(Integer page, Integer size, String keyword, Integer parentId);
    
    /**
     * 根据ID获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    CategoryResponse getCategoryById(Integer id);
    
    /**
     * 创建分类
     *
     * @param request 分类创建请求
     */
    void createCategory(CategoryRequest request);
    
    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param request 分类更新请求
     */
    void updateCategory(Integer id, CategoryRequest request);
    
    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Integer id);
    
    /**
     * 获取所有顶级分类
     *
     * @return 顶级分类列表
     */
    List<CategoryResponse> getTopCategories();
    
    /**
     * 根据父分类ID获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CategoryResponse> getCategoriesByParentId(Integer parentId);
    
    /**
     * 根据分类层级获取分类
     *
     * @param level 分类层级
     * @return 分类列表
     */
    List<CategoryResponse> getCategoriesByLevel(Integer level);
    
    /**
     * 获取分类树结构
     *
     * @return 分类树
     */
    List<CategoryResponse> getCategoryTree();
    
    /**
     * 检查分类名称是否存在
     *
     * @param categoryName 分类名称
     * @param parentId 父分类ID
     * @return 存在返回true，否则返回false
     */
    boolean existsByCategoryName(String categoryName, Integer parentId);
    
    /**
     * 批量更新分类状态
     *
     * @param ids 分类ID列表
     * @param isActive 是否激活
     */
    void batchUpdateStatus(List<Integer> ids, Boolean isActive);
}