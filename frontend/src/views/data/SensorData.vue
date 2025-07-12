<template>
  <div class="sensor-data-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>传感器数据管理</span>
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
        </div>
      </template>
      
      <el-row :gutter="20" class="mb-4">
        <el-col :span="6">
          <el-select v-model="filters.deviceId" placeholder="选择设备" clearable>
            <el-option
              v-for="device in devices"
              :key="device.id"
              :label="device.deviceName"
              :value="device.id"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.sensorType" placeholder="传感器类型" clearable>
            <el-option label="加速度传感器" value="accelerometer" />
            <el-option label="陀螺仪" value="gyroscope" />
            <el-option label="磁力计" value="magnetometer" />
            <el-option label="压力传感器" value="pressure" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="filters.dateRange"
            type="datetimerange"
            placeholder="选择时间范围"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchSensorData">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>

      <el-table
        :data="sensorData"
        v-loading="loading"
        style="width: 100%"
        height="400"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="deviceId" label="设备ID" width="120" />
        <el-table-column prop="sensorType" label="传感器类型" width="120" />
        <el-table-column prop="sensorPosition" label="位置" width="100" />
        <el-table-column prop="dataValue" label="数据值" width="150" />
        <el-table-column prop="timestamp" label="时间戳" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="mt-4"
      />
    </el-card>

    <el-dialog v-model="detailDialog" title="传感器数据详情" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="数据ID">{{ selectedData?.id }}</el-descriptions-item>
        <el-descriptions-item label="设备ID">{{ selectedData?.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="传感器类型">{{ selectedData?.sensorType }}</el-descriptions-item>
        <el-descriptions-item label="传感器位置">{{ selectedData?.sensorPosition }}</el-descriptions-item>
        <el-descriptions-item label="数据值">{{ selectedData?.dataValue }}</el-descriptions-item>
        <el-descriptions-item label="时间戳">{{ selectedData?.timestamp }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { sensorDataAPI } from '@/api/data'
import { deviceAPI } from '@/api/device'

interface SensorData {
  id: number
  deviceId: string
  sensorType: string
  sensorPosition: string
  dataValue: string
  timestamp: string
}

interface Device {
  id: string
  deviceName: string
}

const loading = ref(false)
const sensorData = ref<SensorData[]>([])
const devices = ref<Device[]>([])
const selectedData = ref<SensorData | null>(null)
const detailDialog = ref(false)

const filters = ref({
  deviceId: '',
  sensorType: '',
  dateRange: null as any
})

const pagination = ref({
  page: 1,
  size: 20,
  total: 0
})

const fetchSensorData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page - 1,
      size: pagination.value.size,
      deviceId: filters.value.deviceId || undefined,
      sensorType: filters.value.sensorType || undefined,
      startTime: filters.value.dateRange?.[0] || undefined,
      endTime: filters.value.dateRange?.[1] || undefined
    }
    
    const response = await sensorDataAPI.getSensorData(params)
    sensorData.value = response.data.content || []
    pagination.value.total = response.data.totalElements || 0
  } catch (error) {
    ElMessage.error('获取传感器数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchDevices = async () => {
  try {
    const response = await deviceAPI.getDeviceList()
    devices.value = response.data || []
  } catch (error) {
    console.error('获取设备列表失败:', error)
  }
}

const refreshData = () => {
  fetchSensorData()
}

const resetFilters = () => {
  filters.value = {
    deviceId: '',
    sensorType: '',
    dateRange: null
  }
  pagination.value.page = 1
  fetchSensorData()
}

const viewDetails = (row: SensorData) => {
  selectedData.value = row
  detailDialog.value = true
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
  pagination.value.page = 1
  fetchSensorData()
}

const handleCurrentChange = (val: number) => {
  pagination.value.page = val
  fetchSensorData()
}

onMounted(() => {
  fetchDevices()
  fetchSensorData()
})
</script>

<style scoped>
.sensor-data-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
}
</style>