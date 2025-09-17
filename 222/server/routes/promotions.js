const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { validateRequired, validatePositiveInteger } = require('../utils/validator');

// 获取促销活动列表
router.get('/', async (req, res) => {
  try {
    const {
      page = 1,
      limit = 20,
      type,
      status = 'active'
    } = req.query;
    
    const offset = (page - 1) * limit;
    
    // 构建查询条件
    let whereConditions = [];
    let queryParams = [];
    
    if (type) {
      whereConditions.push('promotion_type = ?');
      queryParams.push(parseInt(type));
    }
    
    if (status === 'active') {
      whereConditions.push('is_active = 1');
      whereConditions.push('start_time <= NOW()');
      whereConditions.push('end_time >= NOW()');
    } else if (status === 'inactive') {
      whereConditions.push('is_active = 0 OR start_time > NOW() OR end_time < NOW()');
    }
    
    const whereClause = whereConditions.length > 0 ? 'WHERE ' + whereConditions.join(' AND ') : '';
    
    // 获取促销活动列表
    const promotions = await query(`
      SELECT 
        promotion_id,
        promotion_name,
        promotion_type,
        target_id,
        discount_type,
        discount_value,
        min_amount,
        max_discount,
        start_time,
        end_time,
        is_active,
        sort_order,
        banner_image,
        description,
        create_time
      FROM promotions
      ${whereClause}
      ORDER BY sort_order ASC, create_time DESC
      LIMIT ? OFFSET ?
    `, [...queryParams, parseInt(limit), offset]);
    
    // 获取总数
    const totalResult = await query(`
      SELECT COUNT(*) as total
      FROM promotions
      ${whereClause}
    `, queryParams);
    const total = totalResult[0].total;
    
    // 格式化促销活动数据
    const formattedPromotions = promotions.map(promotion => ({
      id: promotion.promotion_id,
      name: promotion.promotion_name,
      type: promotion.promotion_type,
      typeText: getPromotionTypeText(promotion.promotion_type),
      targetId: promotion.target_id,
      discountType: promotion.discount_type,
      discountTypeText: promotion.discount_type === 1 ? '百分比折扣' : '固定金额减免',
      discountValue: parseFloat(promotion.discount_value),
      minAmount: parseFloat(promotion.min_amount || 0),
      maxDiscount: promotion.max_discount ? parseFloat(promotion.max_discount) : null,
      startTime: promotion.start_time,
      endTime: promotion.end_time,
      isActive: Boolean(promotion.is_active),
      sortOrder: promotion.sort_order,
      bannerImage: promotion.banner_image,
      description: promotion.description,
      createTime: promotion.create_time
    }));
    
    res.json({
      success: true,
      message: '获取促销活动列表成功',
      data: {
        promotions: formattedPromotions,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total: total,
          totalPages: Math.ceil(total / limit)
        }
      }
    });
    
  } catch (error) {
    console.error('获取促销活动列表失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取促销活动详情
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    
    // 参数验证
    const validation = validateRequired(['id'], { id });
    if (!validation.isValid || !validatePositiveInteger(id)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：促销活动ID必须为正整数'
      });
    }
    
    // 获取促销活动详情
    const promotionResult = await query(`
      SELECT 
        p.*,
        CASE 
          WHEN p.promotion_type = 1 THEN prod.product_name
          WHEN p.promotion_type = 2 THEN cat.category_name
          ELSE '全场促销'
        END as target_name
      FROM promotions p
      LEFT JOIN products prod ON p.promotion_type = 1 AND p.target_id = prod.product_id
      LEFT JOIN categories cat ON p.promotion_type = 2 AND p.target_id = cat.category_id
      WHERE p.promotion_id = ?
    `, [id]);
    
    if (promotionResult.length === 0) {
      return res.status(404).json({
        success: false,
        message: '促销活动不存在'
      });
    }
    
    const promotion = promotionResult[0];
    
    // 获取适用的商品列表
    let applicableProducts = [];
    if (promotion.promotion_type === 1) {
      // 单商品促销
      applicableProducts = await query(`
        SELECT 
          p.product_id,
          p.product_name,
          p.current_price,
          p.original_price,
          pi.image_url
        FROM products p
        LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
        WHERE p.product_id = ? AND p.status = 1
      `, [promotion.target_id]);
    } else if (promotion.promotion_type === 2) {
      // 分类促销
      applicableProducts = await query(`
        SELECT 
          p.product_id,
          p.product_name,
          p.current_price,
          p.original_price,
          pi.image_url
        FROM products p
        LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
        WHERE p.category_id = ? AND p.status = 1
        ORDER BY p.sales_count DESC
        LIMIT 10
      `, [promotion.target_id]);
    } else {
      // 全场促销
      applicableProducts = await query(`
        SELECT 
          p.product_id,
          p.product_name,
          p.current_price,
          p.original_price,
          pi.image_url
        FROM products p
        LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.image_type = 1
        WHERE p.status = 1
        ORDER BY p.sales_count DESC
        LIMIT 10
      `);
    }
    
    // 格式化促销活动详情
    const promotionDetail = {
      id: promotion.promotion_id,
      name: promotion.promotion_name,
      type: promotion.promotion_type,
      typeText: getPromotionTypeText(promotion.promotion_type),
      targetId: promotion.target_id,
      targetName: promotion.target_name,
      discountType: promotion.discount_type,
      discountTypeText: promotion.discount_type === 1 ? '百分比折扣' : '固定金额减免',
      discountValue: parseFloat(promotion.discount_value),
      minAmount: parseFloat(promotion.min_amount || 0),
      maxDiscount: promotion.max_discount ? parseFloat(promotion.max_discount) : null,
      startTime: promotion.start_time,
      endTime: promotion.end_time,
      isActive: Boolean(promotion.is_active),
      sortOrder: promotion.sort_order,
      bannerImage: promotion.banner_image,
      description: promotion.description,
      applicableProducts: applicableProducts.map(product => ({
        id: product.product_id,
        name: product.product_name,
        currentPrice: parseFloat(product.current_price).toFixed(2),
        originalPrice: parseFloat(product.original_price).toFixed(2),
        image: product.image_url || '/img/default-product.jpg'
      })),
      createTime: promotion.create_time,
      updateTime: promotion.update_time
    };
    
    res.json({
      success: true,
      message: '获取促销活动详情成功',
      data: promotionDetail
    });
    
  } catch (error) {
    console.error('获取促销活动详情失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 获取当前有效的促销活动
router.get('/active/list', async (req, res) => {
  try {
    const { limit = 10 } = req.query;
    
    // 获取当前有效的促销活动
    const activePromotions = await query(`
      SELECT 
        promotion_id,
        promotion_name,
        promotion_type,
        discount_type,
        discount_value,
        min_amount,
        banner_image,
        description,
        start_time,
        end_time
      FROM promotions
      WHERE is_active = 1
        AND start_time <= NOW()
        AND end_time >= NOW()
      ORDER BY sort_order ASC
      LIMIT ?
    `, [parseInt(limit)]);
    
    // 格式化数据
    const formattedPromotions = activePromotions.map(promotion => ({
      id: promotion.promotion_id,
      name: promotion.promotion_name,
      type: promotion.promotion_type,
      typeText: getPromotionTypeText(promotion.promotion_type),
      discountType: promotion.discount_type,
      discountValue: parseFloat(promotion.discount_value),
      minAmount: parseFloat(promotion.min_amount || 0),
      bannerImage: promotion.banner_image,
      description: promotion.description,
      startTime: promotion.start_time,
      endTime: promotion.end_time
    }));
    
    res.json({
      success: true,
      message: '获取有效促销活动成功',
      data: formattedPromotions
    });
    
  } catch (error) {
    console.error('获取有效促销活动失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 检查商品是否参与促销
router.get('/check/:productId', async (req, res) => {
  try {
    const { productId } = req.params;
    
    // 参数验证
    const validation = validateRequired(['productId'], { productId });
    if (!validation.isValid || !validatePositiveInteger(productId)) {
      return res.status(400).json({
        success: false,
        message: '参数错误：商品ID必须为正整数'
      });
    }
    
    // 获取商品信息
    const productResult = await query(
      'SELECT product_id, category_id, current_price FROM products WHERE product_id = ? AND status = 1',
      [productId]
    );
    
    if (productResult.length === 0) {
      return res.status(404).json({
        success: false,
        message: '商品不存在'
      });
    }
    
    const product = productResult[0];
    
    // 检查适用的促销活动
    const applicablePromotions = await query(`
      SELECT 
        promotion_id,
        promotion_name,
        promotion_type,
        discount_type,
        discount_value,
        min_amount,
        max_discount
      FROM promotions
      WHERE is_active = 1
        AND start_time <= NOW()
        AND end_time >= NOW()
        AND (
          (promotion_type = 1 AND target_id = ?) OR
          (promotion_type = 2 AND target_id = ?) OR
          promotion_type = 3
        )
      ORDER BY 
        CASE promotion_type
          WHEN 1 THEN 1
          WHEN 2 THEN 2
          WHEN 3 THEN 3
        END,
        discount_value DESC
    `, [productId, product.category_id]);
    
    // 计算最优促销价格
    let bestPromotion = null;
    let bestPrice = parseFloat(product.current_price);
    
    for (const promotion of applicablePromotions) {
      let promotionPrice = parseFloat(product.current_price);
      
      if (promotion.discount_type === 1) {
        // 百分比折扣
        promotionPrice = promotionPrice * (1 - promotion.discount_value / 100);
      } else if (promotion.discount_type === 2) {
        // 固定金额减免
        promotionPrice = Math.max(0, promotionPrice - promotion.discount_value);
      }
      
      // 应用最大优惠限制
      if (promotion.max_discount) {
        const maxDiscountAmount = parseFloat(product.current_price) - promotion.max_discount;
        promotionPrice = Math.max(promotionPrice, maxDiscountAmount);
      }
      
      if (promotionPrice < bestPrice) {
        bestPrice = promotionPrice;
        bestPromotion = promotion;
      }
    }
    
    const result = {
      productId: parseInt(productId),
      originalPrice: parseFloat(product.current_price).toFixed(2),
      hasPromotion: bestPromotion !== null,
      promotionPrice: bestPrice.toFixed(2),
      savings: bestPromotion ? (parseFloat(product.current_price) - bestPrice).toFixed(2) : '0.00',
      promotion: bestPromotion ? {
        id: bestPromotion.promotion_id,
        name: bestPromotion.promotion_name,
        type: bestPromotion.promotion_type,
        discountType: bestPromotion.discount_type,
        discountValue: parseFloat(bestPromotion.discount_value),
        minAmount: parseFloat(bestPromotion.min_amount || 0)
      } : null,
      allPromotions: applicablePromotions.map(promo => ({
        id: promo.promotion_id,
        name: promo.promotion_name,
        type: promo.promotion_type,
        discountType: promo.discount_type,
        discountValue: parseFloat(promo.discount_value)
      }))
    };
    
    res.json({
      success: true,
      message: '检查商品促销成功',
      data: result
    });
    
  } catch (error) {
    console.error('检查商品促销失败:', error);
    res.status(500).json({
      success: false,
      message: '服务器内部错误',
      error: error.message
    });
  }
});

// 辅助函数：获取促销类型文本
function getPromotionTypeText(type) {
  switch (type) {
    case 1:
      return '商品促销';
    case 2:
      return '分类促销';
    case 3:
      return '全场促销';
    default:
      return '未知类型';
  }
}

module.exports = router;