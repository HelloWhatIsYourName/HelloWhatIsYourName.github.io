<template>
  <div class="breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item
        v-for="item in breadcrumbItems"
        :key="item.path"
        :to="item.path"
      >
        {{ item.title }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 面包屑数据
const breadcrumbItems = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  const items = []
  
  // 添加首页
  if (route.path !== '/dashboard') {
    items.push({
      path: '/dashboard',
      title: '首页'
    })
  }
  
  // 添加匹配的路由
  matched.forEach(item => {
    if (item.path !== '/') {
      items.push({
        path: item.path,
        title: item.meta?.title || item.name
      })
    }
  })
  
  return items
})
</script>

<style lang="scss" scoped>
.breadcrumb {
  margin-left: 16px;
  
  .el-breadcrumb {
    font-size: 14px;
    
    .el-breadcrumb__item {
      .el-breadcrumb__inner {
        color: var(--text-color-regular);
        
        &:hover {
          color: var(--primary-color);
        }
      }
      
      &:last-child {
        .el-breadcrumb__inner {
          color: var(--text-color-primary);
          font-weight: 500;
        }
      }
    }
  }
}
</style>