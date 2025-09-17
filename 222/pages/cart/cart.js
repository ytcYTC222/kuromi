const app = getApp()
const { request } = require('../../config/api')

Page({
  data: {
    cartItems: [],
    loading: false,
    recommendedProducts: [],
    loadingRecommendations: false,
    selectAll: false,
    totalPrice: "0",
    selectedCount: 0
  },

  onLoad: function () {
    this.loadCartData();
    this.loadRecommendations();
  },

  onShow: function () {
    // 检查是否需要刷新购物车数据
    if (app.globalData.cartNeedsRefresh) {
      this.loadCartData();
      this.loadRecommendations(); // 同时刷新推荐商品
      app.globalData.cartNeedsRefresh = false; // 重置刷新标志
    }
  },

  // 加载购物车数据
  async loadCartData() {
    try {
      this.setData({ loading: true });
      
      const response = await request({
        url: '/cart',
        method: 'GET'
      });
      
      if (response.success) {
        // 格式化购物车数据以匹配前端格式
        const cartItems = response.data.map(item => ({
          id: item.cartId,
          productId: item.productId,
          title: item.title,
          price: item.price,
          originalPrice: item.originalPrice,
          quantity: item.quantity,
          checked: item.selected,
          image: item.image,
          attributes: item.specifications,
          stockQuantity: item.stockQuantity,
          imageLoading: false,
          imageError: false
        }));
        
        this.setData({ 
          cartItems,
          loading: false 
        });
        
        this.calculateTotal();
        this.updateSelectAllStatus();
      } else {
        throw new Error(response.message || '获取购物车失败');
      }
    } catch (error) {
      console.error('加载购物车失败:', error);
      this.setData({ loading: false });
      
      // 检查是否是401未授权错误
      if (error.message && error.message.includes('401')) {
        // 用户未登录，跳转到登录页面
        wx.showModal({
          title: '提示',
          content: '请先登录后查看购物车',
          showCancel: false,
          success: () => {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        });
      } else {
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      }
    }
  },

  // 加载购物车商品（别名方法，用于外部调用）
  async loadCartItems() {
    await this.loadCartData();
  },

  // 切换商品选中状态
  async toggleItemCheck(e) {
    const id = e.currentTarget.dataset.id;
    const item = this.data.cartItems.find(item => item.id === id);
    if (item) {
      await this.updateItemSelection(id, !item.checked);
    }
  },

  // 更新商品选择状态
  async updateItemSelection(cartId, selected) {
    try {
      const response = await request({
        url: '/cart/select',
        method: 'PUT',
        data: {
          cartId,
          selected
        }
      });
      
      if (response.success) {
        const cartItems = this.data.cartItems.map(item => {
          if (item.id === cartId) {
            return {
              ...item,
              checked: selected
            };
          }
          return item;
        });
        this.setData({ cartItems });
        this.calculateTotal();
        this.updateSelectAllStatus();
      } else {
        throw new Error(response.message || '更新失败');
      }
    } catch (error) {
      console.error('更新选择状态失败:', error);
      wx.showToast({
        title: '更新失败',
        icon: 'none'
      });
    }
  },

  // 切换全选状态
  async toggleSelectAll() {
    const selectAll = !this.data.selectAll;
    await this.updateSelectAll(selectAll);
  },

  // 更新全选状态
  async updateSelectAll(selectAll) {
    try {
      const response = await request({
        url: '/cart/select-all',
        method: 'PUT',
        data: {
          selected: selectAll
        }
      });
      
      if (response.success) {
        const cartItems = this.data.cartItems.map(item => ({
          ...item,
          checked: selectAll
        }));
        
        this.setData({
          selectAll,
          cartItems
        });
        this.calculateTotal();
      } else {
        throw new Error(response.message || '更新失败');
      }
    } catch (error) {
      console.error('更新全选状态失败:', error);
      wx.showToast({
        title: '更新失败',
        icon: 'none'
      });
    }
  },

  // 增加商品数量
  async increaseQuantity(e) {
    const id = e.currentTarget.dataset.id;
    const item = this.data.cartItems.find(item => item.id === id);
    if (item) {
      const newQuantity = Math.min(item.quantity + 1, 99);
      await this.updateQuantity(id, newQuantity);
    }
  },

  // 减少商品数量
  async decreaseQuantity(e) {
    const id = e.currentTarget.dataset.id;
    const item = this.data.cartItems.find(item => item.id === id);
    if (item && item.quantity > 1) {
      const newQuantity = Math.max(item.quantity - 1, 1);
      await this.updateQuantity(id, newQuantity);
    }
  },

  // 更新商品数量
  async updateQuantity(cartId, quantity) {
    try {
      const response = await request({
        url: '/cart/update',
        method: 'PUT',
        data: {
          cartId,
          quantity
        }
      });
      
      if (response.success) {
        const cartItems = this.data.cartItems.map(item => {
          if (item.id === cartId) {
            return {
              ...item,
              quantity: quantity
            };
          }
          return item;
        });
        this.setData({ cartItems });
        this.calculateTotal();
      } else {
        throw new Error(response.message || '更新失败');
      }
    } catch (error) {
      console.error('更新数量失败:', error);
      wx.showToast({
        title: '更新失败',
        icon: 'none'
      });
    }
  },

  // 删除商品
  deleteItem(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这件商品吗？',
      success: (res) => {
        if (res.confirm) {
          this.removeFromCart(id);
        }
      }
    });
  },

  // 从购物车删除商品
  async removeFromCart(cartId) {
    try {
      const response = await request({
        url: '/cart/remove',
        method: 'DELETE',
        data: {
          cartId
        }
      });
      
      if (response.success) {
        const cartItems = this.data.cartItems.filter(item => item.id !== cartId);
        this.setData({ cartItems });
        this.calculateTotal();
        this.updateSelectAllStatus();
        
        wx.showToast({
          title: '删除成功',
          icon: 'success'
        });
      } else {
        throw new Error(response.message || '删除失败');
      }
    } catch (error) {
      console.error('删除商品失败:', error);
      wx.showToast({
        title: '删除失败',
        icon: 'none'
      });
    }
  },

  // 计算总价和选中数量
  calculateTotal: function () {
    let totalPrice = 0;
    let selectedCount = 0;
    
    this.data.cartItems.forEach(item => {
      if (item.checked) {
        // 移除价格中的逗号，并转换为数字
        const price = parseFloat(item.price.replace(/,/g, ''));
        totalPrice += price * item.quantity;
        selectedCount += item.quantity;
      }
    });
    
    // 格式化总价，添加千位分隔符
    const formattedTotalPrice = totalPrice.toLocaleString('en-US');
    
    this.setData({
      totalPrice: formattedTotalPrice,
      selectedCount
    });
  },

  // 更新全选状态
  updateSelectAllStatus: function () {
    const selectAll = this.data.cartItems.length > 0 && 
                     this.data.cartItems.every(item => item.checked);
    
    this.setData({ selectAll });
  },

  // 去结算
  checkout: function () {
    if (this.data.selectedCount === 0) {
      wx.showToast({
        title: '请选择要结算的商品',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    
    // 获取选中的商品
    const selectedItems = this.data.cartItems.filter(item => item.checked);
    
    // 转换为结算页面需要的格式
    const orderItems = selectedItems.map(item => ({
      id: item.productId,
      cartId: item.id,
      title: item.title,
      price: item.price,
      originalPrice: item.originalPrice,
      quantity: item.quantity,
      image: item.image,
      attributes: item.attributes
    }));
    
    // 跳转到结算页面
    wx.navigateTo({
      url: `/pages/checkout/checkout?cartItems=${encodeURIComponent(JSON.stringify(orderItems))}`
    });
  },

  // 跳转到商品详情页
  navigateToProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${id}`
    });
  },

  // 跳转到产品页面
  goToProducts: function () {
    wx.switchTab({
      url: '/pages/products/products'
    });
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    Promise.all([
      this.loadCartData(),
      this.loadRecommendations()
    ]).finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 图片加载成功
  onImageLoad: function(e) {
    const id = e.currentTarget.dataset.id;
    const cartItems = this.data.cartItems.map(item => {
      if (item.id === id) {
        return { ...item, imageLoading: false, imageError: false };
      }
      return item;
    });
    this.setData({ cartItems });
  },

  // 图片加载失败
  onImageError: function(e) {
    const id = e.currentTarget.dataset.id;
    const cartItems = this.data.cartItems.map(item => {
      if (item.id === id) {
        return { ...item, imageLoading: false, imageError: true };
      }
      return item;
    });
    this.setData({ cartItems });
    console.warn('商品图片加载失败:', id);
  },

  // 加载推荐商品
  async loadRecommendations() {
    try {
      this.setData({ loadingRecommendations: true });
      
      const response = await request({
        url: '/recommendations/cart',
        method: 'GET',
        data: {
          limit: 8
        }
      });
      
      if (response.success) {
        // 格式化推荐商品数据
        const recommendedProducts = response.data.map(item => ({
          id: item.id,
          title: item.title,
          price: item.price,
          originalPrice: item.originalPrice,
          rating: Math.round(item.rating * 10) / 10, // 保留一位小数
          ratingCount: item.ratingCount,
          salesCount: item.salesCount,
          image: item.image,
          categoryName: item.categoryName
        }));
        
        this.setData({ 
          recommendedProducts,
          loadingRecommendations: false 
        });
      } else {
        throw new Error(response.message || '获取推荐商品失败');
      }
    } catch (error) {
      console.error('加载推荐商品失败:', error);
      this.setData({ loadingRecommendations: false });
      
      // 如果获取推荐失败，使用默认推荐商品
      const defaultRecommendations = [
        {
          id: 4,
          title: "现代简约茶几",
          price: "1899.00",
          originalPrice: "2299.00",
          rating: 4.5,
          ratingCount: 128,
          salesCount: 256,
          image: "",
          categoryName: "茶几"
        },
        {
          id: 5,
          title: "北欧风格书桌",
          price: "2199.00",
          originalPrice: "2599.00",
          rating: 4.7,
          ratingCount: 89,
          salesCount: 145,
          image: "",
          categoryName: "书桌"
        },
        {
          id: 6,
          title: "北欧风格餐桌",
          price: "3699.00",
          originalPrice: "4299.00",
          rating: 4.8,
          ratingCount: 203,
          salesCount: 312,
          image: "",
          categoryName: "餐桌"
        },
        {
          id: 7,
          title: "简约风格沙发垫",
          price: "299.00",
          originalPrice: "399.00",
          rating: 4.3,
          ratingCount: 67,
          salesCount: 189,
          image: "",
          categoryName: "沙发配件"
        }
      ];
      
      this.setData({ 
        recommendedProducts: defaultRecommendations
      });
    }
  },

  // 刷新推荐商品
  refreshRecommendations: function() {
    this.loadRecommendations();
    wx.showToast({
      title: '正在刷新推荐',
      icon: 'loading',
      duration: 1000
    });
  },

  // 从推荐商品加入购物车
  async addToCartFromRecommendation(e) {
    const productId = e.currentTarget.dataset.id;
    
    try {
      wx.showLoading({
        title: '加入购物车中...'
      });
      
      const response = await request({
        url: '/cart/add',
        method: 'POST',
        data: {
          productId: productId,
          quantity: 1
        }
      });
      
      wx.hideLoading();
      
      if (response.success) {
        wx.showToast({
          title: '已加入购物车',
          icon: 'success',
          duration: 1500
        });
        
        // 刷新购物车数据
        this.loadCartData();
        
        // 更新全局购物车状态
        app.globalData.cartNeedsRefresh = true;
      } else {
        throw new Error(response.message || '加入购物车失败');
      }
    } catch (error) {
      wx.hideLoading();
      console.error('加入购物车失败:', error);
      wx.showToast({
        title: error.message || '加入购物车失败',
        icon: 'none',
        duration: 2000
      });
    }
  }
})