<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import websocketClient from './utils/websocket'

const router = useRouter()

// 初始化WebSocket连接
onMounted(() => {
  const userId = localStorage.getItem('userId')
  const token = localStorage.getItem('token')
  
  // 如果用户已登录，建立WebSocket连接
  if (userId && token) {
    console.log('初始化WebSocket连接...')
    websocketClient.connect(userId)
    
    // 监听预警点击事件，跳转到预警详情页面
    websocketClient.on('alert-click', (alert: any) => {
      router.push({
        name: 'AlertDetail',
        params: { id: alert.alertId }
      })
    })
  }
})

// 组件卸载时关闭WebSocket连接
onUnmounted(() => {
  websocketClient.close()
})
</script>

<style>
#app {
  height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Helvetica Neue', sans-serif;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  background-color: #f5f7fa;
}
</style>