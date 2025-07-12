<template>
  <div class="register-container">
    <div class="register-form">
      <div class="register-header">
        <div class="logo">
          <img src="/logo.png" alt="Logo" class="logo-img">
        </div>
        <h1 class="title">用户注册</h1>
        <p class="subtitle">创建您的数据手套管理账户</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form-content"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            :prefix-icon="Message"
            type="email"
            placeholder="请输入邮箱"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            :prefix-icon="Phone"
            placeholder="请输入手机号（可选）"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="realName">
          <el-input
            v-model="registerForm.realName"
            :prefix-icon="UserFilled"
            placeholder="请输入真实姓名（可选）"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            :prefix-icon="Lock"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            :prefix-icon="Lock"
            type="password"
            placeholder="请确认密码"
            size="large"
            show-password
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          <el-button type="text" @click="$router.push('/login')">
            已有账号？立即登录
          </el-button>
        </div>
      </el-form>
    </div>
    
    <div class="register-background">
      <div class="bg-decoration"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Message, Phone, UserFilled, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { validateEmail, validatePhone, validatePassword } from '@/utils'
import type { RegisterRequest } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

// 注册表单数据
const registerForm = reactive<RegisterRequest>({
  username: '',
  email: '',
  phone: '',
  realName: '',
  password: '',
  confirmPassword: ''
})

// 自定义验证规则
const validatePasswordStrength = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  
  const result = validatePassword(value)
  if (!result.valid) {
    callback(new Error(result.message))
  } else {
    callback()
  }
}

const validatePasswordConfirm = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
    return
  }
  
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateEmailFormat = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入邮箱'))
    return
  }
  
  if (!validateEmail(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

const validatePhoneFormat = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback() // 手机号是可选的
    return
  }
  
  if (!validatePhone(value)) {
    callback(new Error('请输入正确的手机号格式'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmailFormat, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhoneFormat, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePasswordStrength, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePasswordConfirm, trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  
  try {
    await userStore.register(registerForm)
    router.push('/')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  max-width: 500px;
  margin: 0 auto;
}

.register-header {
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

.register-form-content {
  width: 100%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  
  .el-form-item {
    margin-bottom: 20px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .register-button {
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
  
  .register-footer {
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

.register-background {
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
  .register-container {
    flex-direction: row;
  }
  
  .register-form {
    max-width: 50%;
  }
  
  .register-background {
    display: block;
  }
}

@media (max-width: 767px) {
  .register-form {
    padding: 20px;
  }
  
  .register-form-content {
    padding: 30px 20px;
  }
  
  .register-header {
    margin-bottom: 30px;
    
    .title {
      font-size: 24px;
    }
  }
}
</style>