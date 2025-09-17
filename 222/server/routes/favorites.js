const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { verifyToken } = require('../middleware/auth');
const { validateRequired } = require('../utils/validator');

// 获取用户收藏列表
router.get('/', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { page = 1, limit = 20 } = req.query;
    
    const offset = (page - 1) * limit;
    
    // 获取收藏商品列表
    const favorites = await query(`
      SELECT 
        uf.favorite_id,
        uf.create_time as favorite_time,
        p.product_id,
        p.product_name,
        p.description,
        p.current_price,
        p.original_price,
        p.rating_average,
        p.rating_count,
        p.sales_count,
        p.stock_quantity,
        p.status,
        pi.image_url
      FROM user_favorites uf
      LEFT JOIN products p ON uf.product_id = p.product_id
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      WHERE uf.user_id = ?
      ORDER BY uf.create_time DESC
      LIMIT ? OFFSET ?
    `, [userId, parseInt(limit), offset]);

    // 获取总数
    const totalResult = await query(
      'SELECT COUNT(*) as total FROM user_favorites WHERE user_id = ?',
      [userId]
    );
    const total = totalResult[0].total;

    // 格式化收藏数据
    const formattedFavorites = favorites.map(item => ({
      favoriteId: item.favorite_id,
      productId: item.product_id,
      title: item.product_name,
      description: item.description,
      price: parseFloat(item.current_price || 0).toFixed(2),
      originalPrice: parseFloat(item.original_price || 0).toFixed(2),
      rating: parseFloat(item.rating_average) || 0,
      ratingCount: item.rating_count || 0,
      salesCount: item.sales_count || 0,
      stockQuantity: item.stock_quantity || 0,
      status: item.status,
      image: item.image_url || '/img/default-product.jpg',
      favoriteTime: item.favorite_time
    }));

    res.json({
      success: true,
      message: '获取收藏列表成功',
      data: {
        favorites: formattedFavorites,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total: total,
          totalPages: Math.ceil(total / limit)
        }
      }
    });

  } catch (error) {
    console.error('获取收藏列表失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 添加商品到收藏
router.post('/add', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { productId } = req.body;

    // 参数验证
    if (!validateRequired(productId)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID不能为空'
      });
    }

    // 检查商品是否存在
    const product = await query(
      'SELECT product_id, status FROM products WHERE product_id = ?',
      [productId]
    );

    if (product.length === 0) {
      return res.status(404).json({
        success: false,
        message: '商品不存在'
      });
    }

    if (product[0].status !== 1) {
      return res.status(400).json({
        success: false,
        message: '商品已下架，无法收藏'
      });
    }

    // 检查是否已经收藏
    const existingFavorite = await query(
      'SELECT favorite_id FROM user_favorites WHERE user_id = ? AND product_id = ?',
      [userId, productId]
    );

    if (existingFavorite.length > 0) {
      return res.status(400).json({
        success: false,
        message: '商品已在收藏夹中'
      });
    }

    // 添加收藏
    await query(
      'INSERT INTO user_favorites (user_id, product_id) VALUES (?, ?)',
      [userId, productId]
    );

    res.json({
      success: true,
      message: '收藏成功'
    });

  } catch (error) {
    console.error('添加收藏失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 取消收藏
router.delete('/remove', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { productId } = req.body;

    // 参数验证
    if (!validateRequired(productId)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID不能为空'
      });
    }

    // 检查收藏是否存在
    const favorite = await query(
      'SELECT favorite_id FROM user_favorites WHERE user_id = ? AND product_id = ?',
      [userId, productId]
    );

    if (favorite.length === 0) {
      return res.status(404).json({
        success: false,
        message: '收藏不存在'
      });
    }

    // 删除收藏
    await query(
      'DELETE FROM user_favorites WHERE user_id = ? AND product_id = ?',
      [userId, productId]
    );

    res.json({
      success: true,
      message: '取消收藏成功'
    });

  } catch (error) {
    console.error('取消收藏失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 检查商品是否已收藏
router.get('/check/:productId', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { productId } = req.params;

    // 参数验证
    if (!validateRequired(productId)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID不能为空'
      });
    }

    // 检查是否已收藏
    const favorite = await query(
      'SELECT favorite_id FROM user_favorites WHERE user_id = ? AND product_id = ?',
      [userId, productId]
    );

    res.json({
      success: true,
      message: '检查收藏状态成功',
      data: {
        isFavorite: favorite.length > 0
      }
    });

  } catch (error) {
    console.error('检查收藏状态失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 批量取消收藏
router.delete('/batch-remove', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { productIds } = req.body;

    // 参数验证
    if (!Array.isArray(productIds) || productIds.length === 0) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID列表不能为空'
      });
    }

    // 构建删除条件
    const placeholders = productIds.map(() => '?').join(',');
    const deleteQuery = `DELETE FROM user_favorites WHERE user_id = ? AND product_id IN (${placeholders})`;
    
    // 删除收藏
    const result = await query(deleteQuery, [userId, ...productIds]);

    res.json({
      success: true,
      message: '批量取消收藏成功',
      data: {
        removedCount: result.affectedRows
      }
    });

  } catch (error) {
    console.error('批量取消收藏失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取收藏统计信息
router.get('/stats', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    
    const stats = await query(`
      SELECT 
        COUNT(*) as total_favorites,
        COUNT(CASE WHEN p.status = 1 THEN 1 END) as available_favorites,
        COUNT(CASE WHEN p.status = 0 THEN 1 END) as unavailable_favorites
      FROM user_favorites uf
      LEFT JOIN products p ON uf.product_id = p.product_id
      WHERE uf.user_id = ?
    `, [userId]);

    const result = stats[0] || {
      total_favorites: 0,
      available_favorites: 0,
      unavailable_favorites: 0
    };

    res.json({
      success: true,
      message: '获取收藏统计成功',
      data: {
        totalFavorites: result.total_favorites,
        availableFavorites: result.available_favorites,
        unavailableFavorites: result.unavailable_favorites
      }
    });

  } catch (error) {
    console.error('获取收藏统计失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

module.exports = router;