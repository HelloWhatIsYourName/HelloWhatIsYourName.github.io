import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 状态
  const sidebarCollapsed = ref(false)
  const device = ref<'desktop' | 'mobile'>('desktop')
  const theme = ref<'light' | 'dark'>('light')
  const language = ref<'zh-CN' | 'en'>('zh-CN')
  const pageLoading = ref(false)
  
  // 切换侧边栏
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  // 设置侧边栏状态
  const setSidebarCollapsed = (collapsed: boolean) => {
    sidebarCollapsed.value = collapsed
  }
  
  // 设置设备类型
  const setDevice = (deviceType: 'desktop' | 'mobile') => {
    device.value = deviceType
  }
  
  // 切换主题
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }
  
  // 设置主题
  const setTheme = (themeType: 'light' | 'dark') => {
    theme.value = themeType
  }
  
  // 设置语言
  const setLanguage = (lang: 'zh-CN' | 'en') => {
    language.value = lang
  }
  
  // 设置页面加载状态
  const setPageLoading = (loading: boolean) => {
    pageLoading.value = loading
  }
  
  return {
    // 状态
    sidebarCollapsed,
    device,
    theme,
    language,
    pageLoading,
    
    // 方法
    toggleSidebar,
    setSidebarCollapsed,
    setDevice,
    toggleTheme,
    setTheme,
    setLanguage,
    setPageLoading,
  }
})