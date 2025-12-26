import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 应用状态
  const loading = ref(false)
  const sidebarCollapsed = ref(false)
  const theme = ref<'light' | 'dark'>('light')
  const language = ref('zh-CN')
  
  // 操作
  const setLoading = (value: boolean) => {
    loading.value = value
  }
  
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  const setSidebarCollapsed = (value: boolean) => {
    sidebarCollapsed.value = value
  }
  
  const setTheme = (value: 'light' | 'dark') => {
    theme.value = value
    localStorage.setItem('theme', value)
  }
  
  const setLanguage = (value: string) => {
    language.value = value
    localStorage.setItem('language', value)
  }
  
  // 初始化
  const initApp = () => {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      theme.value = savedTheme as 'light' | 'dark'
    }
    
    const savedLanguage = localStorage.getItem('language')
    if (savedLanguage) {
      language.value = savedLanguage
    }
  }
  
  return {
    // 状态
    loading,
    sidebarCollapsed,
    theme,
    language,
    
    // 操作
    setLoading,
    toggleSidebar,
    setSidebarCollapsed,
    setTheme,
    setLanguage,
    initApp
  }
})

