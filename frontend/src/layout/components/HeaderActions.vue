<template>
  <div class="header-actions">
    <!-- 主题切换 -->
    <el-tooltip content="切换主题">
      <el-button
        type="text"
        :icon="appStore.theme === 'light' ? 'Sunny' : 'Moon'"
        @click="toggleTheme"
      />
    </el-tooltip>
    
    <!-- 全屏切换 -->
    <el-tooltip content="全屏">
      <el-button
        type="text"
        :icon="isFullscreen ? 'Aim' : 'FullScreen'"
        @click="toggleFullscreen"
      />
    </el-tooltip>
    
    <!-- 消息通知 -->
    <el-badge :value="unreadCount" :hidden="unreadCount === 0">
      <el-tooltip content="消息通知">
        <el-button
          type="text"
          icon="Bell"
          @click="showNotifications"
        />
      </el-tooltip>
    </el-badge>
    
    <!-- 用户菜单 -->
    <el-dropdown @command="handleCommand">
      <div class="user-info">
        <el-avatar :src="userStore.avatar" :size="32">
          <el-icon><User /></el-icon>
        </el-avatar>
        <span class="username">{{ userStore.username }}</span>
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </div>
      
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">
            <el-icon><User /></el-icon>
            个人中心
          </el-dropdown-item>
          <el-dropdown-item command="settings">
            <el-icon><Setting /></el-icon>
            个人设置
          </el-dropdown-item>
          <el-dropdown-item divided command="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const isFullscreen = ref(false)
const unreadCount = ref(0)

onMounted(() => {
  // 监听全屏状态变化
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

// 切换主题
const toggleTheme = () => {
  appStore.toggleTheme()
  // 这里可以添加主题切换的具体逻辑
  document.documentElement.classList.toggle('dark', appStore.theme === 'dark')
}

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 处理全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 显示通知
const showNotifications = () => {
  ElMessage.info('暂无新消息')
}

// 处理用户菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/profile?tab=settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理登出
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await userStore.logout()
    router.push('/login')
    ElMessage.success('退出登录成功')
  }).catch(() => {
    // 用户取消
  })
}
</script>

<style lang="scss" scoped>
.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .el-button {
    border: none;
    background: none;
    color: var(--text-color-regular);
    
    &:hover {
      color: var(--primary-color);
      background-color: var(--background-color-base);
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s;
    
    &:hover {
      background-color: var(--background-color-base);
    }
    
    .username {
      font-size: 14px;
      color: var(--text-color-primary);
      font-weight: 500;
    }
    
    .el-icon--right {
      color: var(--text-color-secondary);
      font-size: 12px;
    }
  }
}
</style>