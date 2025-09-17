import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout/index.vue'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Auth/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Auth/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/redirect',
    component: Layout,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/Redirect/index.vue')
      }
    ]
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard/index.vue'),
        meta: {
          title: '仪表板',
          icon: 'Dashboard',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/products',
    component: Layout,
    redirect: '/products/list',
    meta: {
      title: '商品管理',
      icon: 'Goods',
      requiresAuth: true
    },
    children: [
      {
        path: 'list',
        name: 'ProductList',
        component: () => import('@/views/Products/List.vue'),
        meta: {
          title: '商品列表',
          requiresAuth: true
        }
      },
      {
        path: 'add',
        name: 'ProductAdd',
        component: () => import('@/views/Products/Add.vue'),
        meta: {
          title: '添加商品',
          requiresAuth: true,
          permissions: ['product:add']
        }
      },
      {
        path: 'edit/:id',
        name: 'ProductEdit',
        component: () => import('@/views/Products/Edit.vue'),
        meta: {
          title: '编辑商品',
          requiresAuth: true,
          permissions: ['product:edit']
        }
      },
      {
        path: 'categories',
        name: 'ProductCategories',
        component: () => import('@/views/Products/Categories.vue'),
        meta: {
          title: '商品分类',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/orders',
    component: Layout,
    redirect: '/orders/list',
    meta: {
      title: '订单管理',
      icon: 'ShoppingCart',
      requiresAuth: true  
    },
    children: [
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/Orders/List.vue'),
        meta: {
          title: '订单列表',
          requiresAuth: true 
        }
      },
      {
        path: 'detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/Orders/Detail.vue'),
        meta: {
          title: '订单详情',
          requiresAuth: true  
        }
      },
      {
        path: 'refunds',
        name: 'OrderRefunds',
        component: () => import('@/views/Orders/Refunds.vue'),
        meta: {
          title: '退款管理',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/users',
    component: Layout,
    redirect: '/users/list',
    meta: {
      title: '用户管理',
      icon: 'User',
      requiresAuth: true
    },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/Users/List.vue'),
        meta: {
          title: '用户列表',
          requiresAuth: true
        }
      },
      {
        path: 'add',
        name: 'UserAdd',
        component: () => import('@/views/Users/Form.vue'),
        meta: {
          title: '添加用户',
          requiresAuth: true
        }
      },
      {
        path: 'edit/:id',
        name: 'UserEdit',
        component: () => import('@/views/Users/Form.vue'),
        meta: {
          title: '编辑用户',
          requiresAuth: true
        }
      },
      {
        path: 'roles',
        name: 'UserRoles',
        component: () => import('@/views/Users/Roles.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true,
          permissions: ['user:role']
        }
      }
    ]
  },
  {
    path: '/marketing',
    component: Layout,
    redirect: '/marketing/coupons/list',
    meta: {
      title: '营销管理',
      icon: 'Promotion',
      requiresAuth: true
    },
    children: [
      {
        path: 'coupons/list',
        name: 'MarketingCouponsList',
        component: () => import('@/views/Marketing/Coupons/List.vue'),
        meta: {
          title: '优惠券管理',
          requiresAuth: true
        }
      },
      {
        path: 'coupons/add',
        name: 'MarketingCouponsAdd',
        component: () => import('@/views/Marketing/Coupons/Form.vue'),
        meta: {
          title: '添加优惠券',
          requiresAuth: true
        }
      },
      {
        path: 'coupons/edit/:id',
        name: 'MarketingCouponsEdit',
        component: () => import('@/views/Marketing/Coupons/Form.vue'),
        meta: {
          title: '编辑优惠券',
          requiresAuth: true
        }
      },
      {
        path: 'coupons/detail/:id',
        name: 'MarketingCouponsDetail',
        component: () => import('@/views/Marketing/Coupons/Detail.vue'),
        meta: {
          title: '优惠券详情',
          requiresAuth: true
        }
      },
      {
        path: 'activities',
        name: 'MarketingActivities',
        component: () => import('@/views/Marketing/Activities.vue'),
        meta: {
          title: '活动管理',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/content',
    component: Layout,
    redirect: '/content/banners',
    meta: {
      title: '内容管理',
      icon: 'Document',
      requiresAuth: true
    },
    children: [
      {
        path: 'banners',
        name: 'ContentBanners',
        component: () => import('@/views/Content/Banners.vue'),
        meta: {
          title: '轮播图',
          requiresAuth: true
        }
      },
      {
        path: 'articles',
        name: 'ContentArticles',
        component: () => import('@/views/Content/Articles.vue'),
        meta: {
          title: '文章管理',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/settings',
    meta: {
      title: '系统管理',
      icon: 'Setting',
      requiresAuth: true,
      roles: ['admin']
    },
    children: [
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/System/Settings.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'logs',
        name: 'SystemLogs',
        component: () => import('@/views/System/Logs.vue'),
        meta: {
          title: '操作日志',
          requiresAuth: true,
          roles: ['admin']
        }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Profile',
        component: () => import('@/views/Profile/index.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/Error/404.vue'),
    meta: {
      title: '页面不存在'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const appStore = useAppStore()
  
  // 设置页面标题
  document.title = to.meta?.title ? `${to.meta.title} - 商城管理系统` : '商城管理系统'
  
  // 设置面包屑
  const breadcrumbs = generateBreadcrumbs(to)
  appStore.setBreadcrumbs(breadcrumbs)
  
  // 检查是否需要认证
  if (to.meta?.requiresAuth) {
    if (!authStore.isLoggedIn) {
      ElMessage.error('请先登录')
      next('/login')
      return
    }
    
    // 检查角色权限
    if (to.meta?.roles && !to.meta.roles.some((role: string) => authStore.hasRole(role))) {
      ElMessage.error('没有访问权限')
      next('/404')
      return
    }
    
    // 检查具体权限
    if (to.meta?.permissions && !to.meta.permissions.some((permission: string) => authStore.hasPermission(permission))) {
      ElMessage.error('没有操作权限')
      next('/404')
      return
    }
  }
  
  // 已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && authStore.isLoggedIn) {
    next('/dashboard')
    return
  }
  
  next()
})

// 生成面包屑导航
function generateBreadcrumbs(route: any) {
  const breadcrumbs = []
  const pathArray = route.path.split('/').filter((item: string) => item)
  
  breadcrumbs.push({ title: '首页', path: '/dashboard' })
  
  let currentPath = ''
  for (const path of pathArray) {
    currentPath += `/${path}`
    const matchedRoute = router.getRoutes().find(r => r.path === currentPath)
    if (matchedRoute && matchedRoute.meta?.title) {
      breadcrumbs.push({
        title: matchedRoute.meta.title as string,
        path: currentPath
      })
    }
  }
  
  return breadcrumbs
}

export default router
