import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { 
      title: '登录',
      requiresAuth: false 
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { 
      title: '用户注册',
      requiresAuth: false 
    }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理员控制台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system',
        name: 'AdminSystem',
        component: () => import('@/views/admin/System.vue'),
        meta: { title: '系统设置' }
      }
    ]
  },
  {
    path: '/supplier',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'SupplierDashboard',
        component: () => import('@/views/supplier/Dashboard.vue'),
        meta: { title: '供应商控制台' }
      },
      {
        path: 'products',
        name: 'SupplierProducts',
        component: () => import('@/views/supplier/Products.vue'),
        meta: { title: '产品管理' }
      },
      {
        path: 'orders',
        name: 'SupplierOrders',
        component: () => import('@/views/supplier/Orders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'evaluation',
        name: 'SupplierEvaluation',
        component: () => import('@/views/supplier/Evaluation.vue'),
        meta: { title: '供应商评价' }
      }
    ]
  },
  {
    path: '/logistics',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'LogisticsDashboard',
        component: () => import('@/views/logistics/Dashboard.vue'),
        meta: { title: '物流商控制台' }
      },
      {
        path: 'vehicles',
        name: 'LogisticsVehicles',
        component: () => import('@/views/logistics/Vehicles.vue'),
        meta: { title: '车辆管理' }
      },
      {
        path: 'tracking',
        name: 'LogisticsTracking',
        component: () => import('@/views/logistics/Tracking.vue'),
        meta: { title: '运输跟踪' }
      },
      {
        path: 'temperature',
        name: 'LogisticsTemperature',
        component: () => import('@/views/logistics/Temperature.vue'),
        meta: { title: '温控监测' }
      }
    ]
  },
  {
    path: '/purchaser',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'PurchaserDashboard',
        component: () => import('@/views/purchaser/Dashboard.vue'),
        meta: { title: '采购商控制台' }
      },
      {
        path: 'orders',
        name: 'PurchaserOrders',
        component: () => import('@/views/purchaser/Orders.vue'),
        meta: { title: '采购订单' }
      },
      {
        path: 'receiving',
        name: 'PurchaserReceiving',
        component: () => import('@/views/purchaser/Receiving.vue'),
        meta: { title: '收货管理' }
      },
      {
        path: 'evaluation',
        name: 'PurchaserSupplierEvaluation',
        component: () => import('@/views/purchaser/SupplierEvaluation.vue'),
        meta: { title: '供应商评价' }
      }
    ]
  },
  {
    path: '/regulator',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'RegulatorDashboard',
        component: () => import('@/views/regulator/Dashboard.vue'),
        meta: { title: '监管员控制台' }
      },
      {
        path: 'compliance',
        name: 'RegulatorCompliance',
        component: () => import('@/views/regulator/Compliance.vue'),
        meta: { title: '合规检查' }
      },
      {
        path: 'reports',
        name: 'RegulatorReports',
        component: () => import('@/views/regulator/Reports.vue'),
        meta: { title: '监管报告' }
      },
      {
        path: 'violations',
        name: 'RegulatorViolations',
        component: () => import('@/views/regulator/Violations.vue'),
        meta: { title: '违规处理' }
      }
    ]
  },
  // 通用业务路由 - 所有角色都可以访问
  {
    path: '/products',
    name: 'Products',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: '',
        name: 'ProductsIndex',
        component: () => import('@/views/common/Products.vue'),
        meta: { title: '产品管理' }
      }
    ]
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: '',
        name: 'OrdersIndex',
        component: () => import('@/views/common/Orders.vue'),
        meta: { title: '订单管理' }
      }
    ]
  },
  {
    path: '/alerts',
    name: 'Alerts',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: '',
        name: 'AlertsIndex',
        component: () => import('@/views/common/Alerts.vue'),
        meta: { title: '预警管理' }
      }
    ]
  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: '',
        name: 'AnalysisIndex',
        component: () => import('@/views/common/Analysis.vue'),
        meta: { title: '数据分析' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router