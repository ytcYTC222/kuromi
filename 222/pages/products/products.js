const app = getApp()
const { request } = require('../../config/api')

Page({
  data: {
    sortingOptions: ['推荐', '价格低到高', '价格高到低', '评分', '最新'],
    sortingIndex: 0,
    priceRangeOptions: ['全部价格', '¥0-¥1999', '¥2000-¥4999', '¥5000-¥9999', '¥10000以上'],
    priceRangeIndex: 0,

    activeTags: [],
    currentPage: 1,
    totalPages: 1,
    products: [],
    loading: false,
    hasMore: true
  },

  onLoad: function (options) {
    // 初始化页面数据
    this.initPage(options);
  },

  // 初始化页面
  async initPage(options) {
    try {
      // 加载产品数据
      await this.loadProducts();
      
    } catch (error) {
      console.error('页面初始化失败:', error);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },



  // 加载产品数据
  async loadProducts(reset = false) {
    if (this.data.loading) return;
    
    try {
      this.setData({ loading: true });
      
      const page = this.data.currentPage;
      const params = this.buildRequestParams(page);
      
      const response = await request({
        url: '/products',
        method: 'GET',
        data: params
      });
      
      if (response.success && response.data) {
        const { products, pagination } = response.data;
        
        this.setData({
          products: products,
          currentPage: pagination.page,
          totalPages: pagination.totalPages,
          hasMore: pagination.page < pagination.totalPages,
          loading: false
        });
      }
    } catch (error) {
      console.error('加载产品失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 构建请求参数
  buildRequestParams(page = 1) {
    const params = {
      page,
      limit: 10
    };
    

    // 价格区间筛选
    const priceRange = this.getPriceRange();
    if (priceRange) {
      if (priceRange.min !== null) {
        params.minPrice = priceRange.min;
      }
      if (priceRange.max !== null) {
        params.maxPrice = priceRange.max;
      }
    }
    
    // 排序方式
    const sortMap = {
      0: 'default',     // 推荐
      1: 'price_asc',   // 价格低到高
      2: 'price_desc',  // 价格高到低
      3: 'rating',      // 评分
      4: 'newest'       // 最新
    };
    params.sort = sortMap[this.data.sortingIndex] || 'default';
    
    return params;
  },

  // 获取价格区间
  getPriceRange() {
    const priceRangeIndex = this.data.priceRangeIndex;
    const priceRanges = [
      null,                    // 全部价格
      { min: 0, max: 1999 },   // ¥0-¥1999
      { min: 2000, max: 4999 }, // ¥2000-¥4999
      { min: 5000, max: 9999 }, // ¥5000-¥9999
      { min: 10000, max: null } // ¥10000以上
    ];
    
    return priceRanges[priceRangeIndex];
  },

  // 切换排序方式
  changeSorting: function (e) {
    const sortingIndex = e.detail.value;
    this.setData({ 
      sortingIndex,
      currentPage: 1
    });
    
    // 更新筛选标签
    this.updateFilterTags('sorting', `排序: ${this.data.sortingOptions[sortingIndex]}`);
    
    // 刷新产品列表
    this.loadProducts(true);
  },

  // 切换价格区间
  changePriceRange: function (e) {
    const priceRangeIndex = e.detail.value;
    this.setData({ 
      priceRangeIndex,
      currentPage: 1
    });
    
    // 更新筛选标签
    if (priceRangeIndex > 0) {
      this.updateFilterTags('price', `价格: ${this.data.priceRangeOptions[priceRangeIndex]}`);
    } else {
      this.removeFilterTag('price');
    }
    
    // 刷新产品列表
    this.loadProducts(true);
  },

  // 切换分类


  // 更新筛选标签
  updateFilterTags: function (id, name) {
    const activeTags = [...this.data.activeTags];
    const existingTagIndex = activeTags.findIndex(tag => tag.id === id);
    
    if (existingTagIndex !== -1) {
      activeTags[existingTagIndex] = { id, name };
    } else {
      activeTags.push({ id, name });
    }
    
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
    
    // 根据标签类型重置对应的筛选器
    if (id === 'sorting') {
      this.setData({ 
        sortingIndex: 0,
        currentPage: 1
      });
    } else if (id === 'price') {
      this.setData({ 
        priceRangeIndex: 0,
        currentPage: 1
      });
    } else if (id === 'category') {
      this.setData({ 
        categoryIndex: 0,
        currentPage: 1
      });
    }
    
    this.removeFilterTag(id);
    
    // 刷新产品列表
    this.loadProducts(true);
  },

  // 上一页
  prevPage: function () {
    if (this.data.currentPage > 1 && !this.data.loading) {
      this.setData({
        currentPage: this.data.currentPage - 1
      });
      this.loadProducts(true);
    }
  },

  // 下一页
  nextPage: function () {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        currentPage: this.data.currentPage + 1
      });
      this.loadProducts(true);
    }
  },

  // 跳转到产品详情页
  navigateToProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${id}`
    });
  },

  // 添加到购物车
  async addToCart(e) {
    const id = e.currentTarget.dataset.id;
    
    try {
      const response = await request({
        url: '/cart/add',
        method: 'POST',
        data: {
          productId: id,
          quantity: 1
        }
      });
      
      if (response.success) {
        // 更新购物车数量
        const cartItems = app.globalData.cartItems + 1;
        app.globalData.cartItems = cartItems;
        
        // 设置购物车需要刷新的标志
        app.globalData.cartNeedsRefresh = true;
        
        // 显示添加成功提示
        wx.showToast({
          title: '已加入购物车',
          icon: 'success',
          duration: 2000
        });
      } else {
        wx.showToast({
          title: response.message || '添加失败，请重新登录',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('添加到购物车失败:', error);
      wx.showToast({
        title: '添加失败，请登录账号',
        icon: 'none'
      });
    }
  },

  // 切换收藏状态
  async toggleFavorite(e) {
    const id = e.currentTarget.dataset.id;
    
    const product = this.data.products.find(p => p.id === id);
    if (!product) return;
    
    try {
      const response = await request({
        url: product.isFavorite ? '/favorites/remove' : '/favorites/add',
        method: 'POST',
        data: {
          productId: id
        }
      });
      
      if (response.success) {
        // 更新收藏状态
        const products = this.data.products.map(p => {
          if (p.id === id) {
            return {
              ...p,
              isFavorite: !p.isFavorite
            };
          }
          return p;
        });
        
        this.setData({ products });
        
        // 显示收藏状态提示
        wx.showToast({
          title: product.isFavorite ? '已取消收藏' : '已收藏',
          icon: 'success',
          duration: 2000
        });
      } else {
        wx.showToast({
          title: response.message || '操作失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('收藏操作失败:', error);
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      });
    }
  },

  // 预览产品
  previewProduct: function (e) {
    const id = e.currentTarget.dataset.id;
    
    const product = this.data.products.find(p => p.id === id);
    
    // 预览产品图片
    wx.previewImage({
      current: product.image,
      urls: [product.image]
    });
  },

  // 下拉刷新
  onPullDownRefresh: function () {
    // 重置页面数据
    this.setData({
      currentPage: 1,
      products: [],
      hasMore: true
    });
    
    // 重新加载数据
    this.loadProducts(true).finally(() => {
      // 停止下拉刷新动画
      wx.stopPullDownRefresh();
    });
  }
})