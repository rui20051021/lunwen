// 用户相关类型定义

export interface User {
  id: number
  username: string
  realName: string
  email?: string
  phone?: string
  avatar?: string
  userType: UserType
  companyId?: number
  lastLoginTime?: string
  status: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresAt: string
  userInfo: User
  permissions: string[]
  roles: string[]
}

export enum UserType {
  SUPPLIER = 'supplier',
  LOGISTICS = 'logistics', 
  PURCHASER = 'purchaser',
  REGULATOR = 'regulator',
  ADMIN = 'admin'
}

export interface UserRole {
  id: number
  roleCode: string
  roleName: string
  description?: string
}

