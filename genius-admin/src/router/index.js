/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2022-08-18 18:00:22
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 10:51:00
 */
import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)
/* Layout */
import Layout from '@/layout'

export const constantRoutes = [{
    path: '/',
    redirect: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/dashboard',
    component: Layout,
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: {
        title: '仪表盘',
        icon: 'dashboard'
      }
    }]
  },
  {
    path: '/userInfoList',
    component: Layout,
    redirect: '/userInfoList',
    children: [{
      path: 'userInfoList',
      name: 'userInfoList',
      component: () => import('@/views/userInfoList/index'),
      meta: {
        title: '用户信息',
        icon: 'user'
      }
    }]
  },


  {
    path: '/accountList',
    component: Layout,
    redirect: '/accountList',
    children: [{
      path: 'accountList',
      name: 'accountList',
      component: () => import('@/views/accountList/index'),
      meta: {
        title: '账户信息',
        icon: 'eye'
      }
    }]
  },

  {
    path: '/cashbackList',
    component: Layout,
    redirect: '/cashbackList',
    children: [{
      path: 'cashbackList',
      name: 'cashbackList',
      component: () => import('@/views/cashbackList/index'),
      meta: {
        title: '返佣信息',
        icon: 'eye'
      }
    }]
  },

  {
    path: '/vipList',
    component: Layout,
    redirect: '/vipList',
    children: [{
      path: 'vipList',
      name: 'vipList',
      component: () => import('@/views/vipList/index'),
      meta: {
        title: '会员卡配置',
        icon: 'example'
      }
    }]
  },

  {
    path: '/vipStrategyList',
    component: Layout,
    redirect: '/vipStrategyList',
    children: [{
      path: 'vipStrategyList',
      name: 'vipStrategyList',
      component: () => import('@/views/vipStrategyList/index'),
      meta: {
        title: '会员权益配置',
        icon: 'form'
      }
    }]
  },
  {
    path: '/currencyList',
    component: Layout,
    redirect: '/currencyList',
    children: [{
      path: 'currencyList',
      name: 'currencyList',
      component: () => import('@/views/currencyList/index'),
      meta: {
        title: '流量包配置',
        icon: 'table'
      }
    }]
  },
  {
    path: '/productConsumeConfig',
    component: Layout,
    redirect: '/productConsumeConfig',
    children: [{
      path: 'productConsumeConfig',
      name: 'productConsumeConfig',
      component: () => import('@/views/productConsumeConfig/index'),
      meta: {
        title: '代币消耗配置',
        icon: 'table'
      }
    }]
  },

  {
    path: '/paymentOrderList',
    component: Layout,
    redirect: '/paymentOrderList',
    children: [{
      path: 'paymentOrderList',
      name: 'paymentOrderList',
      component: () => import('@/views/paymentOrderList/index'),
      meta: {
        title: '支付订单列表',
        icon: 'nested'
      }
    }]
  },

  // {
  //   path: '/questionLogList',
  //   component: Layout,
  //   redirect: '/questionLogList',
  //   children: [{
  //     path: 'questionLogList',
  //     name: 'questionLogList',
  //     component: () => import('@/views/questionLogList/index'),
  //     meta: {
  //       title: '提问日志列表',
  //       icon: 'table'
  //     }
  //   }]
  // },
  // {
  //   path: '/mjManagerList',
  //   component: Layout,
  //   redirect: '/mjManagerList',
  //   children: [{
  //     path: 'mjManagerList',
  //     name: 'mjManagerList',
  //     component: () => import('@/views/mjManagerList/index'),
  //     meta: {
  //       title: 'mj管理列表',
  //       icon: 'nested'
  //     }
  //   }]
  // },


  {
    path: '/redemptionCode',
    component: Layout,
    redirect: '/redemptionCode',
    children: [{
      path: 'redemptionCode',
      name: 'redemptionCode',
      component: () => import('@/views/redemptionCode/index'),
      meta: {
        title: '兑换码管理',
        icon: 'eye'
      }
    }]
  },


  {
    path: '/bannerConfig',
    component: Layout,
    redirect: '/bannerConfig',
    children: [{
      path: 'bannerConfig',
      name: 'bannerConfig',
      component: () => import('@/views/bannerConfig/index'),
      meta: {
        title: '广告管理',
        icon: 'nested'
      }
    }]
  },


  {
    path: '/navBarConfig',
    component: Layout,
    redirect: '/navBarConfig',
    children: [{
      path: 'navBarConfig',
      name: 'navBarConfig',
      component: () => import('@/views/navBarConfig/index'),
      meta: {
        title: '导航栏配置',
        icon: 'eye'
      }
    }]
  },

  {
    path: '/aiRole',
    component: Layout,
    redirect: '/aiRole',
    children: [{
      path: 'aiRole',
      name: 'aiRole',
      component: () => import('@/views/aiRole/index'),
      meta: {
        title: '角色配置',
        icon: 'nested'
      }
    }]
  },

  {
    path: '/chatgptConfig',
    component: Layout,
    redirect: '/chatgptConfig',
    children: [{
      path: 'chatgptConfig',
      name: 'chatgptConfig',
      component: () => import('@/views/chatgptConfig/index'),
      meta: {
        title: '大模型设置',
        icon: 'eye-open'
      }
    }]
  },

  {
    path: '/ststemConfig',
    component: Layout,
    redirect: '/ststemConfig',
    children: [{
      path: 'ststemConfig',
      name: 'ststemConfig',
      component: () => import('@/views/ststemConfig/index'),
      meta: {
        title: '系统配置',
        icon: 'tree'
      }
    }]
  },

  {
    path: '/openPlatformConfig',
    component: Layout,
    redirect: '/openPlatformConfig',
    children: [{
      path: 'openPlatformConfig',
      name: 'openPlatformConfig',
      component: () => import('@/views/openPlatformConfig/index'),
      meta: {
        title: '开放平台配置',
        icon: 'tree'
      }
    }]
  },
  {
    path: '/sysLog',
    component: Layout,
    redirect: '/sysLog',
    children: [{
      path: 'sysLog',
      name: 'sysLog',
      component: () => import('@/views/sysLog/index'),
      meta: {
        title: '日志',
        icon: 'nested'
      }
    }]
  },

  // 404 page must be placed at the end !!!
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({
    y: 0
  }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
