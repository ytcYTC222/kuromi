const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { validateRequired } = require('../utils/validator');

// 商品搜索
router.get('/products', async (req, res) => {
  try {
    const {
      keyword = '',
      page = 1,
      limit = 20,
      sort = 'relevance',
      categoryId,
      minPrice,
      maxPrice,
      userId
    } = req.query;
    
    const validation = validateRequired(['keyword'], { keyword });
    if (!validation.isValid) {
      return res.status(400).json({
        success: false,
        message: '搜索关键词不能为空'
      });
    }
    
    const offset = (page - 1) * limit;
    
    // 构建搜索条件
    let whereConditions = ['p.status = 1'];
    let queryParams = [];
    
    // 关键词搜索
    if (keyword.trim()) {
      whereConditions.push('(p.product_name LIKE ? OR p.description LIKE ? OR p.brand LIKE ?)');
      const searchTerm = `%${keyword.trim()}%`;
      queryParams.push(searchTerm, searchTerm, searchTerm);
    }
    
    // 分类筛选
    if (categoryId) {
      whereConditions.push('p.category_id = ?');
      queryParams.push(parseInt(categoryId));
    }
    
    // 价格筛选
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
      case 'relevance':
      default:
        // 相关性排序：优先匹配商品名称，然后是销量和评分
        orderBy = `
          CASE 
            WHEN p.product_name LIKE ? THEN 1
            WHEN p.description LIKE ? THEN 2
            WHEN p.brand LIKE ? THEN 3
            ELSE 4
          END,
          p.sales_count DESC,
          p.rating_average DESC
        `;
        const searchTerm = `%${keyword.trim()}%`;
        queryParams.unshift(searchTerm, searchTerm, searchTerm);
        break;
    }
    
    const whereClause = whereConditions.join(' AND ');
    
    // 获取搜索结果
    const products = await query(`
      SELECT 
        p.product_id,
        p.product_name,
        p.description,
        p.brand,
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
        ${userId ? 'CASE WHEN uf.favorite_id IS NOT NULL THEN 1 ELSE 0 END as is_favorite' : '0 as is_favorite'}
      FROM products p
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN categories c ON p.category_id = c.category_id
      ${userId ? 'LEFT JOIN user_favorites uf ON p.product_id = uf.product_id AND uf.user_id = ?' : ''}
      WHERE ${whereClause}
      ORDER BY ${orderBy}
      LIMIT ? OFFSET ?
    `, userId ? [userId, ...queryParams, parseInt(limit), offset] : [...queryParams, parseInt(limit), offset]);
    
    // 获取搜索结果总数
    const totalResult = await query(`
      SELECT COUNT(*) as total
      FROM products p
      WHERE ${whereClause}
    `, sort === 'relevance' ? queryParams.slice(3) : queryParams.slice(0, -2));
    const total = totalResult[0].total;
    
    // 格式化搜索结果
    const formattedProducts = products.map(product => ({
      id: product.product_id,
      title: product.product_name,
      description: product.description,
      brand: product.brand,
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
      isFavorite: Boolean(product.is_favorite),
      image: product.image_url || '/img/default-product.jpg',
      categoryName: product.category_name
    }));
    
    // 记录搜索历史
    if (keyword.trim()) {
      try {
        await query(
          'INSERT INTO search_records (user_id, search_keyword, search_type, result_count) VALUES (?, ?, 1, ?)',
          [userId || null, keyword.trim(), total]
        );
        
        // 更新热门搜索
        await query(`
          INSERT INTO hot_searches (keyword, search_count) 
          VALUES (?, 1) 
          ON DUPLICATE KEY UPDATE 
          search_count = search_count + 1,
          update_time = CURRENT_TIMESTAMP
        `, [keyword.trim()]);
      } catch (error) {
        console.error('记录搜索历史失败:', error);
      }
    }
    
    res.json({
      success: true,
      message: '搜索成功',
      data: {
        keyword: keyword,
        products: formattedProducts,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total: total,
          totalPages: Math.ceil(total / limit)
        },
        filters: {
          categoryId: categoryId ? parseInt(categoryId) : null,
          minPrice: minPrice ? parseFloat(minPrice) : null,
          maxPrice: maxPrice ? parseFloat(maxPrice) : null,
          sort: sort
        }
      }
    });
    
  } catch (error) {
    console.error('商品搜索失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取热门搜索
router.get('/hot', async (req, res) => {
  try {
    const { limit = 10 } = req.query;
    
    const hotSearches = await query(`
      SELECT keyword, search_count
      FROM hot_searches
      WHERE is_active = 1
      ORDER BY sort_order ASC, search_count DESC
      LIMIT ?
    `, [parseInt(limit)]);
    
    res.json({
      success: true,
      message: '获取热门搜索成功',
      data: hotSearches.map(item => ({
        keyword: item.keyword,
        searchCount: item.search_count
      }))
    });
    
  } catch (error) {
    console.error('获取热门搜索失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取搜索建议
router.get('/suggestions', async (req, res) => {
  try {
    const { keyword = '', limit = 10 } = req.query;
    
    if (!keyword.trim()) {
      return res.json({
        success: true,
        message: '获取搜索建议成功',
        data: []
      });
    }
    
    const searchTerm = `%${keyword.trim()}%`;
    
    // 获取商品名称建议
    const productSuggestions = await query(`
      SELECT DISTINCT product_name as suggestion, 'product' as type
      FROM products
      WHERE product_name LIKE ? AND status = 1
      ORDER BY sales_count DESC
      LIMIT ?
    `, [searchTerm, Math.ceil(parseInt(limit) * 0.6)]);
    
    // 获取品牌建议
    const brandSuggestions = await query(`
      SELECT DISTINCT brand as suggestion, 'brand' as type
      FROM products
      WHERE brand LIKE ? AND brand IS NOT NULL AND status = 1
      ORDER BY COUNT(*) DESC
      LIMIT ?
    `, [searchTerm, Math.ceil(parseInt(limit) * 0.2)]);
    
    // 获取分类建议
    const categorySuggestions = await query(`
      SELECT DISTINCT category_name as suggestion, 'category' as type
      FROM categories
      WHERE category_name LIKE ? AND is_active = 1
      ORDER BY sort_order ASC
      LIMIT ?
    `, [searchTerm, Math.ceil(parseInt(limit) * 0.2)]);
    
    // 合并建议结果
    const allSuggestions = [
      ...productSuggestions,
      ...brandSuggestions,
      ...categorySuggestions
    ].slice(0, parseInt(limit));
    
    res.json({
      success: true,
      message: '获取搜索建议成功',
      data: allSuggestions.map(item => ({
        suggestion: item.suggestion,
        type: item.type
      }))
    });
    
  } catch (error) {
    console.error('获取搜索建议失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取用户搜索历史
router.get('/history/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    const { limit = 10 } = req.query;
    
    const validation = validateRequired(['userId'], { userId });
    if (!validation.isValid) {
      return res.status(400).json({
        success: false,
        message: '用户ID不能为空'
      });
    }
    
    const searchHistory = await query(`
      SELECT 
        search_keyword,
        search_type,
        result_count,
        create_time
      FROM search_records
      WHERE user_id = ?
      ORDER BY create_time DESC
      LIMIT ?
    `, [userId, parseInt(limit)]);
    
    res.json({
      success: true,
      message: '获取搜索历史成功',
      data: searchHistory.map(item => ({
        keyword: item.search_keyword,
        type: item.search_type,
        resultCount: item.result_count,
        searchTime: item.create_time
      }))
    });
    
  } catch (error) {
    console.error('获取搜索历史失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 清除用户搜索历史
router.delete('/history/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    
    const validation = validateRequired(['userId'], { userId });
    if (!validation.isValid) {
      return res.status(400).json({
        success: false,
        message: '用户ID不能为空'
      });
    }
    
    await query('DELETE FROM search_records WHERE user_id = ?', [userId]);
    
    res.json({
      success: true,
      message: '清除搜索历史成功'
    });
    
  } catch (error) {
    console.error('清除搜索历史失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 搜索统计
router.get('/stats', async (req, res) => {
  try {
    const { startDate, endDate } = req.query;
    
    let dateCondition = '';
    let queryParams = [];
    
    if (startDate && endDate) {
      dateCondition = 'WHERE create_time BETWEEN ? AND ?';
      queryParams = [startDate, endDate];
    } else if (startDate) {
      dateCondition = 'WHERE create_time >= ?';
      queryParams = [startDate];
    } else if (endDate) {
      dateCondition = 'WHERE create_time <= ?';
      queryParams = [endDate];
    }
    
    // 获取搜索统计数据
    const [totalSearches, topKeywords, searchTrends] = await Promise.all([
      // 总搜索次数
      query(`
        SELECT COUNT(*) as total
        FROM search_records
        ${dateCondition}
      `, queryParams),
      
      // 热门搜索关键词
      query(`
        SELECT 
          search_keyword,
          COUNT(*) as search_count,
          AVG(result_count) as avg_results
        FROM search_records
        ${dateCondition}
        GROUP BY search_keyword
        ORDER BY search_count DESC
        LIMIT 20
      `, queryParams),
      
      // 搜索趋势（按日期）
      query(`
        SELECT 
          DATE(create_time) as search_date,
          COUNT(*) as search_count
        FROM search_records
        ${dateCondition}
        GROUP BY DATE(create_time)
        ORDER BY search_date DESC
        LIMIT 30
      `, queryParams)
    ]);
    
    res.json({
      success: true,
      message: '获取搜索统计成功',
      data: {
        totalSearches: totalSearches[0].total,
        topKeywords: topKeywords.map(item => ({
          keyword: item.search_keyword,
          searchCount: item.search_count,
          avgResults: Math.round(item.avg_results)
        })),
        searchTrends: searchTrends.map(item => ({
          date: item.search_date,
          searchCount: item.search_count
        }))
      }
    });
    
  } catch (error) {
    console.error('获取搜索统计失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

module.exports = router;