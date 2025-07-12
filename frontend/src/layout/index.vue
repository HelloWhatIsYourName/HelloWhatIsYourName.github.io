<template>
  <div class="layout">
    <!-- 侧边栏 -->
    <div 
      class="sidebar" 
      :class="{ 'sidebar-collapsed': appStore.sidebarCollapsed }"
    >
      <div class="sidebar-header">
        <div class="logo">
          <img src="/logo.png" alt="Logo" class="logo-img">
          <span v-if="!appStore.sidebarCollapsed" class="logo-text">数据手套</span>
        </div>
      </div>
      
      <div class="sidebar-menu">
        <SidebarMenu />
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 头部 -->
      <div class="header">
        <div class="header-left">
          <el-icon class="menu-toggle" @click="appStore.toggleSidebar">
            <Expand v-if="appStore.sidebarCollapsed" />
            <Fold v-else />
          </el-icon>
          
          <Breadcrumb />
        </div>
        
        <div class="header-right">
          <HeaderActions />
        </div>
      </div>
      
      <!-- 内容区 -->
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/app'
import SidebarMenu from './components/SidebarMenu.vue'
import Breadcrumb from './components/Breadcrumb.vue'
import HeaderActions from './components/HeaderActions.vue'

const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: var(--sidebar-width);
  background-color: var(--background-color);
  border-right: 1px solid var(--border-color-lighter);
  transition: width 0.3s ease;
  overflow: hidden;
  
  &.sidebar-collapsed {
    width: var(--sidebar-collapsed-width);
  }
  
  .sidebar-header {
    height: var(--header-height);
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-bottom: 1px solid var(--border-color-lighter);
    
    .logo {
      display: flex;
      align-items: center;
      
      .logo-img {
        width: 32px;
        height: 32px;
        margin-right: 12px;
      }
      
      .logo-text {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-color-primary);
      }
    }
  }
  
  .sidebar-menu {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background-color: var(--background-color);
  border-bottom: 1px solid var(--border-color-lighter);
  
  .header-left {
    display: flex;
    align-items: center;
    
    .menu-toggle {
      font-size: 20px;
      cursor: pointer;
      padding: 8px;
      border-radius: 4px;
      transition: background-color 0.2s;
      
      &:hover {
        background-color: var(--background-color-base);
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
  }
}

.content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background-color: var(--background-color-base);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>