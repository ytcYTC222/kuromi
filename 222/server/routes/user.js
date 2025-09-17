const express = require('express');
const router = express.Router();
const { query } = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// 获取用户地址列表 (暂时移除认证用于测试)
router.get('/addresses', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    
    const addresses = await query(
      `SELECT 
        address_id,
        receiver_name,
        receiver_phone,
        province,
        city,
        district,
        detail_address,
        is_default,
        create_time
      FROM user_addresses 
      WHERE user_id = ?
      ORDER BY is_default DESC, create_time DESC`,
      [userId]
    );
    
    res.json({
      success: true,
      code: 200,
      message: '获取地址列表成功',
      data: addresses
    });
  } catch (error) {
    console.error('获取用户地址失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '获取地址列表失败',
      error: error.message
    });
  }
});

// 添加用户地址 (暂时移除认证用于测试)
router.post('/addresses', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    const {
      receiver_name,
      receiver_phone,
      province,
      city,
      district,
      detail_address,
      is_default = false
    } = req.body;
    
    // 如果设置为默认地址，先将其他地址设为非默认
    if (is_default) {
      await query(
        'UPDATE user_addresses SET is_default = 0 WHERE user_id = ?',
        [userId]
      );
    }
    
    const result = await query(
      `INSERT INTO user_addresses (
        user_id, receiver_name, receiver_phone, province, city, district, 
        detail_address, is_default, create_time
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())`,
      [userId, receiver_name, receiver_phone, province, city, district, detail_address, is_default ? 1 : 0]
    );
    
    res.json({
      success: true,
      code: 200,
      message: '添加地址成功',
      data: {
        address_id: result.insertId
      }
    });
  } catch (error) {
    console.error('添加用户地址失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '添加地址失败',
      error: error.message
    });
  }
});

// 更新用户地址 (暂时移除认证用于测试)
router.put('/addresses/:id', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    const addressId = req.params.id;
    const {
      receiver_name,
      receiver_phone,
      province,
      city,
      district,
      detail_address,
      is_default = false
    } = req.body;
    
    // 如果设置为默认地址，先将其他地址设为非默认
    if (is_default) {
      await query(
        'UPDATE user_addresses SET is_default = 0 WHERE user_id = ? AND address_id != ?',
        [userId, addressId]
      );
    }
    
    await query(
      `UPDATE user_addresses SET 
        receiver_name = ?, receiver_phone = ?, province = ?, city = ?, 
        district = ?, detail_address = ?, is_default = ?, update_time = NOW()
      WHERE address_id = ? AND user_id = ?`,
      [receiver_name, receiver_phone, province, city, district, detail_address, is_default ? 1 : 0, addressId, userId]
    );
    
    res.json({
      success: true,
      code: 200,
      message: '更新地址成功'
    });
  } catch (error) {
    console.error('更新用户地址失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '更新地址失败',
      error: error.message
    });
  }
});

// 删除用户地址 (暂时移除认证用于测试)
router.delete('/addresses/:id', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    const addressId = req.params.id;
    
    await query(
      'DELETE FROM user_addresses WHERE address_id = ? AND user_id = ?',
      [addressId, userId]
    );
    
    res.json({
      success: true,
      code: 200,
      message: '删除地址成功'
    });
  } catch (error) {
    console.error('删除用户地址失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '删除地址失败',
      error: error.message
    });
  }
});

// 获取用户可用优惠券 (暂时移除认证用于测试)
router.get('/coupons', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    const { status = 0, amount = 0 } = req.query; // status: 0-未使用, 1-已使用, 2-已过期
    
    let whereClause = 'WHERE uc.user_id = ?';
    let params = [userId];
    
    if (status == 0) {
      whereClause += ' AND uc.status = 0 AND c.end_time > NOW()';
    } else if (status == 1) {
      whereClause += ' AND uc.status = 1';
    } else if (status == 2) {
      whereClause += ' AND (uc.status = 2 OR c.end_time <= NOW())';
    }
    
    // 如果提供了金额，只返回满足条件的优惠券
    if (amount > 0) {
      whereClause += ' AND c.min_amount <= ?';
      params.push(amount);
    }
    
    const coupons = await query(
      `SELECT 
        uc.user_coupon_id,
        c.coupon_id,
        c.coupon_name,
        c.coupon_type,
        c.discount_value,
        c.min_amount,
        c.max_discount,
        c.start_time,
        c.end_time,
        uc.status,
        uc.receive_time,
        uc.use_time
      FROM user_coupons uc
      JOIN coupons c ON uc.coupon_id = c.coupon_id
      ${whereClause}
      ORDER BY c.discount_value DESC, c.end_time ASC`,
      params
    );
    
    res.json({
      success: true,
      code: 200,
      message: '获取优惠券列表成功',
      data: coupons
    });
  } catch (error) {
    console.error('获取用户优惠券失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '获取优惠券列表失败',
      error: error.message
    });
  }
});

// 获取用户信息 (暂时移除认证用于测试)
router.get('/profile', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    
    const users = await query(
      `SELECT 
        user_id,
        username,
        nickname,
        email,
        phone,
        avatar,
        gender,
        birthday,
        create_time
      FROM users 
      WHERE user_id = ?`,
      [userId]
    );
    
    if (users.length === 0) {
      return res.status(404).json({
        success: false,
        code: 404,
        message: '用户不存在'
      });
    }
    
    res.json({
      success: true,
      code: 200,
      message: '获取用户信息成功',
      data: users[0]
    });
  } catch (error) {
    console.error('获取用户信息失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '获取用户信息失败',
      error: error.message
    });
  }
});

// 更新用户信息 (暂时移除认证用于测试)
router.put('/profile', async (req, res) => {
  try {
    const userId = 1; // 暂时使用固定用户ID用于测试
    const {
      nickname,
      email,
      phone,
      avatar,
      gender,
      birthday
    } = req.body;
    
    await query(
      `UPDATE users SET 
        nickname = ?, email = ?, phone = ?, avatar = ?, 
        gender = ?, birthday = ?, update_time = NOW()
      WHERE user_id = ?`,
      [nickname, email, phone, avatar, gender, birthday, userId]
    );
    
    res.json({
      success: true,
      code: 200,
      message: '更新用户信息成功'
    });
  } catch (error) {
    console.error('更新用户信息失败:', error);
    res.status(500).json({
      success: false,
      code: 500,
      message: '更新用户信息失败',
      error: error.message
    });
  }
});

module.exports = router;