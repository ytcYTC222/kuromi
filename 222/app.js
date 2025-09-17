// app.js
App({
  onLaunch: function () {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
  },
  globalData: {
    userInfo: null,
    cartItems: 3, // 购物车商品数量
    cartNeedsRefresh: false // 购物车是否需要刷新
  }
})