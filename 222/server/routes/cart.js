const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { verifyToken } = require('../middleware/auth');
const { validateRequired, validatePositiveInteger } = require('../utils/validator');

// 获取用户购物车
router.get('/', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    
    const cartItems = await query(`
      SELECT 
        sc.cart_id,
        sc.product_id,
        sc.quantity,
        sc.selected,
        p.product_name,
        p.current_price,
        p.original_price,
        p.stock_quantity,
        pi.image_url,
        GROUP_CONCAT(CONCAT(ps.spec_name, ': ', ps.spec_value) SEPARATOR ', ') as specifications
      FROM shopping_cart sc
      LEFT JOIN products p ON sc.product_id = p.product_id
      LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
      LEFT JOIN product_specifications ps ON p.product_id = ps.product_id
      WHERE sc.user_id = ? AND p.status = 1
      GROUP BY sc.cart_id, sc.product_id, sc.quantity, sc.selected, p.product_name, p.current_price, p.original_price, p.stock_quantity, pi.image_url
      ORDER BY sc.create_time DESC
    `, [userId]);

    // 格式化购物车数据
    const formattedItems = cartItems.map(item => ({
      cartId: item.cart_id,
      productId: item.product_id,
      title: item.product_name,
      price: parseFloat(item.current_price || 0).toFixed(2),
      originalPrice: parseFloat(item.original_price || 0).toFixed(2),
      quantity: item.quantity,
      selected: Boolean(item.selected),
      image: item.image_url || '/img/default-product.jpg',
      stockQuantity: item.stock_quantity,
      specifications: item.specifications || null
    }));

    res.json({
      success: true,
      message: '获取购物车成功',
      data: formattedItems
    });

  } catch (error) {
    console.error('获取购物车失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 添加商品到购物车
router.post('/add', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { productId, quantity = 1 } = req.body;

    // 参数验证
    const requiredValidation = validateRequired(['productId'], req.body);
    const quantityValidation = validatePositiveInteger(quantity, '数量');
    
    if (!requiredValidation.isValid) {
      return res.status(400).json({
        success: false,
        message: requiredValidation.message
      });
    }
    
    if (!quantityValidation.isValid) {
      return res.status(400).json({
        success: false,
        message: quantityValidation.message
      });
    }

    // 检查商品是否存在且有库存
    const product = await query(
      'SELECT product_id, stock_quantity, status FROM products WHERE product_id = ?',
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
        message: '商品已下架'
      });
    }

    if (product[0].stock_quantity < quantity) {
      return res.status(400).json({
        success: false,
        message: '库存不足'
      });
    }

    // 检查购物车中是否已存在该商品
    const existingItem = await query(
      'SELECT cart_id, quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?',
      [userId, productId]
    );

    if (existingItem.length > 0) {
      // 每次只增加1个数量
      const newQuantity = existingItem[0].quantity + 1;
      
      if (newQuantity > product[0].stock_quantity) {
        return res.status(400).json({
          success: false,
          message: '购物车中该商品数量已达库存上限'
        });
      }

      await query(
        'UPDATE shopping_cart SET quantity = ?, update_time = NOW() WHERE cart_id = ?',
        [newQuantity, existingItem[0].cart_id]
      );
    } else {
      // 添加新商品
      await query(
        'INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)',
        [userId, productId, quantity]
      );
    }

    res.json({
      success: true,
      message: '添加到购物车成功'
    });

  } catch (error) {
    console.error('添加到购物车失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 更新购物车商品数量
router.put('/update', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { cartId, quantity } = req.body;

    // 参数验证
    const requiredValidation = validateRequired(['cartId'], req.body);
    const quantityValidation = validatePositiveInteger(quantity, '数量');
    
    if (!requiredValidation.isValid) {
      return res.status(400).json({
        success: false,
        message: requiredValidation.message
      });
    }
    
    if (!quantityValidation.isValid) {
      return res.status(400).json({
        success: false,
        message: quantityValidation.message
      });
    }

    // 检查购物车项是否属于当前用户
    const cartItem = await query(
      'SELECT sc.cart_id, sc.product_id, p.stock_quantity FROM shopping_cart sc LEFT JOIN products p ON sc.product_id = p.product_id WHERE sc.cart_id = ? AND sc.user_id = ?',
      [cartId, userId]
    );

    if (cartItem.length === 0) {
      return res.status(404).json({
        success: false,
        message: '购物车项不存在'
      });
    }

    if (quantity > cartItem[0].stock_quantity) {
      return res.status(400).json({
        success: false,
        message: '数量超过库存限制'
      });
    }

    // 更新数量
    await query(
      'UPDATE shopping_cart SET quantity = ?, update_time = NOW() WHERE cart_id = ?',
      [quantity, cartId]
    );

    res.json({
      success: true,
      message: '更新购物车成功'
    });

  } catch (error) {
    console.error('更新购物车失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 更新购物车商品选中状态
router.put('/select', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { cartId, selected } = req.body;

    // 参数验证
    const requiredValidation = validateRequired(['cartId'], req.body);
    
    if (!requiredValidation.isValid || typeof selected !== 'boolean') {
      return res.status(400).json({
        success: false,
        message: !requiredValidation.isValid ? requiredValidation.message : '参数错误：selected必须为布尔值'
      });
    }

    // 检查购物车项是否属于当前用户
    const cartItem = await query(
      'SELECT cart_id FROM shopping_cart WHERE cart_id = ? AND user_id = ?',
      [cartId, userId]
    );

    if (cartItem.length === 0) {
      return res.status(404).json({
        success: false,
        message: '购物车项不存在'
      });
    }

    // 更新选中状态
    await query(
      'UPDATE shopping_cart SET selected = ?, update_time = NOW() WHERE cart_id = ?',
      [selected ? 1 : 0, cartId]
    );

    res.json({
      success: true,
      message: '更新选中状态成功'
    });

  } catch (error) {
    console.error('更新选中状态失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 全选/取消全选
router.put('/select-all', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { selected } = req.body;

    // 参数验证
    if (typeof selected !== 'boolean') {
      return res.status(400).json({
        success: false,
        message: '参数错误'
      });
    }

    // 更新所有购物车项的选中状态
    await query(
      'UPDATE shopping_cart SET selected = ?, update_time = NOW() WHERE user_id = ?',
      [selected ? 1 : 0, userId]
    );

    res.json({
      success: true,
      message: '更新全选状态成功'
    });

  } catch (error) {
    console.error('更新全选状态失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 删除购物车商品
router.delete('/remove', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { cartId } = req.body;

    // 参数验证
    const requiredValidation = validateRequired(['cartId'], req.body);
    
    if (!requiredValidation.isValid) {
      return res.status(400).json({
        success: false,
        message: requiredValidation.message
      });
    }

    // 检查购物车项是否属于当前用户
    const cartItem = await query(
      'SELECT cart_id FROM shopping_cart WHERE cart_id = ? AND user_id = ?',
      [cartId, userId]
    );

    if (cartItem.length === 0) {
      return res.status(404).json({
        success: false,
        message: '购物车项不存在'
      });
    }

    // 删除购物车项
    await query('DELETE FROM shopping_cart WHERE cart_id = ?', [cartId]);

    res.json({
      success: true,
      message: '删除成功'
    });

  } catch (error) {
    console.error('删除购物车项失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 批量删除购物车商品
router.delete('/batch-delete', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const { cartIds } = req.body;

    // 参数验证
    if (!cartIds || !Array.isArray(cartIds) || cartIds.length === 0) {
      return res.status(400).json({
        success: false,
        message: '请提供要删除的购物车项ID数组'
      });
    }

    // 检查购物车项是否属于当前用户
    const placeholders = cartIds.map(() => '?').join(',');
    const cartItems = await query(
      `SELECT cart_id FROM shopping_cart WHERE cart_id IN (${placeholders}) AND user_id = ?`,
      [...cartIds, userId]
    );

    if (cartItems.length !== cartIds.length) {
      return res.status(404).json({
        success: false,
        message: '部分购物车项不存在或不属于当前用户'
      });
    }

    // 批量删除购物车项
    await query(
      `DELETE FROM shopping_cart WHERE cart_id IN (${placeholders}) AND user_id = ?`,
      [...cartIds, userId]
    );

    res.json({
      success: true,
      message: '批量删除成功',
      data: {
        deletedCount: cartIds.length
      }
    });

  } catch (error) {
    console.error('批量删除购物车项失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 清空购物车
router.delete('/clear', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;

    // 删除用户所有购物车项
    await query('DELETE FROM shopping_cart WHERE user_id = ?', [userId]);

    res.json({
      success: true,
      message: '清空购物车成功'
    });

  } catch (error) {
    console.error('清空购物车失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取购物车统计信息
router.get('/stats', verifyToken, async (req, res) => {
  try {
    const userId = req.user.user_id;
    
    const stats = await query(`
      SELECT 
        COUNT(*) as total_items,
        SUM(quantity) as total_quantity,
        SUM(CASE WHEN selected = 1 THEN quantity ELSE 0 END) as selected_quantity,
        SUM(CASE WHEN selected = 1 THEN quantity * p.current_price ELSE 0 END) as selected_total_price
      FROM shopping_cart sc
      LEFT JOIN products p ON sc.product_id = p.product_id
      WHERE sc.user_id = ? AND p.status = 1
    `, [userId]);

    const result = stats[0] || {
      total_items: 0,
      total_quantity: 0,
      selected_quantity: 0,
      selected_total_price: 0
    };

    res.json({
      success: true,
      message: '获取购物车统计成功',
      data: {
        totalItems: result.total_items,
        totalQuantity: result.total_quantity,
        selectedQuantity: result.selected_quantity,
        selectedTotalPrice: parseFloat(result.selected_total_price || 0).toFixed(2)
      }
    });

  } catch (error) {
    console.error('获取购物车统计失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

module.exports = router;