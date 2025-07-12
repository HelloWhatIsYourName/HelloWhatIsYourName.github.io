<template>
  <div class="dashboard">
    <!-- 概览卡片 -->
    <div class="overview-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="card-content">
              <div class="card-icon primary">
                <el-icon size="32"><User /></el-icon>
              </div>
              <div class="card-info">
                <h3>{{ stats.totalUsers }}</h3>
                <p>总用户数</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="card-content">
              <div class="card-icon success">
                <el-icon size="32"><Monitor /></el-icon>
              </div>
              <div class="card-info">
                <h3>{{ stats.totalDevices }}</h3>
                <p>总设备数</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="card-content">
              <div class="card-icon warning">
                <el-icon size="32"><Connection /></el-icon>
              </div>
              <div class="card-info">
                <h3>{{ stats.onlineDevices }}</h3>
                <p>在线设备</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="card-content">
              <div class="card-icon danger">
                <el-icon size="32"><DataLine /></el-icon>
              </div>
              <div class="card-info">
                <h3>{{ stats.todayData }}</h3>
                <p>今日数据</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>设备状态分布</span>
              </div>
            </template>
            <div class="chart-container">
              <DeviceStatusChart :data="deviceStatusData" />
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>数据趋势图</span>
              </div>
            </template>
            <div class="chart-container">
              <DataTrendChart :data="dataTrendData" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 最近活动 -->
    <div class="recent-activity">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="activity-card">
            <template #header>
              <div class="card-header">
                <span>最近手势识别</span>
                <el-button type="text" size="small" @click="viewAllGestures">查看全部</el-button>
              </div>
            </template>
            <div class="activity-list">
              <div
                v-for="gesture in recentGestures"
                :key="gesture.id"
                class="activity-item"
              >
                <div class="activity-icon">
                  <el-icon><Pointer /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-title">{{ gesture.gestureName }}</div>
                  <div class="activity-desc">
                    置信度: {{ (gesture.confidence * 100).toFixed(1) }}%
                  </div>
                </div>
                <div class="activity-time">
                  {{ formatDateTime(gesture.recognitionTime) }}
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="activity-card">
            <template #header>
              <div class="card-header">
                <span>设备状态</span>
                <el-button type="text" size="small" @click="viewAllDevices">查看全部</el-button>
              </div>
            </template>
            <div class="device-list">
              <div
                v-for="device in recentDevices"
                :key="device.id"
                class="device-item"
              >
                <div class="device-info">
                  <div class="device-name">{{ device.deviceName }}</div>
                  <div class="device-id">{{ device.deviceId }}</div>
                </div>
                <div class="device-status">
                  <el-tag
                    :type="getDeviceStatusType(device.status)"
                    size="small"
                  >
                    {{ getDeviceStatusText(device.status) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { formatDateTime } from '@/utils'
import { getMyDataStatistics, getRecentGestureResults } from '@/api/data'
import { getMyDevices } from '@/api/device'
import DeviceStatusChart from '@/components/charts/DeviceStatusChart.vue'
import DataTrendChart from '@/components/charts/DataTrendChart.vue'
import type { GestureResult, Device } from '@/types'

const router = useRouter()

// 统计数据
const stats = reactive({
  totalUsers: 0,
  totalDevices: 0,
  onlineDevices: 0,
  todayData: 0
})

// 图表数据
const deviceStatusData = ref([])
const dataTrendData = ref([])

// 最近活动数据
const recentGestures = ref<GestureResult[]>([])
const recentDevices = ref<Device[]>([])

onMounted(() => {
  loadDashboardData()
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    // 加载统计数据
    const statisticsData = await getMyDataStatistics()
    stats.totalUsers = 1 // 当前用户
    stats.totalDevices = statisticsData.sensorDataCount > 0 ? 1 : 0
    stats.onlineDevices = 1
    stats.todayData = statisticsData.gestureResultCount
    
    // 加载最近手势识别
    const gestures = await getRecentGestureResults(5)
    recentGestures.value = gestures
    
    // 加载设备列表
    const devices = await getMyDevices()
    recentDevices.value = devices.slice(0, 5)
    
    // 设置图表数据
    deviceStatusData.value = [
      { name: '在线', value: devices.filter(d => d.status === 'ONLINE').length },
      { name: '离线', value: devices.filter(d => d.status === 'OFFLINE').length },
      { name: '维护', value: devices.filter(d => d.status === 'MAINTENANCE').length }
    ]
    
    dataTrendData.value = [
      { name: '传感器数据', value: statisticsData.sensorDataCount },
      { name: '手势识别', value: statisticsData.gestureResultCount },
      { name: '练习次数', value: statisticsData.totalPracticeCount }
    ]
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

// 获取设备状态类型
const getDeviceStatusType = (status: string) => {
  switch (status) {
    case 'ONLINE': return 'success'
    case 'OFFLINE': return 'danger'
    case 'MAINTENANCE': return 'warning'
    default: return 'info'
  }
}

// 获取设备状态文本
const getDeviceStatusText = (status: string) => {
  switch (status) {
    case 'ONLINE': return '在线'
    case 'OFFLINE': return '离线'
    case 'MAINTENANCE': return '维护'
    default: return '未知'
  }
}

// 查看全部手势
const viewAllGestures = () => {
  router.push('/data/gesture')
}

// 查看全部设备
const viewAllDevices = () => {
  router.push('/device/my-devices')
}
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 0;
}

.overview-cards {
  margin-bottom: 20px;
  
  .overview-card {
    .card-content {
      display: flex;
      align-items: center;
      padding: 8px 0;
      
      .card-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        
        &.primary {
          background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
          color: white;
        }
        
        &.success {
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
          color: white;
        }
        
        &.warning {
          background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
          color: white;
        }
        
        &.danger {
          background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
          color: white;
        }
      }
      
      .card-info {
        flex: 1;
        
        h3 {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-color-primary);
          margin: 0 0 4px 0;
        }
        
        p {
          font-size: 14px;
          color: var(--text-color-secondary);
          margin: 0;
        }
      }
    }
  }
}

.charts-section {
  margin-bottom: 20px;
  
  .chart-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      color: var(--text-color-primary);
    }
    
    .chart-container {
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

.recent-activity {
  .activity-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      color: var(--text-color-primary);
    }
    
    .activity-list {
      .activity-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-color-extra-light);
        
        &:last-child {
          border-bottom: none;
        }
        
        .activity-icon {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          background: var(--background-color-base);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          color: var(--primary-color);
        }
        
        .activity-content {
          flex: 1;
          
          .activity-title {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-color-primary);
            margin-bottom: 4px;
          }
          
          .activity-desc {
            font-size: 12px;
            color: var(--text-color-secondary);
          }
        }
        
        .activity-time {
          font-size: 12px;
          color: var(--text-color-placeholder);
        }
      }
    }
    
    .device-list {
      .device-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-color-extra-light);
        
        &:last-child {
          border-bottom: none;
        }
        
        .device-info {
          flex: 1;
          
          .device-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-color-primary);
            margin-bottom: 4px;
          }
          
          .device-id {
            font-size: 12px;
            color: var(--text-color-secondary);
          }
        }
        
        .device-status {
          margin-left: 12px;
        }
      }
    }
  }
}

@media (max-width: 1200px) {
  .overview-cards {
    .el-col {
      margin-bottom: 20px;
    }
  }
}

@media (max-width: 768px) {
  .charts-section,
  .recent-activity {
    .el-col {
      margin-bottom: 20px;
    }
  }
}
</style>