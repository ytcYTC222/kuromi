const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { optionalAuth } = require('../middleware/auth');

// 获取购物车推荐商品
router.get('/cart', optionalAuth, async (req, res) => {
  try {
    const { limit = 8 } = req.query;
    const userId = req.user ? req.user.userId : 0;
    
    let recommendedProducts = [];
    
    // 简化逻辑：直接推荐热门商品
    const limitNum = parseInt(limit) || 8;
    recommendedProducts = await query(`
      SELECT 
        p.product_id,
        p.product_name,
        p.current_price,
        p.original_price,
        p.rating_average,
        p.rating_count,
        p.sales_count,
        pi.image_url,
        c.category_name
      FROM products p
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN categories c ON p.category_id = c.category_id
      WHERE p.status = 1
      ORDER BY p.sales_count DESC, p.rating_average DESC
      LIMIT ${limitNum}
    `);
    
    // 格式化推荐商品数据
    const formattedProducts = recommendedProducts.map(product => ({
      id: product.product_id,
      title: product.product_name,
      price: parseFloat(product.current_price || 0).toFixed(2),
      originalPrice: parseFloat(product.original_price || 0).toFixed(2),
      rating: parseFloat(product.rating_average) || 0,
      ratingCount: product.rating_count || 0,
      salesCount: product.sales_count || 0,
      image: product.image_url || '/img/default-product.jpg',
      categoryName: product.category_name || '推荐商品'
    }));
    
    res.json({
      success: true,
      message: '获取推荐商品成功',
      data: formattedProducts
    });
    
  } catch (error) {
    console.error('获取推荐商品失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取基于用户行为的个性化推荐
router.get('/personalized', optionalAuth, async (req, res) => {
  try {
    const { limit = 10 } = req.query;
    const userId = req.user ? req.user.userId : 0;
    
    if (userId === 0) {
      return res.status(401).json({
        success: false,
        message: '需要登录才能获取个性化推荐'
      });
    }
    
    // 获取用户收藏的商品类别
    const favoriteCategories = await query(`
      SELECT DISTINCT p.category_id, COUNT(*) as count
      FROM user_favorites uf
      JOIN products p ON uf.product_id = p.product_id
      WHERE uf.user_id = ?
      GROUP BY p.category_id
      ORDER BY count DESC
      LIMIT 3
    `, [userId]);
    
    let recommendedProducts = [];
    
    if (favoriteCategories.length > 0) {
      const categoryIds = favoriteCategories.map(cat => cat.category_id);
      const placeholders = categoryIds.map(() => '?').join(',');
      
      // 基于收藏类别推荐商品
      const favoriteSql = `
        SELECT 
          p.product_id,
          p.product_name,
          p.current_price,
          p.original_price,
          p.rating_average,
          p.rating_count,
          p.sales_count,
          pi.image_url,
          c.category_name
        FROM products p
        LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
        LEFT JOIN categories c ON p.category_id = c.category_id
        WHERE p.category_id IN (${placeholders})
          AND p.status = 1
          AND p.product_id NOT IN (
            SELECT product_id FROM user_favorites WHERE user_id = ?
          )
          AND p.product_id NOT IN (
            SELECT product_id FROM shopping_cart WHERE user_id = ?
          )
        ORDER BY p.rating_average DESC, p.sales_count DESC
        LIMIT ${parseInt(limit)}
      `;
      recommendedProducts = await query(favoriteSql, [...categoryIds, userId, userId]);
    }
    
    // 如果基于收藏的推荐不足，补充新品推荐
    if (recommendedProducts.length < parseInt(limit)) {
      const remaining = parseInt(limit) - recommendedProducts.length;
      const existingIds = recommendedProducts.map(p => p.product_id);
      const excludePlaceholders = existingIds.length > 0 ? existingIds.map(() => '?').join(',') : '0';
      
      const newProductsSql = `
        SELECT 
          p.product_id,
          p.product_name,
          p.current_price,
          p.original_price,
          p.rating_average,
          p.rating_count,
          p.sales_count,
          pi.image_url,
          c.category_name
        FROM products p
        LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
        LEFT JOIN categories c ON p.category_id = c.category_id
        WHERE p.status = 1
          AND p.is_new = 1
          ${existingIds.length > 0 ? `AND p.product_id NOT IN (${excludePlaceholders})` : ''}
          AND p.product_id NOT IN (
            SELECT product_id FROM user_favorites WHERE user_id = ?
          )
          AND p.product_id NOT IN (
            SELECT product_id FROM shopping_cart WHERE user_id = ?
          )
        ORDER BY p.create_time DESC
        LIMIT ${remaining}
      `;
      const newProducts = await query(newProductsSql, existingIds.length > 0 ? [...existingIds, userId, userId] : [userId, userId]);
      
      recommendedProducts = [...recommendedProducts, ...newProducts];
    }
    
    // 格式化推荐商品数据
    const formattedProducts = recommendedProducts.map(product => ({
      id: product.product_id,
      title: product.product_name,
      price: parseFloat(product.current_price || 0).toFixed(2),
      originalPrice: parseFloat(product.original_price || 0).toFixed(2),
      rating: parseFloat(product.rating_average) || 0,
      ratingCount: product.rating_count || 0,
      salesCount: product.sales_count || 0,
      image: product.image_url || '/img/default-product.jpg',
      categoryName: product.category_name
    }));
    
    res.json({
      success: true,
      message: '获取个性化推荐成功',
      data: formattedProducts
    });
    
  } catch (error) {
    console.error('获取个性化推荐失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

module.exports = router;