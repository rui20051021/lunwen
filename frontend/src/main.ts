import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 样式
import './styles/index.css'

// 创建应用实例
const app = createApp(App)

// 首先使用pinia，确保store可用
app.use(pinia)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用其他插件
app.use(router)
app.use(ElementPlus, {
  size: 'default',
  zIndex: 3000
})

// 挂载应用
app.mount('#app')

