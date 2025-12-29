/**
 * WebSocket客户端 - 实时接收预警消息
 * 
 * 功能：
 * 1. 建立WebSocket连接
 * 2. 接收实时预警消息
 * 3. 自动重连机制
 * 4. 心跳保活
 */

import { ElNotification } from 'element-plus'

export interface AlertMessage {
  alertId: number
  alertCode: string
  alertType: string
  alertLevel: string
  alertTitle: string
  alertMessage: string
  relatedId: number
  relatedType: string
  alertData: any
}

export interface WebSocketMessage {
  type: string
  message: string
  data: any
  timestamp: number
}

class WebSocketClient {
  private ws: WebSocket | null = null
  private userId: string | null = null
  private reconnectInterval: number = 5000
  private heartbeatInterval: number = 30000
  private reconnectTimer: number | null = null
  private heartbeatTimer: number | null = null
  private isManualClose: boolean = false
  private messageHandlers: Map<string, Function[]> = new Map()
  
  /**
   * 连接WebSocket服务器
   */
  connect(userId: string) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      console.log('WebSocket已连接，无需重复连接')
      return
    }
    
    this.userId = userId
    this.isManualClose = false
    
    const wsUrl = `ws://localhost:8080/ws/alerts?userId=${userId}`
    console.log('正在连接WebSocket:', wsUrl)
    
    try {
      this.ws = new WebSocket(wsUrl)
      
      this.ws.onopen = this.onOpen.bind(this)
      this.ws.onmessage = this.onMessage.bind(this)
      this.ws.onerror = this.onError.bind(this)
      this.ws.onclose = this.onClose.bind(this)
      
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.scheduleReconnect()
    }
  }
  
  /**
   * 连接成功
   */
  private onOpen(event: Event) {
    console.log('WebSocket连接成功')
    
    // 清除重连定时器
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    
    // 启动心跳
    this.startHeartbeat()
    
    // 触发连接成功事件
    this.emit('connected', { userId: this.userId })
  }
  
  /**
   * 接收消息
   */
  private onMessage(event: MessageEvent) {
    try {
      const message: WebSocketMessage = JSON.parse(event.data)
      console.log('收到WebSocket消息:', message)
      
      // 根据消息类型处理
      switch (message.type) {
        case 'alert':
          this.handleAlertMessage(message)
          break
        case 'system':
          this.handleSystemMessage(message)
          break
        case 'notification':
          this.handleNotificationMessage(message)
          break
        default:
          console.log('未知消息类型:', message.type)
      }
      
      // 触发消息事件
      this.emit('message', message)
      
    } catch (error) {
      console.error('解析WebSocket消息失败:', error)
    }
  }
  
  /**
   * 连接错误
   */
  private onError(event: Event) {
    console.error('WebSocket连接错误:', event)
    this.emit('error', event)
  }
  
  /**
   * 连接关闭
   */
  private onClose(event: CloseEvent) {
    console.log('WebSocket连接关闭:', event.code, event.reason)
    
    // 停止心跳
    this.stopHeartbeat()
    
    // 触发关闭事件
    this.emit('closed', { code: event.code, reason: event.reason })
    
    // 如果不是手动关闭，则自动重连
    if (!this.isManualClose) {
      this.scheduleReconnect()
    }
  }
  
  /**
   * 处理预警消息
   */
  private handleAlertMessage(message: WebSocketMessage) {
    const alert: AlertMessage = message.data
    
    // 根据预警级别显示不同类型的通知
    let notificationType: 'success' | 'warning' | 'error' | 'info' = 'warning'
    
    switch (alert.alertLevel) {
      case 'critical':
        notificationType = 'error'
        break
      case 'high':
        notificationType = 'error'
        break
      case 'medium':
        notificationType = 'warning'
        break
      case 'low':
        notificationType = 'info'
        break
    }
    
    // 显示通知
    ElNotification({
      title: alert.alertTitle,
      message: alert.alertMessage,
      type: notificationType,
      duration: 0, // 不自动关闭
      position: 'top-right',
      showClose: true,
      onClick: () => {
        // 点击通知跳转到预警详情页面
        this.emit('alert-click', alert)
      }
    })
    
    // 播放提示音（可选）
    this.playAlertSound(alert.alertLevel)
    
    // 触发预警事件
    this.emit('alert', alert)
  }
  
  /**
   * 处理系统消息
   */
  private handleSystemMessage(message: WebSocketMessage) {
    console.log('系统消息:', message.message)
    
    if (message.message === 'pong') {
      // 心跳响应
      return
    }
    
    // 触发系统消息事件
    this.emit('system', message)
  }
  
  /**
   * 处理通知消息
   */
  private handleNotificationMessage(message: WebSocketMessage) {
    ElNotification({
      title: '通知',
      message: message.message,
      type: 'info',
      duration: 5000,
      position: 'top-right'
    })
    
    // 触发通知事件
    this.emit('notification', message)
  }
  
  /**
   * 发送消息
   */
  send(message: any) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const data = typeof message === 'string' ? message : JSON.stringify(message)
      this.ws.send(data)
    } else {
      console.warn('WebSocket未连接，无法发送消息')
    }
  }
  
  /**
   * 关闭连接
   */
  close() {
    this.isManualClose = true
    
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    
    this.stopHeartbeat()
    
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }
  
  /**
   * 启动心跳
   */
  private startHeartbeat() {
    this.stopHeartbeat()
    
    this.heartbeatTimer = window.setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.send('ping')
      }
    }, this.heartbeatInterval)
  }
  
  /**
   * 停止心跳
   */
  private stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }
  
  /**
   * 安排重连
   */
  private scheduleReconnect() {
    if (this.reconnectTimer) {
      return
    }
    
    console.log(`${this.reconnectInterval / 1000}秒后尝试重连...`)
    
    this.reconnectTimer = window.setTimeout(() => {
      this.reconnectTimer = null
      if (this.userId) {
        this.connect(this.userId)
      }
    }, this.reconnectInterval)
  }
  
  /**
   * 播放预警提示音
   */
  private playAlertSound(alertLevel: string) {
    try {
      // 根据预警级别播放不同的提示音
      const audio = new Audio()
      
      switch (alertLevel) {
        case 'critical':
        case 'high':
          audio.src = '/sounds/alert-high.mp3'
          break
        case 'medium':
          audio.src = '/sounds/alert-medium.mp3'
          break
        case 'low':
          audio.src = '/sounds/alert-low.mp3'
          break
      }
      
      audio.play().catch(error => {
        console.log('播放提示音失败:', error)
      })
      
    } catch (error) {
      console.log('播放提示音失败:', error)
    }
  }
  
  /**
   * 注册事件监听器
   */
  on(event: string, handler: Function) {
    if (!this.messageHandlers.has(event)) {
      this.messageHandlers.set(event, [])
    }
    this.messageHandlers.get(event)!.push(handler)
  }
  
  /**
   * 移除事件监听器
   */
  off(event: string, handler?: Function) {
    if (!handler) {
      this.messageHandlers.delete(event)
    } else {
      const handlers = this.messageHandlers.get(event)
      if (handlers) {
        const index = handlers.indexOf(handler)
        if (index > -1) {
          handlers.splice(index, 1)
        }
      }
    }
  }
  
  /**
   * 触发事件
   */
  private emit(event: string, data?: any) {
    const handlers = this.messageHandlers.get(event)
    if (handlers) {
      handlers.forEach(handler => {
        try {
          handler(data)
        } catch (error) {
          console.error('事件处理器执行失败:', error)
        }
      })
    }
  }
  
  /**
   * 获取连接状态
   */
  getReadyState(): number {
    return this.ws ? this.ws.readyState : WebSocket.CLOSED
  }
  
  /**
   * 是否已连接
   */
  isConnected(): boolean {
    return this.ws !== null && this.ws.readyState === WebSocket.OPEN
  }
}

// 导出单例
export const websocketClient = new WebSocketClient()

export default websocketClient
