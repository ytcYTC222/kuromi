// pages/category-products/category-products.js
const { request } = require('../../config/api');

Page({
  data: {
    categoryId: null,
    categoryName: '',
    categoryDescription: '',
    sortingOptions: ['推荐', '价格低到高', '价格高到低', '评分', '最新'],
    sortingIndex: 0,
    priceRangeOptions: ['全部价格', '¥0-¥1999', '¥2000-¥4999', '¥5000-¥9999', '¥10000以上'],
    priceRangeIndex: 0,
    activeTags: [],
    currentPage: 1,
    totalPages: 1,
    pageSize: 10,
    products: [],
    loading: true,
    error: null,
    addingToCart: false
  },

  onLoad: function (options) {
    // 获取分类ID参数
    if (options.categoryId) {
      this.setData({
        categoryId: options.categoryId
      });
      
      // 加载分类信息和商品数据
      this.loadCategoryInfo();
      this.loadProducts();
    } else {
      this.setData({
        loading: false,
        error: '缺少分类参数'
      });
    }
  },

  // 加载分类信息
  loadCategoryInfo: async function() {
    try {
      const res = await request({
        url: `/categories/${parseInt(this.data.categoryId)}`
      });
      
      if (res.success) {
        this.setData({
          categoryName: res.data.name,
          categoryDescription: res.data.description
        });
        
        // 设置页面标题
        wx.setNavigationBarTitle({
          title: res.data.name
        });
      }
    } catch (error) {
      console.error('加载分类信息失败:', error);
    }
  },

  // 加载商品数据
  loadProducts: async function() {
    try {
      this.setData({ loading: true, error: null });
      
      const params = {
        category: parseInt(this.data.categoryId),
        page: this.data.currentPage,
        limit: this.data.pageSize,
        sort: this.getSortBy()
      };
      
      // 添加价格区间参数
      const priceRange = this.getPriceRange();
      if (priceRange) {
        if (priceRange.min !== undefined && priceRange.min !== null) params.minPrice = priceRange.min;
        if (priceRange.max !== undefined && priceRange.max !== null) params.maxPrice = priceRange.max;
      }
      
      const res = await request({
        url: '/products',
        data: params
      });
      
      if (res.success) {
        this.setData({
          products: res.data.products || [],
          totalPages: res.data.pagination ? res.data.pagination.totalPages : 1,
          loading: false
        });
      } else {
        throw new Error(res.message || '获取商品数据失败');
      }
    } catch (error) {
      console.error('加载商品失败:', error);
      this.setData({
        loading: false,
        error: '加载失败，请重试'
      });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 获取排序参数
  getSortBy: function() {
    const sortMap = {
      0: 'default',
      1: 'price_asc',
      2: 'price_desc',
      3: 'rating',
      4: 'newest'
    };
    return sortMap[this.data.sortingIndex] || 'default';
  },

  // 获取价格区间参数
  getPriceRange: function() {
    const priceMap = {
      0: null,
      1: { min: 0, max: 1999 },
      2: { min: 2000, max: 4999 },
      3: { min: 5000, max: 9999 },
      4: { min: 10000, max: null }
    };
    return priceMap[this.data.priceRangeIndex];
  },

  // 改变排序方式
  changeSorting: function (e) {
    const index = parseInt(e.detail.value);
    this.setData({
      sortingIndex: index,
      currentPage: 1
    });
    
    this.updateFilterTags('sorting', `排序: ${this.data.sortingOptions[index]}`);
    this.loadProducts();
  },

  // 改变价格区间
  changePriceRange: function (e) {
    const index = parseInt(e.detail.value);
    this.setData({
      priceRangeIndex: index,
      currentPage: 1
    });
    
    if (index > 0) {
      this.updateFilterTags('priceRange', `价格: ${this.data.priceRangeOptions[index]}`);
    } else {
      this.removeFilterTag('priceRange');
    }
    this.loadProducts();
  },

  // 更新筛选标签
  updateFilterTags: function (id, name) {
    const activeTags = this.data.activeTags.filter(tag => tag.id !== id);
    activeTags.push({ id, name });
    this.setData({ activeTags });
  },

  // 移除筛选标签
  removeFilterTag: function (id) {
    const activeTags = this.data.activeTags.filter(tag => tag.id !== id);
    this.setData({ activeTags });
  },

  // 移除标签
  removeTag: function (e) {
    const id = e.currentTarget.dataset.id;
    
    if (id === 'sorting') {
      this.setData({ sortingIndex: 0 });
    } else if (id === 'priceRange') {
      this.setData({ priceRangeIndex: 0 });
    }
    
    this.removeFilterTag(id);
    this.setData({ currentPage: 1 });
    this.loadProducts();
  },

  // 上一页
  prevPage: function () {
    if (this.data.currentPage > 1) {
      this.setData({
        currentPage: this.data.currentPage - 1
      });
      this.loadProducts();
    }
  },

  // 下一页
  nextPage: function () {
    if (this.data.currentPage < this.data.totalPages) {
      this.setData({
        currentPage: this.data.currentPage + 1
      });
      this.loadProducts();
    }
  },

  // 导航到商品详情
  navigateToProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${id}`
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
        const app = getApp();
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
    const id = e.currentTarget.dataset.id;
    const products = this.data.products.map(product => {
      if (product.id === id) {
        return {
          ...product,
          isFavorite: !product.isFavorite
        };
      }
      return product;
    });
    
    this.setData({ products });
    
    const product = products.find(p => p.id === id);
    wx.showToast({
      title: product.isFavorite ? '已收藏' : '已取消收藏',
      icon: 'success'
    });
  },

  // 预览商品
  previewProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    const product = this.data.products.find(p => p.id === id);
    
    if (product && product.image) {
      wx.previewImage({
        urls: [product.image]
      });
    }
  },

  // 返回分类页面
  goBack: function() {
    wx.navigateBack();
  },

  // 重试加载
  retryLoad: function() {
    this.loadProducts();
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    this.setData({ currentPage: 1 });
    this.loadProducts().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 页面分享
  onShareAppMessage: function() {
    return {
      title: `${this.data.categoryName} - 精选家具`,
      path: `/pages/category-products/category-products?categoryId=${this.data.categoryId}`
    };
  }
});