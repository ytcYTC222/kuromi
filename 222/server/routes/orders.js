const express = require('express');
const router = express.Router();
const { query } = require('../config/database');

// 创建订单
router.post('/create', async (req, res) => {
  try {
    const {
      items,
      address,
      coupon_id,
      remark,
      total_amount,
      discount_amount = 0,
      shipping_fee = 0,
      actual_amount
    } = req.body;

    // 验证必要字段
    if (!items || !Array.isArray(items) || items.length === 0) {
      return res.status(400).json({
        success: false,
        code: 400,
        message: '订单商品不能为空'
      });
    }

    if (!address || !address.receiver_name || !address.receiver_phone || !address.receiver_address) {
      return res.status(400).json({
        success: false,
        code: 400,
        message: '收货地址信息不完整'
      });
    }

    if (!total_amount || !actual_amount) {
      return res.status(400).json({
        success: false,
        code: 400,
        message: '订单金额不能为空'
      });
    }

    // 生成订单号
    const orderNo = 'ORD' + Date.now() + Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    
    // 暂时使用固定用户ID（实际项目中应该从token中获取）
    const userId = 1;

    // 插入订单主表
    const orderResult = await query(
      `INSERT INTO orders (
        order_no, user_id, order_status, payment_status,
        total_amount, discount_amount, shipping_fee, actual_amount,
        receiver_name, receiver_phone, receiver_address, remark
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        orderNo, userId, 1, 0, // order_status=1(待付款), payment_status=0(未支付)
        total_amount, discount_amount, shipping_fee, actual_amount,
        address.receiver_name, address.receiver_phone, address.receiver_address, remark || null
      ]
    );

    const orderId = orderResult.insertId;

    // 插入订单商品表
    for (const item of items) {
      await query(
        `INSERT INTO order_items (
          order_id, product_id, product_name, product_image,
          product_price, quantity, total_price
        ) VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [
          orderId,
          item.product_id,
          item.product_name || '商品',
          item.product_image || '',
          item.price,
          item.quantity,
          item.price * item.quantity
        ]
      );
    }

    res.json({
      success: true,
      code: 200,
      message: '订单创建成功',
      data: {
        order_id: orderId,
        order_no: orderNo
      }
    });

  } catch (error) {
    console.error('创建订单失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '创建订单失败',
      error: error.message
    });
  }
});

// 获取订单详情
router.get('/:id', async (req, res) => {
  try {
    const orderId = req.params.id;
    
    // 获取订单基本信息
    const orderResult = await query(
      `SELECT * FROM orders WHERE order_id = ?`,
      [orderId]
    );

    if (orderResult.length === 0) {
      return res.status(404).json({
        success: false,
        code: 404,
        message: '订单不存在'
      });
    }

    const order = orderResult[0];

    // 获取订单商品
    const items = await query(
      `SELECT * FROM order_items WHERE order_id = ?`,
      [orderId]
    );

    order.items = items;

    res.json({
      success: true,
      code: 200,
      message: '获取订单详情成功',
      data: order
    });

  } catch (error) {
    console.error('获取订单详情失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '获取订单详情失败',
      error: error.message
    });
  }
});

// 取消订单
router.post('/:id/cancel', async (req, res) => {
  try {
    const orderId = req.params.id;
    
    // 检查订单状态
    const orderResult = await query(
      `SELECT order_status FROM orders WHERE order_id = ?`,
      [orderId]
    );

    if (orderResult.length === 0) {
      return res.status(404).json({
        success: false,
        code: 404,
        message: '订单不存在'
      });
    }

    const order = orderResult[0];
    if (order.order_status !== 1) {
      return res.status(400).json({
        success: false,
        code: 400,
        message: '只能取消待付款订单'
      });
    }

    // 更新订单状态为已取消
    await query(
      `UPDATE orders SET order_status = 6, cancel_time = NOW() WHERE order_id = ?`,
      [orderId]
    );

    res.json({
      success: true,
      code: 200,
      message: '订单取消成功'
    });

  } catch (error) {
    console.error('取消订单失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '取消订单失败',
      error: error.message
    });
  }
});

module.exports = router;