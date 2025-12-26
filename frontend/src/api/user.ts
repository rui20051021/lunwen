import { http } from './request'
import type { PageResponse, PageParams } from '@/types/api'

export interface User {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  userType: string
  companyId: number
  status: number
  createTime: string
  updateTime?: string
  lastLoginTime?: string
}

export interface UserCreateRequest {
  username: string
  realName: string
  email: string
  phone: string
  userType: string
  companyId: number
  password?: string
}

export interface UserUpdateRequest {
  realName?: string
  email?: string
  phone?: string
  userType?: string
  companyId?: number
  status?: number
}

export const userApi = {
  // 分页查询用户
  getUsers(params: PageParams & {
    username?: string
    realName?: string
    userType?: string
    status?: number
  }) {
    return http.get<PageResponse<User>>('/users', { params })
  },
  
  // 创建用户
  createUser(data: UserCreateRequest) {
    return http.post<User>('/users', data)
  },
  
  // 更新用户
  updateUser(id: number, data: UserUpdateRequest) {
    return http.put<User>(`/users/${id}`, data)
  },
  
  // 删除用户
  deleteUser(id: number) {
    return http.delete(`/users/${id}`)
  },
  
  // 获取用户详情
  getUser(id: number) {
    return http.get<User>(`/users/${id}`)
  },
  
  // 批量删除用户
  batchDeleteUsers(ids: number[]) {
    return http.delete('/users/batch', { data: ids })
  },
  
  // 更新用户状态
  updateUserStatus(id: number, status: number) {
    return http.patch(`/users/${id}/status`, null, { params: { status } })
  },
  
  // 重置用户密码
  resetUserPassword(id: number, newPassword: string) {
    return http.post(`/users/${id}/reset-password`, { newPassword })
  },
  
  // 检查用户名是否可用
  checkUsername(username: string) {
    return http.get<boolean>('/users/check-username', { params: { username } })
  },
  
  // 根据用户类型查询用户
  getUsersByType(userType: string) {
    return http.get<User[]>(`/users/type/${userType}`)
  },
  
  // 获取用户统计
  getUserStatistics() {
    return http.get('/users/statistics')
  }
}