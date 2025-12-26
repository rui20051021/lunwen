import { http } from './request'

export interface Role {
  id: number
  roleCode: string
  roleName: string
  description: string
  demoUser: {
    username: string
    password: string
    realName: string
  }
}

export const roleApi = {
  // 获取所有角色列表
  getRoles() {
    return http.get<Role[]>('/roles')
  },
  
  // 获取角色详情
  getRole(id: number) {
    return http.get<Role>(`/roles/${id}`)
  }
}

// 演示角色数据
export const demoRoles: Role[] = [
  {
    id: 1,
    roleCode: 'admin',
    roleName: '系统管理员',
    description: '系统管理和配置权限',
    demoUser: {
      username: 'admin',
      password: 'admin123',
      realName: '系统管理员'
    }
  },
  {
    id: 2,
    roleCode: 'supplier',
    roleName: '供应商',
    description: '产品供应和订单管理',
    demoUser: {
      username: 'supplier01',
      password: 'admin123',
      realName: '张经理'
    }
  },
  {
    id: 3,
    roleCode: 'logistics',
    roleName: '物流商',
    description: '运输和车辆管理',
    demoUser: {
      username: 'logistics01',
      password: 'admin123',
      realName: '王物流'
    }
  },
  {
    id: 4,
    roleCode: 'purchaser',
    roleName: '采购商',
    description: '采购和收货管理',
    demoUser: {
      username: 'purchaser01',
      password: 'admin123',
      realName: '刘采购'
    }
  },
  {
    id: 5,
    roleCode: 'regulator',
    roleName: '监管员',
    description: '监管和合规检查',
    demoUser: {
      username: 'regulator01',
      password: 'admin123',
      realName: '监管员A'
    }
  }
]