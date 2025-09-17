package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.entity.Product;
import com.mall.admin.mapper.ProductMapper;
import com.mall.admin.mapper.CategoryMapper;
import com.mall.admin.mapper.ProductImageMapper;
import com.mall.admin.entity.Category;
import com.mall.admin.entity.ProductImage;
import com.mall.admin.service.IProductService;
import com.mall.admin.dto.request.ProductRequest;
import com.mall.admin.dto.request.ProductQueryDTO;
import com.mall.admin.dto.response.ProductResponse;
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
 * 商品服务实现类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final ProductImageMapper productImageMapper;
    
    @Override
    public PageResult<ProductResponse> getProductList(ProductQueryDTO queryDTO) {
        log.info("获取商品列表，查询参数: {}", queryDTO);
        
        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 商品名称模糊查询
        queryWrapper.like(StringUtils.hasText(queryDTO.getProductName()), Product::getProductName, queryDTO.getProductName());
        
        // 商品编码精确查询
        queryWrapper.eq(StringUtils.hasText(queryDTO.getProductCode()), Product::getProductCode, queryDTO.getProductCode());
        
        // 关键词搜索（商品名称或编码）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                .like(Product::getProductName, queryDTO.getKeyword())
                .or()
                .like(Product::getProductCode, queryDTO.getKeyword())
                .or()
                .like(Product::getDescription, queryDTO.getKeyword()));
        }
        
        // 分类ID
        queryWrapper.eq(queryDTO.getCategoryId() != null, Product::getCategoryId, queryDTO.getCategoryId());
        
        // 商品状态
        queryWrapper.eq(queryDTO.getStatus() != null, Product::getStatus, queryDTO.getStatus());
        

        
        // 是否热销
        queryWrapper.eq(queryDTO.getIsHot() != null, Product::getIsHot, queryDTO.getIsHot());
        
        // // 价格范围
        // queryWrapper.ge(queryDTO.getMinPrice() != null, Product::getPrice, queryDTO.getMinPrice());
        // queryWrapper.le(queryDTO.getMaxPrice() != null, Product::getPrice, queryDTO.getMaxPrice());
        
        // 品牌
        queryWrapper.eq(StringUtils.hasText(queryDTO.getBrand()), Product::getBrand, queryDTO.getBrand());
        
        // // 库存预警
        // queryWrapper.le(queryDTO.getStockWarning() != null, Product::getStock, queryDTO.getStockWarning());
        
        // 排序
        if (StringUtils.hasText(queryDTO.getOrderBy())) {
            if ("asc".equalsIgnoreCase(queryDTO.getOrderDirection())) {
                queryWrapper.orderByAsc(Product::getProductId);
            } else {
                queryWrapper.orderByDesc(Product::getProductId);
            }
        } else {
            queryWrapper.orderByDesc(Product::getProductId);
        }
        
        // 添加按product_id排序确保结果一致性
        queryWrapper.orderByAsc(Product::getProductId);
        
        // 分页查询
        IPage<Product> pageInfo = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Product> result = productMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为响应对象
        List<ProductResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(responseList, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public ProductResponse getProductById(Integer id) {
        log.info("根据ID获取商品详情，商品ID: {}", id);
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        return convertToResponse(product);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(ProductRequest request) {
        log.info("创建商品，商品名称: {}, 商品编码: {}", request.getProductName(), request.getProductCode());
        
        // 检查商品编码是否已存在
        if (existsByProductCode(request.getProductCode())) {
            throw new BusinessException("商品编码已存在");
        }
        
        // 转换为实体对象
        Product product = convertToEntity(request);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        // 保存到数据库
        int result = productMapper.insert(product);
        if (result <= 0) {
            throw new BusinessException("商品创建失败");
        }
        
        log.info("商品创建成功，商品ID: {}", product.getProductId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Integer id, ProductRequest request) {
        log.info("更新商品，商品ID: {}, 商品名称: {}", id, request.getProductName());
        
        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品编码是否被其他商品使用
        if (!existingProduct.getProductCode().equals(request.getProductCode()) && 
            existsByProductCode(request.getProductCode())) {
            throw new BusinessException("商品编码已存在");
        }
        
        // 更新商品信息
        Product product = convertToEntity(request);
        product.setProductId(id);
        product.setUpdateTime(LocalDateTime.now());
        
        int result = productMapper.updateById(product);
        if (result <= 0) {
            throw new BusinessException("商品更新失败");
        }
        
        log.info("商品更新成功，商品ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Integer id) {
        log.info("删除商品，商品ID: {}", id);
        
        // 检查商品是否存在
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 逻辑删除
        int result = productMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("商品删除失败");
        }
        
        log.info("商品删除成功，商品ID: {}", id);
    }
        
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Integer id, Integer status) {
        log.info("更新商品状态，商品ID: {}, 新状态: {}", id, status);
            
        if (id == null) {
            throw new BusinessException("商品ID不能为空");
        }
            
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("商品状态参数错误，只能为0（下架）或1（上架）");
        }
            
        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new BusinessException("商品不存在");
        }
            
        // 更新状态
        Product updateProduct = new Product();
        updateProduct.setProductId(id);
        updateProduct.setStatus(status);
        updateProduct.setUpdateTime(LocalDateTime.now());
            
        int result = productMapper.updateById(updateProduct);
        if (result <= 0) {
            throw new BusinessException("更新商品状态失败");
        }
            
        log.info("更新商品状态成功，商品ID: {}, 新状态: {}", id, status);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Integer> ids, Integer status) {
        log.info("批量更新商品状态，商品ID列表: {}, 新状态: {}", ids, status);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("商品ID列表不能为空");
        }
        
        // 批量更新状态
        LambdaQueryWrapper<Product> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.in(Product::getProductId, ids);
        
        Product updateProduct = new Product();
        updateProduct.setStatus(status);
        updateProduct.setUpdateTime(LocalDateTime.now());
        
        int result = productMapper.update(updateProduct, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("批量更新商品状态失败");
        }
        
        log.info("批量更新商品状态成功，更新数量: {}", result);
    }
    
    @Override
    public List<ProductResponse> getHotProducts(Integer limit) {
        log.info("获取热门商品，限制数量: {}", limit);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getIsHot, 1)
                   .eq(Product::getStatus, 1)
                   .orderByDesc(Product::getSalesCount)
                   .last("LIMIT " + (limit != null ? limit : 10));
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> getRecommendedProducts(Integer limit) {
        log.info("获取推荐商品，限制数量: {}", limit);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                   .orderByDesc(Product::getRatingAverage)
                   .orderByDesc(Product::getSalesCount)
                   .last("LIMIT " + (limit != null ? limit : 10));
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> getProductsByCategoryId(Integer categoryId) {
        log.info("根据分类ID获取商品，分类ID: {}", categoryId);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCategoryId, categoryId)
                   .eq(Product::getStatus, 1)
                   .orderByDesc(Product::getCreateTime);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        log.info("搜索商品，关键词: {}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Product::getProductName, keyword)
                   .or().like(Product::getProductCode, keyword)
                   .or().like(Product::getDescription, keyword)
                   .eq(Product::getStatus, 1)
                   .orderByDesc(Product::getSalesCount);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStock(Integer productId, Integer quantity) {
        log.info("更新商品库存，商品ID: {}, 数量: {}", productId, quantity);
        
        // 使用Mapper中的updateStock方法
        int result = productMapper.updateStock(productId, quantity);
        return result > 0;
    }
    
    @Override
    public boolean existsByProductCode(String productCode) {
        if (!StringUtils.hasText(productCode)) {
            return false;
        }
        
        // 使用Mapper中的findByProductCode方法
        Product product = productMapper.findByProductCode(productCode);
        return product != null;
    }
    
    /**
     * 将Product实体转换为ProductResponse
     */
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        
        // 手动映射字段名不匹配的属性
        response.setStockQuantity(product.getStockQuantity());
        response.setSalesCount(product.getSalesCount());
        response.setCurrentPrice(product.getCurrentPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setCostPrice(product.getCostPrice());
        response.setMaterial(product.getMaterial());
        response.setColor(product.getColor());
        response.setSize(product.getSize());
        
        // 映射评分相关字段
        if (product.getRatingAverage() != null) {
            response.setRating(product.getRatingAverage().doubleValue());
        }
        response.setReviewCount(product.getRatingCount());
        
        // 映射其他可能缺失的字段
        response.setSpecification(product.getMaterial() + "|" + product.getColor() + "|" + product.getSize());
        if (product.getWeight() != null) {
            response.setWeight(product.getWeight().intValue());
        }
        
        // 设置分类名称
        if (product.getCategoryId() != null) {
            Category category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                response.setCategoryName(category.getCategoryName());
            }
        }
        
        // 设置商品图片信息
        List<ProductImage> productImages = productImageMapper.selectByProductId(product.getProductId());
        if (productImages != null && !productImages.isEmpty()) {
            // 设置图片URL列表
            List<String> imageUrls = productImages.stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            response.setImages(imageUrls);
            
            // 设置主图
            ProductImage mainImage = productImages.stream()
                    .filter(img -> img.getIsMain() != null && img.getIsMain() == 1)
                    .findFirst()
                    .orElse(productImages.get(0)); // 如果没有主图，使用第一张图片
            response.setMainImage(mainImage.getImageUrl());
        }
        
        // // 如果没有图片，但Product实体中有imageUrl字段，使用它作为主图
        // if ((response.getImages() == null || response.getImages().isEmpty()) && 
        //     product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
        //     response.setMainImage(product.getImageUrl());
        //     response.setImages(List.of(product.getImageUrl()));
        // }
        
        return response;
    }
    
    /**
     * 将ProductRequest转换为Product实体
     */
    private Product convertToEntity(ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        return product;
    }
}