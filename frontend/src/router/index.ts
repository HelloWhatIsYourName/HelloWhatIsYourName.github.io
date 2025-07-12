import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: {
      title: '登录',
      hidden: true,
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: {
      title: '注册',
      hidden: true,
    },
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'Monitor',
        },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          icon: 'User',
          hidden: true,
        },
      },
    ],
  },
  {
    path: '/user',
    component: () => import('@/layout/index.vue'),
    redirect: '/user/list',
    meta: {
      title: '用户管理',
      icon: 'UserFilled',
      roles: ['ADMIN'],
    },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: {
          title: '用户列表',
          icon: 'User',
        },
      },
      {
        path: 'roles',
        name: 'UserRoles',
        component: () => import('@/views/user/Roles.vue'),
        meta: {
          title: '角色管理',
          icon: 'Key',
        },
      },
    ],
  },
  {
    path: '/device',
    component: () => import('@/layout/index.vue'),
    redirect: '/device/list',
    meta: {
      title: '设备管理',
      icon: 'Monitor',
    },
    children: [
      {
        path: 'list',
        name: 'DeviceList',
        component: () => import('@/views/device/List.vue'),
        meta: {
          title: '设备列表',
          icon: 'Monitor',
        },
      },
      {
        path: 'my-devices',
        name: 'MyDevices',
        component: () => import('@/views/device/MyDevices.vue'),
        meta: {
          title: '我的设备',
          icon: 'Connection',
        },
      },
    ],
  },
  {
    path: '/data',
    component: () => import('@/layout/index.vue'),
    redirect: '/data/sensor',
    meta: {
      title: '数据管理',
      icon: 'DataLine',
    },
    children: [
      {
        path: 'sensor',
        name: 'SensorData',
        component: () => import('@/views/data/SensorData.vue'),
        meta: {
          title: '传感器数据',
          icon: 'TrendCharts',
        },
      },
      {
        path: 'gesture',
        name: 'GestureData',
        component: () => import('@/views/data/GestureData.vue'),
        meta: {
          title: '手势识别',
          icon: 'Pointer',
        },
      },
      {
        path: 'learning',
        name: 'LearningData',
        component: () => import('@/views/data/LearningData.vue'),
        meta: {
          title: '学习记录',
          icon: 'Reading',
        },
      },
    ],
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hidden: true,
    },
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 白名单路由
  const whiteList = ['/login', '/register', '/404']
  
  if (userStore.isLoggedIn) {
    if (to.path === '/login' || to.path === '/register') {
      // 已登录用户访问登录/注册页面，重定向到首页
      next('/')
    } else {
      // 检查是否有用户信息
      if (!userStore.userInfo) {
        try {
          await userStore.getUserInfo()
        } catch (error) {
          // 获取用户信息失败，清除token并重定向到登录页
          userStore.logout()
          next('/login')
          return
        }
      }
      
      // 检查权限
      if (to.meta?.roles && Array.isArray(to.meta.roles)) {
        const hasRole = to.meta.roles.some(role => userStore.hasRole(role))
        if (hasRole) {
          next()
        } else {
          ElMessage.error('没有权限访问此页面')
          next('/404')
        }
      } else {
        next()
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router