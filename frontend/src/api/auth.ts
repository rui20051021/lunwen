import { http } from './request'
import type { LoginRequest, LoginResponse, User } from '@/types/user'
import type { ApiResponse } from '@/types/api'

export const authApi = {
  // 用户登录
  login(data: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    return http.post('/auth/login', data)
  },
  
  // 获取当前用户信息
  getCurrentUser(): Promise<ApiResponse<User>> {
    return http.get('/auth/me')
  },
  
  // 用户注册
  register(data: any): Promise<ApiResponse<User>> {
    return http.post('/auth/register', data)
  },
  
  // 用户登出
  logout(): Promise<ApiResponse<void>> {
    return http.post('/auth/logout')
  },
  
  // 刷新Token
  refreshToken(): Promise<ApiResponse<string>> {
    return http.post('/auth/refresh')
  },
  
  // 修改密码
  changePassword(oldPassword: string, newPassword: string): Promise<ApiResponse<void>> {
    return http.post('/auth/change-password', {
      oldPassword,
      newPassword
    })
  },
  
  // 检查用户名是否可用
  checkUsername(username: string): Promise<ApiResponse<boolean>> {
    return http.get('/auth/check-username', { params: { username } })
  }
}

