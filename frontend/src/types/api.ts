// API相关类型定义

export interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
  timestamp?: number
}

export interface PageParams {
  current: number
  size: number
}

export interface PageResponse<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
  hasPrevious: boolean
  hasNext: boolean
}

export interface Product {
  id: number
  productCode: string
  productName: string
  productType: ProductType
  unit: string
  minTemp?: number
  maxTemp?: number
  minHumidity?: number
  maxHumidity?: number
  shelfLife?: number
  status: number
  createdAt?: string
}

export enum ProductType {
  FRUIT = 'fruit',
  VEGETABLE = 'vegetable',
  MEAT = 'meat',
  SEAFOOD = 'seafood',
  DAIRY = 'dairy',
  OTHER = 'other'
}

export interface Order {
  id: number
  orderCode: string
  supplierName: string
  purchaserName: string
  orderStatus: OrderStatus
  totalAmount: number
  pickupAddress?: string
  deliveryAddress?: string
  createdAt: string
}

export enum OrderStatus {
  CREATED = 'created',
  CONFIRMED = 'confirmed',
  IN_TRANSIT = 'in_transit',
  DELIVERED = 'delivered',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
  EXCEPTION = 'exception'
}

export interface AlertRule {
  id: number
  ruleCode: string
  ruleName: string
  ruleType: AlertType
  alertLevel: AlertLevel
  thresholdValue: number
  isEnabled: number
  createdAt?: string
}

export enum AlertType {
  TIMEOUT = 'timeout',
  TEMPERATURE = 'temperature',
  HUMIDITY = 'humidity',
  ROUTE_DEVIATION = 'route_deviation'
}

export enum AlertLevel {
  INFO = 'info',
  WARNING = 'warning',
  ERROR = 'error',
  CRITICAL = 'critical'
}

