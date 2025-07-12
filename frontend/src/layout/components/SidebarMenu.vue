<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="appStore.sidebarCollapsed"
    :unique-opened="true"
    router
    background-color="var(--background-color)"
    text-color="var(--text-color-primary)"
    active-text-color="var(--primary-color)"
  >
    <template v-for="item in menuItems" :key="item.path">
      <el-menu-item
        v-if="!item.children"
        :index="item.path"
        :class="{ 'is-active': activeMenu === item.path }"
      >
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <template #title>
          <span>{{ item.meta?.title }}</span>
        </template>
      </el-menu-item>
      
      <el-sub-menu
        v-else
        :index="item.path"
        :class="{ 'is-active': activeMenu.startsWith(item.path) }"
      >
        <template #title>
          <el-icon v-if="item.meta?.icon">
            <component :is="item.meta.icon" />
          </el-icon>
          <span>{{ item.meta?.title }}</span>
        </template>
        
        <el-menu-item
          v-for="child in item.children"
          :key="child.path"
          :index="child.path"
          :class="{ 'is-active': activeMenu === child.path }"
        >
          <el-icon v-if="child.meta?.icon">
            <component :is="child.meta.icon" />
          </el-icon>
          <template #title>
            <span>{{ child.meta?.title }}</span>
          </template>
        </el-menu-item>
      </el-sub-menu>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 过滤菜单项
const menuItems = computed(() => {
  const routes = router.getRoutes()
  return routes
    .filter(item => {
      // 过滤掉隐藏的菜单和不需要显示的路由
      if (item.meta?.hidden) return false
      if (item.path === '/') return false
      if (item.path === '/login') return false
      if (item.path === '/register') return false
      if (item.path === '/404') return false
      
      // 检查权限
      if (item.meta?.roles && Array.isArray(item.meta.roles)) {
        return item.meta.roles.some(role => userStore.hasRole(role))
      }
      
      return true
    })
    .map(item => ({
      path: item.path,
      name: item.name,
      meta: item.meta,
      children: item.children?.filter(child => {
        if (child.meta?.hidden) return false
        if (child.meta?.roles && Array.isArray(child.meta.roles)) {
          return child.meta.roles.some(role => userStore.hasRole(role))
        }
        return true
      }).map(child => ({
        path: child.path,
        name: child.name,
        meta: child.meta,
      }))
    }))
})
</script>

<style lang="scss" scoped>
.el-menu {
  border: none;
  
  .el-menu-item {
    &:hover {
      background-color: var(--background-color-base) !important;
    }
    
    &.is-active {
      background-color: var(--primary-color) !important;
      color: #ffffff !important;
      
      .el-icon {
        color: #ffffff !important;
      }
    }
  }
  
  .el-sub-menu {
    .el-sub-menu__title {
      &:hover {
        background-color: var(--background-color-base) !important;
      }
    }
    
    &.is-active {
      .el-sub-menu__title {
        color: var(--primary-color) !important;
        
        .el-icon {
          color: var(--primary-color) !important;
        }
      }
    }
  }
}
</style>