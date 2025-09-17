package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.entity.Category;
import com.mall.admin.mapper.CategoryMapper;
import com.mall.admin.service.ICategoryService;
import com.mall.admin.dto.request.CategoryRequest;
import com.mall.admin.dto.response.CategoryResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    
    private final CategoryMapper categoryMapper;
    
    @Override
    public PageResult<CategoryResponse> getCategoryList(Integer page, Integer size, String keyword, Integer parentId) {
        log.info("获取分类列表，页码: {}, 大小: {}, 关键词: {}, 父分类ID: {}", page, size, keyword, parentId);
        
        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 分类名称模糊查询
        queryWrapper.like(StringUtils.hasText(keyword), Category::getCategoryName, keyword);
        
        // 父分类ID
        queryWrapper.eq(parentId != null, Category::getParentId, parentId);
        
        // 排序
        queryWrapper.orderByAsc(Category::getSortOrder);
        queryWrapper.orderByDesc(Category::getCreateTime);
        
        // 分页查询
        IPage<Category> pageInfo = new Page<>(page != null ? page : 1, size != null ? size : 10);
        IPage<Category> result = categoryMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为响应对象
        List<CategoryResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(responseList, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public CategoryResponse getCategoryById(Integer id) {
        log.info("根据ID获取分类详情，分类ID: {}", id);
        
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        return convertToResponse(category);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(CategoryRequest request) {
        log.info("创建分类，分类名称: {}", request.getCategoryName());
        
        // 检查分类名称是否已存在
        if (existsByCategoryName(request.getCategoryName(), request.getParentId())) {
            throw new BusinessException("分类名称已存在");
        }
        
        // 转换为实体对象
        Category category = convertToEntity(request);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        // 保存到数据库
        int result = categoryMapper.insert(category);
        if (result <= 0) {
            throw new BusinessException("分类创建失败");
        }
        
        log.info("分类创建成功，分类ID: {}", category.getCategoryId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Integer id, CategoryRequest request) {
        log.info("更新分类，分类ID: {}, 分类名称: {}", id, request.getCategoryName());
        
        // 检查分类是否存在
        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查分类名称是否被其他分类使用
        if (!existingCategory.getCategoryName().equals(request.getCategoryName()) && 
            existsByCategoryName(request.getCategoryName(), request.getParentId())) {
            throw new BusinessException("分类名称已存在");
        }
        
        // 更新分类信息
        Category category = convertToEntity(request);
        category.setCategoryId(id);
        category.setUpdateTime(LocalDateTime.now());
        
        int result = categoryMapper.updateById(category);
        if (result <= 0) {
            throw new BusinessException("分类更新失败");
        }
        
        log.info("分类更新成功，分类ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Integer id) {
        log.info("删除分类，分类ID: {}", id);
        
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, id);
        long childCount = categoryMapper.selectCount(queryWrapper);
        if (childCount > 0) {
            throw new BusinessException("该分类下还有子分类，无法删除");
        }
        
        // 逻辑删除
        int result = categoryMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("分类删除失败");
        }
        
        log.info("分类删除成功，分类ID: {}", id);
    }
    
    @Override
    public List<CategoryResponse> getTopCategories() {
        log.info("获取所有顶级分类");
        
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(Category::getParentId)
                   .or()
                   .eq(Category::getParentId, 0);
        queryWrapper.eq(Category::getIsActive, 1);
        queryWrapper.orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CategoryResponse> getCategoriesByParentId(Integer parentId) {
        log.info("根据父分类ID获取子分类，父分类ID: {}", parentId);
        
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, parentId);
        queryWrapper.eq(Category::getIsActive, 1);
        queryWrapper.orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CategoryResponse> getCategoriesByLevel(Integer level) {
        log.info("根据分类层级获取分类，层级: {}", level);
        
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryLevel, level);
        queryWrapper.eq(Category::getIsActive, 1);
        queryWrapper.orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CategoryResponse> getCategoryTree() {
        log.info("获取分类树结构");
        
        // 获取所有启用的分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getIsActive, 1);
        queryWrapper.orderByAsc(Category::getSortOrder);
        
        List<Category> allCategories = categoryMapper.selectList(queryWrapper);
        List<CategoryResponse> categoryResponses = allCategories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // 构建树结构
        return buildCategoryTree(categoryResponses, null);
    }
    
    @Override
    public boolean existsByCategoryName(String categoryName, Integer parentId) {
        if (!StringUtils.hasText(categoryName)) {
            return false;
        }
        
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, categoryName);
        
        // 修复: 明确处理parentId为null和0的情况
        if (parentId != null) {
            queryWrapper.eq(Category::getParentId, parentId);
        } else {
            // 当parentId为null时，只查找顶级分类（parentId为0或null）
            queryWrapper.and(wrapper -> 
                wrapper.isNull(Category::getParentId)
                       .or()
                       .eq(Category::getParentId, 0)
            );
        }
        
        long count = categoryMapper.selectCount(queryWrapper);
        return count > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Integer> ids, Boolean isActive) {
        log.info("批量更新分类状态，分类ID列表: {}, 新状态: {}", ids, isActive);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("分类ID列表不能为空");
        }
        
        // 批量更新状态
        LambdaQueryWrapper<Category> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.in(Category::getCategoryId, ids);
        
        Category updateCategory = new Category();
        updateCategory.setIsActive(isActive ? 1 : 0);
        updateCategory.setUpdateTime(LocalDateTime.now());
        
        int result = categoryMapper.update(updateCategory, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("批量更新分类状态失败");
        }
        
        log.info("批量更新分类状态成功，更新数量: {}", result);
    }
    
    /**
     * 将Category实体转换为CategoryResponse
     */
    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        
        // 手动映射字段名不匹配的属性
        response.setStatus(category.getIsActive());
        response.setLevel(category.getCategoryLevel());
        response.setImage(category.getCategoryImage());
        
        return response;
    }
    
    /**
     * 将CategoryRequest转换为Category实体
     */
    private Category convertToEntity(CategoryRequest request) {
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        
        // 手动映射字段名不匹配的属性
        category.setCategoryLevel(request.getLevel());
        category.setCategoryImage(request.getImage());
        
        // 处理isActive字段映射
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        } else {
            // 默认启用状态
            category.setIsActive(1);
        }
        
        // 生成分类编码（如果没有提供）
        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            category.setCategoryCode(generateCategoryCode(request.getCategoryName()));
        }
        
        return category;
    }
    
    /**
     * 生成分类编码
     */
    private String generateCategoryCode(String categoryName) {
        // 使用时间戳 + 随机数生成分类编码
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return "CAT_" + timestamp + "_" + random;
    }
    
    /**
     * 构建分类树结构
     */
    private List<CategoryResponse> buildCategoryTree(List<CategoryResponse> categories, Integer parentId) {
        return categories.stream()
                .filter(category -> {
                    if (parentId == null) {
                        return category.getParentId() == null || category.getParentId() == 0;
                    } else {
                        return parentId.equals(category.getParentId());
                    }
                })
                .map(category -> {
                    List<CategoryResponse> children = buildCategoryTree(categories, category.getCategoryId());
                    category.setChildren(children);
                    return category;
                })
                .collect(Collectors.toList());
    }
}