// API配置文件
const config = {
  // 开发环境API基础URL
  baseURL: 'http://localhost:3001/api',
  // baseURL: 'http://192.168.8.157:3000/api',
  
  // 生产环境API基础URL（部署时需要修改）
  // baseURL: 'https://your-domain.com/api',
  
  // 请求超时时间（毫秒）
  timeout: 10000,
  
  // API接口路径
  endpoints: {
    // 首页相关接口
    home: {
      hero: '/home/hero',
      stats: '/home/stats'
    },
    
    // 产品相关接口
    products: {
      list: '/products',
      detail: '/products/:id',
      search: '/products/search',
      related: '/products/:id/related'
    },
    
    // 分类相关接口
    categories: {
      list: '/categories',
      detail: '/categories/:id',
      products: '/categories/:id/products',
      tree: '/categories/tree',
      hot: '/categories/hot'
    },
    
    // 购物车相关接口
    cart: {
      list: '/cart',
      add: '/cart/add',
      update: '/cart/update',
      remove: '/cart/remove',
      clear: '/cart/clear',
      stats: '/cart/stats',
      updateSelected: '/cart/update-selected',
      selectAll: '/cart/select-all',
      batchDelete: '/cart/batch-delete'
    },
    
    // 收藏相关接口
    favorites: {
      list: '/favorites',
      add: '/favorites/add',
      remove: '/favorites/remove',
      check: '/favorites/check',
      batchRemove: '/favorites/batch-remove',
      stats: '/favorites/stats'
    },
    
    // 促销相关接口
    promotions: {
      list: '/promotions',
      detail: '/promotions/:id',
      active: '/promotions/active',
      checkProduct: '/promotions/check-product'
    },
    
    // 搜索相关接口
    search: {
      products: '/search/products',
      hotKeywords: '/search/hot-keywords',
      suggestions: '/search/suggestions',
      history: '/search/history',
      clearHistory: '/search/clear-history',
      stats: '/search/stats'
    },
    
    // 用户相关接口
    user: {
      profile: '/user/profile',
      favorites: '/user/favorites',
      orders: '/user/orders',
      coupons: '/user/coupons',
      addresses: '/user/addresses'
    },
    
    // 订单相关接口
    order: {
      create: '/orders/create',
      detail: '/orders/:id',
      list: '/orders',
      cancel: '/orders/:id/cancel',
      confirm: '/orders/:id/confirm',
      pay: '/orders/:id/pay'
    },
    
    // 推荐相关接口
    recommendations: {
      cart: '/recommendations/cart',
      personalized: '/recommendations/personalized'
    }
  }
};

// 获取完整的API URL
function getApiUrl(endpoint) {
  // 如果endpoint是完整路径，直接拼接
  if (endpoint.startsWith('/')) {
    return config.baseURL + endpoint;
  }
  
  // 如果是点分路径，从endpoints中获取
  const parts = endpoint.split('.');
  let path = config.endpoints;
  
  for (const part of parts) {
    if (path && path[part]) {
      path = path[part];
    } else {
      throw new Error(`API endpoint not found: ${endpoint}`);
    }
  }
  
  if (typeof path !== 'string') {
    throw new Error(`Invalid API endpoint: ${endpoint}`);
  }
  
  return config.baseURL + path;
}

// 封装请求函数
function request(options) {
  return new Promise((resolve, reject) => {
    const { url, data = {}, method = 'GET', header = {} } = options;
    
    // 构建完整的URL
    let fullUrl;
    if (url.startsWith('http')) {
      // 如果已经是完整URL，直接使用
      fullUrl = url;
    } else {
      // 否则通过getApiUrl构建
      fullUrl = getApiUrl(url);
    }
    
    // 构建查询参数
    let requestUrl = fullUrl;
    if (method === 'GET' && Object.keys(data).length > 0) {
      const params = Object.keys(data)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(data[key])}`)
        .join('&');
      requestUrl = `${fullUrl}?${params}`;
    }
    
    // 获取用户token
    const userInfo = wx.getStorageSync('userInfo');
    const token = userInfo ? userInfo.token : null;
    
    // 构建请求头
    const requestHeader = {
      'Content-Type': 'application/json',
      ...header
    };
    
    // 如果有token，添加到请求头
    if (token) {
      requestHeader['Authorization'] = `Bearer ${token}`;
    }
    
    wx.request({
      url: requestUrl,
      data: method === 'GET' ? {} : data,
      method: method,
      header: requestHeader,
      timeout: config.timeout,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else {
          reject(new Error(`请求失败: ${res.statusCode}`));
        }
      },
      fail: (err) => {
        reject(new Error(`网络请求失败: ${err.errMsg || '未知错误'}`));
      }
    });
  });
}

// 导出配置
module.exports = {
  config,
  getApiUrl,
  request
};