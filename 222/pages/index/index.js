const app = getApp()
const { getApiUrl, request } = require('../../config/api.js')

Page({
  data: {
    banners: [],
    promotions: [],
    categories: [],
    hotProducts: [],
    loading: true,
    refreshing: false,
    // 热门产品分页相关
    hotProductsPage: 1,
    hotProductsTotal: 0,
    hotProductsHasNext: false,
    hotProductsLoading: false,
    // 添加购物车loading状态
    addingToCart: false,
    // 用户信息
    userInfo: null
  },

  onLoad: function () {
    // 检查登录状态
    this.checkLoginStatus();
    // 页面加载时获取首页数据
    this.loadHomeData();
    // 加载热门产品第一页
    this.loadHotProducts(1);
  },

  // 加载首页数据
  loadHomeData: function(isRefresh = false) {
    const that = this;
    
    // 如果不是刷新操作，显示loading
    if (!isRefresh) {
      that.setData({ loading: true });
    }
    
    wx.request({
      url: getApiUrl('/home/hero'),
      method: 'GET',
      success: function(res) {
        if (res.data.success) {
          const data = res.data.data;
          that.setData({
            banners: data.banners || [],
            promotions: data.promotions || [],
            categories: data.categories || [],
            loading: false,
            refreshing: false
          });
          
          // 刷新成功提示
          if (isRefresh) {
            wx.showToast({
              title: '刷新成功',
              icon: 'success',
              duration: 1500
            });
          }
        } else {
          console.error('获取首页数据失败:', res.data.message);
          that.setData({ 
            loading: false,
            refreshing: false
          });
        }
      },
      fail: function(error) {
        console.error('请求失败:', error);
        that.setData({ 
          loading: false,
          refreshing: false
        });
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 下拉刷新
  onRefresh: function() {
    this.setData({ refreshing: true });
    this.loadHomeData(true);
    // 刷新时重新加载热门产品第一页
    this.loadHotProducts(1);
  },

  // 加载热门产品分页数据
  loadHotProducts: function(page = 1) {
    const that = this;
    
    that.setData({ hotProductsLoading: true });
    
    wx.request({
      url: getApiUrl('/home/hot-products'),
      method: 'GET',
      data: {
        page: page
      },
      success: function(res) {
        if (res.data.success) {
          const data = res.data.data;
          that.setData({
            hotProducts: data.products || [],
            hotProductsPage: data.pagination.page,
            hotProductsTotal: data.pagination.totalPages,
            hotProductsHasNext: data.pagination.hasNext,
            hotProductsLoading: false
          });
        } else {
          console.error('获取热门产品失败:', res.data.message);
          that.setData({ hotProductsLoading: false });
        }
      },
      fail: function(error) {
        console.error('请求失败:', error);
        that.setData({ hotProductsLoading: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 上一页
  onPrevPage: function() {
    const currentPage = this.data.hotProductsPage;
    if (currentPage > 1) {
      this.loadHotProducts(currentPage - 1);
    }
  },

  // 下一页
  onNextPage: function() {
    const currentPage = this.data.hotProductsPage;
    const hasNext = this.data.hotProductsHasNext;
    if (hasNext) {
      this.loadHotProducts(currentPage + 1);
    }
  },

  onShow: function () {
    // 检查登录状态
    this.checkLoginStatus();
    // 页面显示时重新加载数据，确保获取最新内容
    this.loadHomeData();
    // 重新加载热门产品当前页
    this.loadHotProducts(this.data.hotProductsPage);
  },

  // 跳转到产品详情页
  navigateToProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${id}`
    });
  },

  // 跳转到分类页面
  navigateToCategory: function () {
    wx.switchTab({
      url: '/pages/category/category'
    });
  },

  // 跳转到产品页面
  navigateToProducts: function () {
    wx.switchTab({
      url: '/pages/products/products'
    });
  },



  // 添加到购物车
  addToCart: async function (e) {
    const productId = e.currentTarget.dataset.id;
    
    // 防止重复点击
    if (this.data.addingToCart) {
      return;
    }
    
    // 检查用户登录状态
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.userId) {
      // 跳转到登录页面
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    this.setData({ addingToCart: true });
    
    try {
      // 调用后端API添加到购物车
      const response = await request({
        url: '/cart/add',
        method: 'POST',
        data: {
          productId: productId,
          quantity: 1
        }
      });
      
      if (response.success) {
        // 更新购物车数量
        const cartItems = app.globalData.cartItems + 1;
        app.globalData.cartItems = cartItems;
        
        // 设置购物车需要刷新的标志
        app.globalData.cartNeedsRefresh = true;
        
        wx.showToast({
          title: '已加入购物车',
          icon: 'success',
          duration: 2000
        });
      } else {
        wx.showToast({
          title: response.message || '添加失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('添加购物车失败:', error);
      wx.showToast({
        title: '网络请求失败',
        icon: 'none'
      });
    } finally {
      // 恢复按钮状态
      this.setData({ addingToCart: false });
    }
  },

  // 切换收藏状态
  toggleFavorite: function (e) {
    const productId = e.currentTarget.dataset.id;
    const that = this;
    
    // 检查用户登录状态
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.userId) {
      // 跳转到登录页面
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取当前商品的收藏状态
    const currentProduct = this.data.hotProducts.find(p => p.id === productId);
    const isFavorite = currentProduct.isFavorite;
    
    // 调用后端API切换收藏状态
    const apiUrl = isFavorite ? '/favorites/remove' : '/favorites/add';
    wx.request({
      url: getApiUrl(apiUrl),
      method: 'POST',
      data: {
        userId: userInfo.userId,
        productId: productId
      },
      success: function(res) {
        if (res.data.success) {
          // 更新本地收藏状态
          const hotProducts = that.data.hotProducts.map(product => {
            if (product.id === productId) {
              return {
                ...product,
                isFavorite: !product.isFavorite
              };
            }
            return product;
          });
          
          that.setData({
            hotProducts
          });
          
          wx.showToast({
            title: !isFavorite ? '已收藏' : '已取消收藏',
            icon: 'success',
            duration: 2000
          });
        } else {
          wx.showToast({
            title: res.data.message || '操作失败',
            icon: 'none'
          });
        }
      },
      fail: function(error) {
        console.error('收藏操作失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 预览产品
  previewProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    
    const product = this.data.hotProducts.find(p => p.id === id);
    
    // 预览产品图片
    wx.previewImage({
      current: product.image,
      urls: [product.image]
    });
  },

  // 检查登录状态
  checkLoginStatus: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    
    // 同步到全局状态
    if (userInfo) {
      app.globalData.userInfo = userInfo;
    }
  },

  // 跳转到登录页面
  navigateToLogin: function() {
    wx.navigateTo({
      url: '/pages/login/login'
    });
  },

  // 退出登录
  logout: function() {
    const that = this;
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: function(res) {
        if (res.confirm) {
          // 清除本地存储
          wx.removeStorageSync('userInfo');
          
          // 清除全局状态
          app.globalData.userInfo = null;
          app.globalData.cartItems = 0;
          
          // 更新页面状态
          that.setData({ userInfo: null });
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
        }
      }
    });
  }
})