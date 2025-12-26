import { http } from './request'
import type { Order, OrderStatus, PageResponse, PageParams } from '@/types/api'

export interface OrderCreateRequest {
  supplierId: number
  purchaserId: number
  orderType?: string
  pickupAddress: string
  deliveryAddress: string
  requiredDeliveryTime?: string
  orderItems: {
    productId: number
    quantity: number
    unitPrice: number
    productBatch?: string
  }[]
}

export const orderApi = {
  // 分页查询订单
  getOrders(params: PageParams & {
    orderCode?: string
    supplierId?: number
    purchaserId?: number
    orderStatus?: OrderStatus
    startTime?: string
    endTime?: string
  }) {
    return http.get<PageResponse<Order>>('/database/order/all', { params })
  },
  
  // 创建订单
  createOrder(data: OrderCreateRequest) {
    return http.post<Order>('/database/order/create', data)
  },
  
  // 获取订单详情
  getOrderDetail(id: number) {
    return http.get(`/database/order/${id}`)
  },
  
  // 根据编号获取订单
  getOrderByCode(orderCode: string) {
    return http.get<Order>(`/database/order/code/${orderCode}`)
  },
  
  // 确认订单
  confirmOrder(id: number) {
    return http.post(`/database/order/${id}/confirm`)
  },
  
  // 订单发货
  shipOrder(id: number) {
    return http.post(`/database/order/${id}/ship`)
  },
  
  // 订单签收
  deliverOrder(id: number, signaturePerson: string) {
    return http.post(`/database/order/${id}/deliver`, null, { 
      params: { signaturePerson } 
    })
  },
  
  // 完成订单
  completeOrder(id: number) {
    return http.post(`/database/order/${id}/complete`)
  },
  
  // 取消订单
  cancelOrder(id: number, reason: string) {
    return http.post(`/database/order/${id}/cancel`, null, { 
      params: { reason } 
    })
  },
  
  // 查询运输中订单
  getInTransitOrders() {
    return http.get<Order[]>('/database/order/in-transit')
  },
  
  // 查询超时订单
  getOverdueOrders() {
    return http.get<Order[]>('/database/order/overdue')
  },
  
  // 获取订单统计
  getOrderStatistics() {
    return http.get('/database/order/statistics')
  },
  
  // 获取订单状态统计
  getOrderStatusStatistics() {
    return http.get('/database/order/status-statistics')
  },
  
  // 获取订单时间分析
  getOrderTimeAnalysis(startDate: string, endDate: string) {
    return http.get('/database/order/time-analysis', { 
      params: { startDate, endDate } 
    })
  },
  
  // 按供应商统计订单金额
  getOrderAmountBySupplier() {
    return http.get('/database/order/amount-by-supplier')
  },
  
  // 检查订单权限
  checkOrderPermissions(id: number) {
    return http.get<{ canModify: boolean; canCancel: boolean }>(`/database/order/${id}/permissions`)
  }
}

