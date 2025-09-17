// 认证中间件
const jwt = require('jsonwebtoken');

// 验证JWT令牌
const verifyToken = (req, res, next) => {
  const token = req.headers.authorization?.replace('Bearer ', '') || req.headers['x-access-token'];
  
  if (!token) {
    return res.status(401).json({
      success: false,
      message: '未提供访问令牌'
    });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'your-secret-key');
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(401).json({
      success: false,
      message: '无效的访问令牌'
    });
  }
};

// 可选的认证中间件（不强制要求登录）
const optionalAuth = (req, res, next) => {
  const token = req.headers.authorization?.replace('Bearer ', '') || req.headers['x-access-token'];
  
  if (token) {
    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET || 'your-secret-key');
      req.user = decoded;
    } catch (error) {
      // 忽略错误，继续执行
    }
  }
  
  next();
};

module.exports = {
  verifyToken,
  optionalAuth
};