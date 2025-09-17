const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const router = express.Router();
const { query } = require('../config/database');

// 生成JWT token
function generateToken(userId) {
  return jwt.sign({ userId }, process.env.JWT_SECRET || 'your-secret-key', {
    expiresIn: '7d'
  });
}

// 用户注册
router.post('/register', async (req, res) => {
  try {
    const { nickname, phone, email, password } = req.body;
    
    // 验证必填字段
    if (!nickname || !phone || !password) {
      return res.json({
        success: false,
        message: '昵称、手机号和密码为必填项'
      });
    }
    
    // 验证手机号格式
    const phoneRegex = /^1[3-9]\d{9}$/;
    if (!phoneRegex.test(phone)) {
      return res.json({
        success: false,
        message: '手机号格式不正确'
      });
    }
    
    // 验证密码长度
    if (password.length < 6 || password.length > 20) {
      return res.json({
        success: false,
        message: '密码长度为6-20位'
      });
    }
    
    // 检查手机号是否已存在
    const checkPhoneQuery = 'SELECT user_id FROM users WHERE phone = ?';
    const existingUsers = await query(checkPhoneQuery, [phone]);
    const existingUser = existingUsers[0];
    
    if (existingUser) {
      return res.json({
        success: false,
        message: '该手机号已被注册'
      });
    }
    
    // 加密密码
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    
    // 生成临时openid（实际项目中应该通过微信API获取）
    const tempOpenid = 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    
    // 插入用户数据
    const insertQuery = `
      INSERT INTO users (openid, nickname, phone, email, password, gender, birthday, register_time, last_login_time, status)
      VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 1)
    `;
    
    const result = await query(insertQuery, [tempOpenid, nickname, phone, email, hashedPassword, 0, null]);
    
    res.json({
      success: true,
      message: '注册成功',
      data: {
        userId: result.insertId
      }
    });
    
  } catch (error) {
    console.error('注册失败:', error);
    res.json({
      success: false,
      message: '注册失败，请稍后重试'
    });
  }
});

// 用户登录
router.post('/login', async (req, res) => {
  try {
    const { phone, password } = req.body;
    
    // 验证必填字段
    if (!phone || !password) {
      return res.json({
        success: false,
        message: '手机号和密码为必填项'
      });
    }
    
    // 查询用户
    const userQuery = 'SELECT user_id, nickname, phone, email, password, avatar_url, gender, birthday, status FROM users WHERE phone = ?';
    const users = await query(userQuery, [phone]);
    const user = users[0];
    
    if (!user) {
      return res.json({
        success: false,
        message: '用户不存在'
      });
    }
    
    if (user.status === 0) {
      return res.json({
        success: false,
        message: '账户已被禁用'
      });
    }
    
    // 验证密码
    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return res.json({
        success: false,
        message: '密码错误'
      });
    }
    
    // 更新最后登录时间
    const updateLoginTimeQuery = 'UPDATE users SET last_login_time = NOW() WHERE user_id = ?';
    await query(updateLoginTimeQuery, [user.user_id]);
    
    // 生成token
    const token = generateToken(user.user_id);
    
    // 返回用户信息（不包含密码）
    const userInfo = {
      userId: user.user_id,
      nickname: user.nickname,
      phone: user.phone,
      email: user.email,
      avatarUrl: user.avatar_url,
      gender: user.gender,
      birthday: user.birthday,
      token: token
    };
    
    res.json({
      success: true,
      message: '登录成功',
      data: userInfo
    });
    
  } catch (error) {
    console.error('登录失败:', error);
    res.json({
      success: false,
      message: '登录失败，请稍后重试'
    });
  }
});



// 验证token
router.post('/verify-token', async (req, res) => {
  try {
    const { token } = req.body;
    
    if (!token) {
      return res.json({
        success: false,
        message: '缺少token'
      });
    }
    
    // 验证token
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'your-secret-key');
    
    // 查询用户信息
    const userQuery = 'SELECT user_id, nickname, phone, email, avatar_url, gender, birthday, status FROM users WHERE user_id = ?';
    const users = await query(userQuery, [decoded.userId]);
    const user = users[0];
    
    if (!user || user.status === 0) {
      return res.json({
        success: false,
        message: 'token无效'
      });
    }
    
    const userInfo = {
      userId: user.user_id,
      nickname: user.nickname,
      phone: user.phone,
      email: user.email,
      avatarUrl: user.avatar_url,
      gender: user.gender,
      birthday: user.birthday,
      token: token
    };
    
    res.json({
      success: true,
      message: 'token有效',
      data: userInfo
    });
    
  } catch (error) {
    console.error('验证token失败:', error);
    res.json({
      success: false,
      message: 'token无效'
    });
  }
});

module.exports = router;