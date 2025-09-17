// 验证工具函数

// 验证必填字段
const validateRequired = (fields, data) => {
  const missing = [];
  
  for (const field of fields) {
    if (!data[field] || (typeof data[field] === 'string' && data[field].trim() === '')) {
      missing.push(field);
    }
  }
  
  if (missing.length > 0) {
    return {
      isValid: false,
      message: `缺少必填字段: ${missing.join(', ')}`
    };
  }
  
  return { isValid: true };
};

// 验证正整数
const validatePositiveInteger = (value, fieldName = '值') => {
  const num = parseInt(value);
  
  if (isNaN(num) || num <= 0) {
    return {
      isValid: false,
      message: `${fieldName}必须是正整数`
    };
  }
  
  return { isValid: true, value: num };
};

// 验证邮箱格式
const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  
  if (!emailRegex.test(email)) {
    return {
      isValid: false,
      message: '邮箱格式不正确'
    };
  }
  
  return { isValid: true };
};

// 验证手机号格式
const validatePhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  
  if (!phoneRegex.test(phone)) {
    return {
      isValid: false,
      message: '手机号格式不正确'
    };
  }
  
  return { isValid: true };
};

// 验证密码强度
const validatePassword = (password) => {
  if (password.length < 6) {
    return {
      isValid: false,
      message: '密码长度至少6位'
    };
  }
  
  return { isValid: true };
};

// 验证价格格式
const validatePrice = (price, fieldName = '价格') => {
  const num = parseFloat(price);
  
  if (isNaN(num) || num < 0) {
    return {
      isValid: false,
      message: `${fieldName}必须是非负数`
    };
  }
  
  return { isValid: true, value: num };
};

module.exports = {
  validateRequired,
  validatePositiveInteger,
  validateEmail,
  validatePhone,
  validatePassword,
  validatePrice
};