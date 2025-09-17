const { getApiUrl } = require('../config/api.js')

/**
 * 封装的请求函数，自动处理JWT认证
 * @param {Object} options 请求选项
 * @param {string} options.url 请求路径（不包含域名）
 * @param {string} options.method 请求方法，默认GET
 * @param {Object} options.data 请求数据
 * @param {Object} options.header 额外的请求头
 * @returns {Promise} 返回Promise对象
 */
function request(options) {
  return new Promise((resolve, reject) => {
    // 获取用户信息和token
    const userInfo = wx.getStorageSync('userInfo')
    
    // 构建请求头
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }
    
    // 如果有token，添加到请求头
    if (userInfo && userInfo.token) {
      header['Authorization'] = `Bearer ${userInfo.token}`
    }
    
    wx.request({
      url: getApiUrl(options.url),
      method: options.method || 'GET',
      data: options.data || {},
      header: header,
      success: function(res) {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // token过期或无效，清除本地存储并跳转到登录页
          wx.removeStorageSync('userInfo')
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/login/login'
            })
          }, 1500)
          reject(new Error('Unauthorized'))
        } else {
          reject(new Error(`Request failed with status ${res.statusCode}`))
        }
      },
      fail: function(error) {
        reject(error)
      }
    })
  })
}

module.exports = {
  request
}