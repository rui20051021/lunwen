import { http } from './request'
import type { Product, ProductType, PageResponse, PageParams } from '@/types/api'

export const productApi = {
  // 分页查询产品
  getProducts(params: PageParams & {
    productName?: string
    productType?: ProductType
    status?: number
  }) {
    return http.get<PageResponse<Product>>('/database/product/all', { params })
  },
  
  // 创建产品
  createProduct(data: Partial<Product>) {
    return http.post<Product>('/database/product/create', data)
  },
  
  // 更新产品
  updateProduct(id: number, data: Partial<Product>) {
    return http.put<Product>(`/database/product/${id}`, data)
  },
  
  // 删除产品
  deleteProduct(id: number) {
    return http.delete(`/database/product/${id}`)
  },
  
  // 批量删除产品
  batchDeleteProducts(ids: number[]) {
    return http.delete('/database/product/batch', { data: ids })
  },
  
  // 获取产品详情
  getProduct(id: number) {
    return http.get<Product>(`/database/product/${id}`)
  },
  
  // 根据编码获取产品
  getProductByCode(productCode: string) {
    return http.get<Product>(`/database/product/code/${productCode}`)
  },
  
  // 根据类型查询产品
  getProductsByType(productType: ProductType) {
    return http.get<Product[]>(`/database/product/type/${productType}`)
  },
  
  // 查询温控产品
  getTemperatureControlProducts() {
    return http.get<Product[]>('/database/product/temperature-control')
  },
  
  // 更新产品状态
  updateProductStatus(id: number, status: number) {
    return http.patch(`/database/product/${id}/status`, null, { params: { status } })
  },
  
  // 获取产品统计
  getProductStatistics() {
    return http.get('/database/product/statistics')
  },
  
  // 获取产品温湿度要求
  getTemperatureHumidityRequirements(id: number) {
    return http.get(`/database/product/${id}/temperature-humidity-requirements`)
  },
  
  // 检查产品编码
  checkProductCode(productCode: string) {
    return http.get<boolean>('/database/product/check-code', { params: { productCode } })
  }
}

