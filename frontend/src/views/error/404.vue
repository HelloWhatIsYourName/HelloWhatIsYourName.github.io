<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-content">
        <div class="error-number">404</div>
        <div class="error-title">页面不存在</div>
        <div class="error-description">
          抱歉，您访问的页面不存在或已被删除。
        </div>
        <div class="error-actions">
          <el-button type="primary" @click="goHome">
            返回首页
          </el-button>
          <el-button @click="goBack">
            返回上页
          </el-button>
        </div>
      </div>
      <div class="error-image">
        <img src="/404.svg" alt="404" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.error-container {
  display: flex;
  align-items: center;
  max-width: 800px;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.error-content {
  flex: 1;
  text-align: center;
  
  .error-number {
    font-size: 120px;
    font-weight: 700;
    color: var(--primary-color);
    line-height: 1;
    margin-bottom: 20px;
  }
  
  .error-title {
    font-size: 32px;
    font-weight: 600;
    color: var(--text-color-primary);
    margin-bottom: 16px;
  }
  
  .error-description {
    font-size: 16px;
    color: var(--text-color-secondary);
    margin-bottom: 40px;
    line-height: 1.6;
  }
  
  .error-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;
    
    .el-button {
      padding: 12px 24px;
      font-size: 14px;
    }
  }
}

.error-image {
  flex: 1;
  text-align: center;
  
  img {
    max-width: 100%;
    height: auto;
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .error-container {
    flex-direction: column;
    text-align: center;
    padding: 30px 20px;
  }
  
  .error-content {
    margin-bottom: 30px;
    
    .error-number {
      font-size: 80px;
    }
    
    .error-title {
      font-size: 24px;
    }
    
    .error-description {
      font-size: 14px;
    }
    
    .error-actions {
      flex-direction: column;
      align-items: center;
      
      .el-button {
        width: 200px;
      }
    }
  }
  
  .error-image {
    img {
      max-height: 200px;
    }
  }
}
</style>