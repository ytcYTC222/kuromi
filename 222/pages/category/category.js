// pages/category/category.js
const { request } = require('../../config/api');

Page({
  data: {
    currentTab: 'all',
    categoryTabs: [],
    categories: {},
    popularCategories: [],
    currentCategories: [],
    loading: true,
    error: null
  },

  onLoad: function (options) {
    // 如果有传入的分类参数，切换到对应的标签
    if (options.category) {
      this.setData({
        currentTab: options.category
      });
    }
    
    // 加载分类数据
    this.loadCategoryData();
  },

  // 加载分类数据
  loadCategoryData: async function() {
    try {
      this.setData({ loading: true, error: null });
      
      // 并行获取一级分类和热门分类
      const [categoriesRes, hotCategoriesRes] = await Promise.all([
        this.getCategories(),
        this.getHotCategories()
      ]);
      
      if (categoriesRes.success && hotCategoriesRes.success) {
        const categories = categoriesRes.data;
        const hotCategories = hotCategoriesRes.data;
        
        // 构建分类标签
        const categoryTabs = [
          { id: 'all', name: '全部' },
          ...categories.map(cat => ({
            id: cat.code,
            name: cat.name
          }))
        ];
        
        // 构建分类数据结构
        const categoriesData = { all: [] };
        
        // 获取所有分类的子分类
        for (const category of categories) {
          const subCategoriesRes = await this.getSubCategories(category.id);
          if (subCategoriesRes.success) {
            categoriesData[category.code] = subCategoriesRes.data.map(sub => ({
              id: sub.id,
              name: sub.name,
              count: sub.productCount,
              image: sub.image
            }));
            
            // 添加到全部分类中
            categoriesData.all.push(...categoriesData[category.code]);
          }
        }
        
        this.setData({
          categoryTabs,
          categories: categoriesData,
          popularCategories: hotCategories.map(cat => ({
            id: cat.id,
            name: cat.name,
            count: cat.productCount,
            image: cat.image
          })),
          currentCategories: categoriesData[this.data.currentTab] || categoriesData.all,
          loading: false
        });
      } else {
        throw new Error('获取分类数据失败');
      }
    } catch (error) {
      console.error('加载分类数据失败:', error);
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

  // 获取一级分类
  getCategories: function() {
    return request({
      url: '/categories',
      data: {
        level: 1,
        includeProducts: true
      }
    });
  },

  // 获取子分类
  getSubCategories: function(parentId) {
    return request({
      url: '/categories',
      data: {
        parentId: parentId,
        includeProducts: true
      }
    });
  },

  // 获取热门分类
  getHotCategories: function() {
    return request({
      url: '/categories/hot/list',
      data: {
        limit: 4
      }
    });
  },

  // 切换标签
  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      currentTab: tab,
      currentCategories: this.data.categories[tab] || []
    });
  },

  // 导航到子分类页面
  navigateToSubcategory: function (e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/category-products/category-products?categoryId=${id}`
    });
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    this.loadCategoryData().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 重试加载
  retryLoad: function() {
    this.loadCategoryData();
  }
})