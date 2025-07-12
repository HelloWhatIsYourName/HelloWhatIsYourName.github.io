<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/store/app'
import { getDeviceType } from '@/utils'

const appStore = useAppStore()

onMounted(() => {
  // 设置设备类型
  appStore.setDevice(getDeviceType())
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
  handleResize()
})

const handleResize = () => {
  const width = window.innerWidth
  if (width < 768) {
    appStore.setDevice('mobile')
    appStore.setSidebarCollapsed(true)
  } else {
    appStore.setDevice('desktop')
  }
}
</script>

<style lang="scss">
#app {
  height: 100vh;
  overflow: hidden;
}
</style>