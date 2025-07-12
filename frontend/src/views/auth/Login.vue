<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <div class="logo">
          <img src="/logo.png" alt="Logo" class="logo-img">
        </div>
        <h1 class="title">数据手套管理平台</h1>
        <p class="subtitle">Data Glove Management Platform</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form-content"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            :prefix-icon="Lock"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <el-button type="text" @click="$router.push('/register')">
            没有账号？立即注册
          </el-button>
        </div>
      </el-form>
    </div>
    
    <div class="login-background">
      <div class="bg-decoration"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import type { LoginRequest } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

// 登录表单数据
const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  
  try {
    await userStore.login(loginForm)
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  max-width: 500px;
  margin: 0 auto;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo {
    margin-bottom: 16px;
    
    .logo-img {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    }
  }
  
  .title {
    font-size: 28px;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }
  
  .subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
    margin: 0;
  }
}

.login-form-content {
  width: 100%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  
  .el-form-item {
    margin-bottom: 24px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .login-button {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    border-radius: 8px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    
    &:hover {
      opacity: 0.9;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }
  
  .login-footer {
    text-align: center;
    margin-top: 24px;
    
    .el-button {
      color: var(--text-color-secondary);
      
      &:hover {
        color: var(--primary-color);
      }
    }
  }
}

.login-background {
  flex: 1;
  position: relative;
  display: none;
  
  .bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('/bg-pattern.png') no-repeat center center;
    background-size: cover;
    opacity: 0.3;
  }
}

@media (min-width: 768px) {
  .login-container {
    flex-direction: row;
  }
  
  .login-form {
    max-width: 50%;
  }
  
  .login-background {
    display: block;
  }
}

@media (max-width: 767px) {
  .login-form {
    padding: 20px;
  }
  
  .login-form-content {
    padding: 30px 20px;
  }
  
  .login-header {
    margin-bottom: 30px;
    
    .title {
      font-size: 24px;
    }
  }
}
</style>