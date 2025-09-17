const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { validateRequired, validatePositiveInteger } = require('../utils/validator');

// 获取分类列表
router.get('/', async (req, res) => {
  try {
    const { level, parentId, includeProducts = false } = req.query;
    
    // 构建查询条件
    let whereConditions = ['is_active = 1'];
    let queryParams = [];
    
    if (level) {
      whereConditions.push('category_level = ?');
      queryParams.push(parseInt(level));
    }
    
    if (parentId !== undefined) {
      whereConditions.push('parent_id = ?');
      queryParams.push(parseInt(parentId));
    }
    
    const whereClause = whereConditions.join(' AND ');
    
    // 获取分类列表
    let categoriesQuery = `
      SELECT 
        category_id,
        category_name,
        category_code,
        parent_id,
        category_level,
        sort_order,
        category_image,
        description
      FROM categories
      WHERE ${whereClause}
      ORDER BY sort_order ASC, category_id ASC
    `;
    
    if (includeProducts === 'true') {
      categoriesQuery = `
        SELECT 
          c.category_id,
          c.category_name,
          c.category_code,
          c.parent_id,
          c.category_level,
          c.sort_order,
          c.category_image,
          c.description,
          COUNT(p.product_id) as product_count
        FROM categories c
        LEFT JOIN products p ON c.category_id = p.category_id AND p.status = 1
        WHERE ${whereClause}
        GROUP BY c.category_id
        ORDER BY c.sort_order ASC, c.category_id ASC
      `;
    }
    
    const categories = await query(categoriesQuery, queryParams);
    
    // 格式化分类数据
    const formattedCategories = categories.map(category => ({
      id: category.category_id,
      name: category.category_name,
      code: category.category_code,
      parentId: category.parent_id,
      level: category.category_level,
      sortOrder: category.sort_order,
      image: category.category_image,
      description: category.description,
      productCount: category.product_count || 0
    }));
    
    res.json({
      success: true,
      message: '获取分类列表成功',
      data: formattedCategories
    });
    
  } catch (error) {
    console.error('获取分类列表失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取分类详情
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    
    // 参数验证
    const validation = validateRequired(['id'], { id });
    if (!validation.isValid || !validatePositiveInteger(id)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：分类ID必须为正整数'
      });
    }
    
    // 获取分类详情
    const categoryResult = await query(`
      SELECT 
        c.*,
        COUNT(p.product_id) as product_count,
        parent.category_name as parent_name
      FROM categories c
      LEFT JOIN products p ON c.category_id = p.category_id AND p.status = 1
      LEFT JOIN categories parent ON c.parent_id = parent.category_id
      WHERE c.category_id = ? AND c.is_active = 1
      GROUP BY c.category_id
    `, [id]);
    
    if (categoryResult.length === 0) {
      return res.status(404).json({
        success: false,
        message: '分类不存在'
      });
    }
    
    const category = categoryResult[0];
    
    // 获取子分类
    const subCategories = await query(`
      SELECT 
        c.category_id,
        c.category_name,
        c.category_code,
        c.category_image,
        c.sort_order,
        COUNT(p.product_id) as product_count
      FROM categories c
      LEFT JOIN products p ON c.category_id = p.category_id AND p.status = 1
      WHERE c.parent_id = ? AND c.is_active = 1
      GROUP BY c.category_id
      ORDER BY c.sort_order ASC
    `, [id]);
    
    // 格式化分类详情数据
    const categoryDetail = {
      id: category.category_id,
      name: category.category_name,
      code: category.category_code,
      parentId: category.parent_id,
      parentName: category.parent_name,
      level: category.category_level,
      sortOrder: category.sort_order,
      image: category.category_image,
      description: category.description,
      productCount: category.product_count,
      subCategories: subCategories.map(sub => ({
        id: sub.category_id,
        name: sub.category_name,
        code: sub.category_code,
        image: sub.category_image,
        sortOrder: sub.sort_order,
        productCount: sub.product_count
      })),
      createTime: category.create_time,
      updateTime: category.update_time
    };
    
    res.json({
      success: true,
      message: '获取分类详情成功',
      data: categoryDetail
    });
    
  } catch (error) {
    console.error('获取分类详情失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取分类下的商品
router.get('/:id/products', async (req, res) => {
  try {
    const { id } = req.params;
    const {
      page = 1,
      limit = 20,
      sort = 'default',
      minPrice,
      maxPrice,
      includeSubCategories = true
    } = req.query;
    
    // 参数验证
    const validation = validateRequired(['id'], { id });
    if (!validation.isValid || !validatePositiveInteger(id)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：分类ID必须为正整数'
      });
    }
    
    const offset = (page - 1) * limit;
    
    // 构建分类条件
    let categoryCondition = 'p.category_id = ?';
    let categoryParams = [id];
    
    if (includeSubCategories === 'true') {
      // 获取所有子分类ID
      const subCategories = await query(
        'SELECT category_id FROM categories WHERE parent_id = ? AND is_active = 1',
        [id]
      );
      
      if (subCategories.length > 0) {
        const subCategoryIds = subCategories.map(sub => sub.category_id);
        const placeholders = subCategoryIds.map(() => '?').join(',');
        categoryCondition = `p.category_id IN (?, ${placeholders})`;
        categoryParams = [id, ...subCategoryIds];
      }
    }
    
    // 构建其他查询条件
    let whereConditions = ['p.status = 1', categoryCondition];
    let queryParams = [...categoryParams];
    
    if (minPrice) {
      whereConditions.push('p.current_price >= ?');
      queryParams.push(parseFloat(minPrice));
    }
    
    if (maxPrice) {
      whereConditions.push('p.current_price <= ?');
      queryParams.push(parseFloat(maxPrice));
    }
    
    // 构建排序条件
    let orderBy = 'p.sales_count DESC, p.rating_average DESC';
    switch (sort) {
      case 'price_asc':
        orderBy = 'p.current_price ASC';
        break;
      case 'price_desc':
        orderBy = 'p.current_price DESC';
        break;
      case 'rating':
        orderBy = 'p.rating_average DESC, p.rating_count DESC';
        break;
      case 'sales':
        orderBy = 'p.sales_count DESC';
        break;
      case 'newest':
        orderBy = 'p.create_time DESC';
        break;
    }
    
    const whereClause = whereConditions.join(' AND ');
    
    // 获取商品列表
    const products = await query(`
      SELECT 
        p.product_id,
        p.product_name,
        p.description,
        p.current_price,
        p.original_price,
        p.promotion_price,
        p.rating_average,
        p.rating_count,
        p.sales_count,
        p.stock_quantity,
        p.is_hot,
        p.is_new,
        p.is_promotion,
        pi.image_url,
        c.category_name
      FROM products p
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN categories c ON p.category_id = c.category_id
      WHERE ${whereClause}
      ORDER BY ${orderBy}
      LIMIT ? OFFSET ?
    `, [...queryParams, parseInt(limit), offset]);
    
    // 获取总数
    const totalResult = await query(`
      SELECT COUNT(*) as total
      FROM products p
      WHERE ${whereClause}
    `, queryParams);
    const total = totalResult[0].total;
    
    // 格式化商品数据
    const formattedProducts = products.map(product => ({
      id: product.product_id,
      title: product.product_name,
      description: product.description,
      price: parseFloat(product.current_price || 0).toFixed(2),
      originalPrice: parseFloat(product.original_price || 0).toFixed(2),
      promotionPrice: product.promotion_price ? parseFloat(product.promotion_price).toFixed(2) : null,
      rating: parseFloat(product.rating_average) || 0,
      ratingCount: product.rating_count || 0,
      salesCount: product.sales_count || 0,
      stockQuantity: product.stock_quantity || 0,
      isHot: Boolean(product.is_hot),
      isNew: Boolean(product.is_new),
      isPromotion: Boolean(product.is_promotion),
      image: product.image_url || '/img/default-product.jpg',
      categoryName: product.category_name
    }));
    
    res.json({
      success: true,
      message: '获取分类商品成功',
      data: {
        products: formattedProducts,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total: total,
          totalPages: Math.ceil(total / limit)
        }
      }
    });
    
  } catch (error) {
    console.error('获取分类商品失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取分类树结构
router.get('/tree/all', async (req, res) => {
  try {
    const { includeProducts = false } = req.query;
    
    // 获取所有分类
    let categoriesQuery = `
      SELECT 
        category_id,
        category_name,
        category_code,
        parent_id,
        category_level,
        sort_order,
        category_image,
        description
      FROM categories
      WHERE is_active = 1
      ORDER BY category_level ASC, sort_order ASC, category_id ASC
    `;
    
    if (includeProducts === 'true') {
      categoriesQuery = `
        SELECT 
          c.category_id,
          c.category_name,
          c.category_code,
          c.parent_id,
          c.category_level,
          c.sort_order,
          c.category_image,
          c.description,
          COUNT(p.product_id) as product_count
        FROM categories c
        LEFT JOIN products p ON c.category_id = p.category_id AND p.status = 1
        WHERE c.is_active = 1
        GROUP BY c.category_id
        ORDER BY c.category_level ASC, c.sort_order ASC, c.category_id ASC
      `;
    }
    
    const categories = await query(categoriesQuery);
    
    // 构建分类树
    const categoryMap = new Map();
    const rootCategories = [];
    
    // 初始化所有分类
    categories.forEach(category => {
      const formattedCategory = {
        id: category.category_id,
        name: category.category_name,
        code: category.category_code,
        parentId: category.parent_id,
        level: category.category_level,
        sortOrder: category.sort_order,
        image: category.category_image,
        description: category.description,
        productCount: category.product_count || 0,
        children: []
      };
      
      categoryMap.set(category.category_id, formattedCategory);
      
      if (category.parent_id === 0) {
        rootCategories.push(formattedCategory);
      }
    });
    
    // 构建父子关系
    categories.forEach(category => {
      if (category.parent_id !== 0) {
        const parent = categoryMap.get(category.parent_id);
        const child = categoryMap.get(category.category_id);
        if (parent && child) {
          parent.children.push(child);
        }
      }
    });
    
    res.json({
      success: true,
      message: '获取分类树成功',
      data: rootCategories
    });
    
  } catch (error) {
    console.error('获取分类树失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取热门分类
router.get('/hot/list', async (req, res) => {
  try {
    const { limit = 8 } = req.query;
    const limitNum = parseInt(limit) || 8;
    
    // 获取热门分类（按商品数量排序）
    const hotCategories = await query(`
      SELECT 
        c.category_id,
        c.category_name,
        c.category_code,
        c.category_image,
        c.description,
        COALESCE(COUNT(p.product_id), 0) as product_count
      FROM categories c
      LEFT JOIN products p ON c.category_id = p.category_id AND p.status = 1
      WHERE c.is_active = 1 AND c.category_level = 1
      GROUP BY c.category_id, c.category_name, c.category_code, c.category_image, c.description, c.sort_order
      ORDER BY product_count DESC, c.sort_order ASC
      LIMIT ${limitNum}
    `);
    
    // 格式化热门分类数据
    const formattedCategories = hotCategories.map(category => ({
      id: category.category_id,
      name: category.category_name,
      code: category.category_code,
      image: category.category_image,
      description: category.description,
      productCount: category.product_count
    }));
    
    res.json({
      success: true,
      message: '获取热门分类成功',
      data: formattedCategories
    });
    
  } catch (error) {
    console.error('获取热门分类失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

module.exports = router;