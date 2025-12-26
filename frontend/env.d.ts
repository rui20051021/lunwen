/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 高德地图类型声明
declare global {
  interface Window {
    AMap: any
    AMapLoader: any
  }
}

// 环境变量类型声明
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_AMAP_API_KEY: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

