const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { optionalAuth } = require('../middleware/auth');
const { validateRequired, validatePositiveInteger } = require('../utils/validator');

// 获取商品列表
router.get('/', optionalAuth, async (req, res) => {
  try {
    const {
      page = 1,
      limit = 10,
      category,
      sort = 'default',
      minPrice,
      maxPrice,
      keyword,
      isHot,
      isNew,
      isPromotion
    } = req.query;
    
    const userId = req.user ? req.user.userId : 0;
    const offset = (page - 1) * limit;
    
    // 构建查询条件
    let whereConditions = ['p.status = 1'];
    let queryParams = [];
   // 处理分类筛选
    if (category) {
      whereConditions.push('p.category_id = ?');
      queryParams.push(parseInt(category));
    }
    
    if (minPrice) {
      whereConditions.push('p.current_price >= ?');
      queryParams.push(parseFloat(minPrice));
    }
    
    if (maxPrice) {
      whereConditions.push('p.current_price <= ?');
      queryParams.push(parseFloat(maxPrice));
    }
    
    if (keyword) {
      whereConditions.push('(p.product_name LIKE ? OR p.description LIKE ?)');
      const searchTerm = `%${keyword}%`;
      queryParams.push(searchTerm, searchTerm);
    }
    
    if (isHot === 'true') {
      whereConditions.push('p.is_hot = 1');
    }
    
    if (isNew === 'true') {
      whereConditions.push('p.is_new = 1');
    }
    
    if (isPromotion === 'true') {
      whereConditions.push('p.is_promotion = 1');
      whereConditions.push('p.promotion_start_time <= NOW()');
      whereConditions.push('p.promotion_end_time >= NOW()');
    }
    
    // 构建排序条件
    let orderBy = 'p.create_time DESC';
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
      default:
        orderBy = 'p.sales_count DESC, p.rating_average DESC';
    }
    
    const whereClause = whereConditions.join(' AND ');
    
    // 构建主查询参数：userId（用于LEFT JOIN） + WHERE条件参数 + limit + offset
    const parsedOffset = parseInt(offset);
    // 参数顺序必须匹配SQL中占位符的顺序：
    // 1. userId (LEFT JOIN user_favorites)
    // 2. WHERE条件的所有参数（按queryParams顺序）
    // 3. limit (LIMIT ?)
    // 4. offset (OFFSET ?)
    const mainQueryParams = [userId, ...queryParams, parseInt(limit), parsedOffset];
    
    console.log('=== SQL参数调试 ===');
    console.log('userId:', userId, typeof userId);
    console.log('queryParams:', queryParams, queryParams.map(p => typeof p));
    console.log('limit:', parseInt(limit), typeof parseInt(limit));
    console.log('parsedOffset:', parsedOffset, typeof parsedOffset);
    console.log('mainQueryParams:', mainQueryParams);
    console.log('mainQueryParams types:', mainQueryParams.map(p => typeof p));
    console.log('参数总数:', mainQueryParams.length);
    console.log('WHERE子句:', whereClause);
    console.log('==================');
    
    // 构建完整的SQL查询
    const mainSql = `
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
        c.category_name,
        CASE WHEN uf.favorite_id IS NOT NULL THEN 1 ELSE 0 END as isFavorite
      FROM products p
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN categories c ON p.category_id = c.category_id
      LEFT JOIN user_favorites uf ON p.product_id = uf.product_id AND uf.user_id = ?
      WHERE ${whereClause}
      ORDER BY ${orderBy}
      LIMIT ? OFFSET ?
    `;
    
    console.log('完整SQL:', mainSql);
    console.log('SQL参数:', mainQueryParams);
    
    // 获取商品列表 - 使用正确的数值参数
    console.log('最终参数:', mainQueryParams);
    const products = await query(mainSql, mainQueryParams);
    
    // 获取总数 - 只使用WHERE条件的参数，不包含userId、limit和offset
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
      categoryName: product.category_name,
      isFavorite: Boolean(product.isFavorite)
    }));
    
    res.json({
      success: true,
      message: '获取商品列表成功',
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
    console.error('获取商品列表失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取商品详情
router.get('/:id', optionalAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.user ? req.user.userId : 0;
    
    // 参数验证
    const validation = validateRequired(['id'], { id });
    if (!validation.isValid || !validatePositiveInteger(id)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID必须为正整数'
      });
    }
    
    // 获取商品基本信息
    const productResult = await query(`
      SELECT 
        p.*,
        c.category_name,
        c.category_code,
        CASE WHEN uf.favorite_id IS NOT NULL THEN 1 ELSE 0 END as isFavorite
      FROM products p
      LEFT JOIN categories c ON p.category_id = c.category_id
      LEFT JOIN user_favorites uf ON p.product_id = uf.product_id AND uf.user_id = ?
      WHERE p.product_id = ?
    `, [userId, id]);
    
    if (productResult.length === 0) {
      return res.status(404).json({
        success: false,
        message: '商品不存在'
      });
    }
    
    const product = productResult[0];
    
    // 获取商品图片
    const images = await query(`
      SELECT image_url, image_type, sort_order
      FROM product_images
      WHERE product_id = ?
      ORDER BY image_type ASC, sort_order ASC
    `, [id]);
    
    // 获取商品规格
    const specifications = await query(`
      SELECT spec_name, spec_value
      FROM product_specifications
      WHERE product_id = ?
      ORDER BY spec_id ASC
    `, [id]);
    
    // 获取商品评价（最新5条）
    const reviews = await query(`
      SELECT 
        pr.review_id,
        pr.rating,
        pr.content,
        pr.images,
        pr.is_anonymous,
        pr.create_time,
        u.nickname,
        u.avatar_url
      FROM product_reviews pr
      LEFT JOIN users u ON pr.user_id = u.user_id
      WHERE pr.product_id = ? AND pr.status = 1
      ORDER BY pr.create_time DESC
      LIMIT 5
    `, [id]);
    
    // 更新浏览次数
    await query(
      'UPDATE products SET view_count = view_count + 1 WHERE product_id = ?',
      [id]
    );
    
    // 如果用户已登录，记录浏览历史
    if (userId > 0) {
      await query(`
        INSERT INTO user_browse_history (user_id, product_id)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE browse_time = NOW()
      `, [userId, id]);
    }
    
    // 格式化商品详情数据
    const productDetail = {
      id: product.product_id,
      name: product.product_name,
      code: product.product_code,
      description: product.description,
      material: product.material,
      color: product.color,
      size: product.size,
      weight: product.weight,
      currentPrice: parseFloat(product.current_price || 0).toFixed(2),
      originalPrice: parseFloat(product.original_price || 0).toFixed(2),
      promotionPrice: product.promotion_price ? parseFloat(product.promotion_price).toFixed(2) : null,
      stockQuantity: product.stock_quantity,
      salesCount: product.sales_count,
      viewCount: product.view_count + 1, // 包含本次浏览
      rating: {
        average: parseFloat(product.rating_average) || 0,
        count: product.rating_count || 0
      },
      isHot: Boolean(product.is_hot),
      isNew: Boolean(product.is_new),
      isPromotion: Boolean(product.is_promotion),
      promotionStartTime: product.promotion_start_time,
      promotionEndTime: product.promotion_end_time,
      status: product.status,
      category: {
        id: product.category_id,
        name: product.category_name,
        code: product.category_code
      },
      images: images.map(img => ({
        url: img.image_url,
        type: img.image_type, // 1: 主图, 2: 详情图
        sortOrder: img.sort_order
      })),
      specifications: specifications.map(spec => ({
        name: spec.spec_name,
        value: spec.spec_value
      })),
      reviews: reviews.map(review => ({
        id: review.review_id,
        rating: review.rating,
        content: review.content,
        images: review.images ? review.images.split(',') : [],
        isAnonymous: Boolean(review.is_anonymous),
        createTime: review.create_time,
        user: {
          nickname: review.is_anonymous ? '匿名用户' : review.nickname,
          avatar: review.is_anonymous ? '/img/default-avatar.jpg' : review.avatar_url
        }
      })),
      isFavorite: Boolean(product.isFavorite),
      createTime: product.create_time,
      updateTime: product.update_time
    };
    
    res.json({
      success: true,
      message: '获取商品详情成功',
      data: productDetail
    });
    
  } catch (error) {
    console.error('获取商品详情失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 搜索商品
router.get('/search', optionalAuth, async (req, res) => {
  try {
    const {
      q: keyword,
      page = 1,
      limit = 20,
      category,
      sort = 'relevance'
    } = req.query;
    
    const userId = req.user ? req.user.userId : 0;
    
    if (!keyword || keyword.trim() === '') {
      return res.status(400).json({
        success: false,
        message: '搜索关键词不能为空'
      });
    }
    
    const offset = (page - 1) * limit;
    const searchTerm = `%${keyword.trim()}%`;
    
    // 构建查询条件
    let whereConditions = [
      'p.status = 1',
      '(p.product_name LIKE ? OR p.description LIKE ? OR p.material LIKE ? OR p.color LIKE ?)'
    ];
    let queryParams = [searchTerm, searchTerm, searchTerm, searchTerm];
    
    if (category) {
      whereConditions.push('p.category_id = ?');
      queryParams.push(category);
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
      default: // relevance
        orderBy = `
          CASE 
            WHEN p.product_name LIKE ? THEN 1
            WHEN p.description LIKE ? THEN 2
            ELSE 3
          END ASC,
          p.sales_count DESC,
          p.rating_average DESC
        `;
        queryParams.unshift(searchTerm, searchTerm); // 添加到开头用于排序
    }
    
    const whereClause = whereConditions.join(' AND ');
    
    // 获取搜索结果
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
        c.category_name,
        CASE WHEN uf.favorite_id IS NOT NULL THEN 1 ELSE 0 END as isFavorite
      FROM products p
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN categories c ON p.category_id = c.category_id
      LEFT JOIN user_favorites uf ON p.product_id = uf.product_id AND uf.user_id = ?
      WHERE ${whereClause}
      ORDER BY ${orderBy}
      LIMIT ? OFFSET ?
    `, [userId, ...queryParams, parseInt(limit), offset]);
    
    // 获取总数
    const totalResult = await query(`
      SELECT COUNT(*) as total
      FROM products p
      WHERE ${whereClause}
    `, queryParams.slice(sort === 'relevance' ? 2 : 0)); // 排除排序用的参数
    const total = totalResult[0].total;
    
    // 格式化搜索结果
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
      categoryName: product.category_name,
      isFavorite: Boolean(product.isFavorite)
    }));
    
    res.json({
      success: true,
      message: '搜索商品成功',
      data: {
        keyword: keyword,
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
    console.error('搜索商品失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取相关商品推荐
router.get('/:id/related', optionalAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const { limit = 8 } = req.query;
    const userId = req.user ? req.user.userId : 0;
    
    // 参数验证
    const validation = validateRequired(['id'], { id });
    if (!validation.isValid || !validatePositiveInteger(id)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID必须为正整数'
      });
    }
    
    // 获取当前商品的分类
    const currentProduct = await query(
      'SELECT category_id FROM products WHERE product_id = ? AND status = 1',
      [id]
    );
    
    if (currentProduct.length === 0) {
      return res.status(404).json({
        success: false,
        message: '商品不存在'
      });
    }
    
    const categoryId = currentProduct[0].category_id;
    
    // 获取相关商品（同分类，排除当前商品）
    const relatedProducts = await query(`
      SELECT 
        p.product_id,
        p.product_name,
        p.current_price,
        p.original_price,
        p.rating_average,
        p.rating_count,
        p.sales_count,
        pi.image_url,
        CASE WHEN uf.favorite_id IS NOT NULL THEN 1 ELSE 0 END as isFavorite
      FROM products p
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN user_favorites uf ON p.product_id = uf.product_id AND uf.user_id = ?
      WHERE p.category_id = ? AND p.product_id != ? AND p.status = 1
      ORDER BY p.sales_count DESC, p.rating_average DESC
      LIMIT ?
    `, [userId, categoryId, id, parseInt(limit)]);
    
    // 格式化相关商品数据
    const formattedProducts = relatedProducts.map(product => ({
      id: product.product_id,
      title: product.product_name,
      price: parseFloat(product.current_price || 0).toFixed(2),
      originalPrice: parseFloat(product.original_price || 0).toFixed(2),
      rating: parseFloat(product.rating_average) || 0,
      ratingCount: product.rating_count || 0,
      salesCount: product.sales_count || 0,
      image: product.image_url || '/img/default-product.jpg',
      isFavorite: Boolean(product.isFavorite)
    }));
    
    res.json({
      success: true,
      message: '获取相关商品成功',
      data: formattedProducts
    });
    
  } catch (error) {
    console.error('获取相关商品失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

module.exports = router;