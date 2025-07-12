<template>
  <div class="device-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备列表</span>
          <el-button type="primary" @click="showAddDialog">添加设备</el-button>
        </div>
      </template>
      
      <el-row :gutter="20" class="mb-4">
        <el-col :span="6">
          <el-input
            v-model="filters.deviceId"
            placeholder="设备ID"
            clearable
          />
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="filters.deviceName"
            placeholder="设备名称"
            clearable
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.status" placeholder="设备状态" clearable>
            <el-option label="在线" value="ONLINE" />
            <el-option label="离线" value="OFFLINE" />
            <el-option label="维护中" value="MAINTENANCE" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchDevices">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>

      <el-table
        :data="devices"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="deviceId" label="设备ID" width="150" />
        <el-table-column prop="deviceName" label="设备名称" width="150" />
        <el-table-column prop="deviceType" label="设备类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editDevice(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteDevice(scope.row)">删除</el-button>
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

    <!-- 添加/编辑设备对话框 -->
    <el-dialog
      v-model="deviceDialog"
      :title="isEdit ? '编辑设备' : '添加设备'"
      width="500px"
    >
      <el-form
        ref="deviceFormRef"
        :model="deviceForm"
        :rules="deviceRules"
        label-width="100px"
      >
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="deviceForm.deviceId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="deviceForm.deviceName" />
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="deviceForm.deviceType" style="width: 100%">
            <el-option label="数据手套" value="DATA_GLOVE" />
            <el-option label="传感器模块" value="SENSOR_MODULE" />
            <el-option label="控制器" value="CONTROLLER" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="deviceForm.status" style="width: 100%">
            <el-option label="在线" value="ONLINE" />
            <el-option label="离线" value="OFFLINE" />
            <el-option label="维护中" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="deviceDialog = false">取消</el-button>
        <el-button type="primary" @click="saveDevice">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { deviceAPI } from '@/api/device'

interface Device {
  id?: number
  deviceId: string
  deviceName: string
  deviceType: string
  status: string
  createdAt?: string
  updatedAt?: string
}

const loading = ref(false)
const devices = ref<Device[]>([])
const deviceDialog = ref(false)
const isEdit = ref(false)
const deviceFormRef = ref<FormInstance>()

const filters = ref({
  deviceId: '',
  deviceName: '',
  status: ''
})

const pagination = ref({
  page: 1,
  size: 20,
  total: 0
})

const deviceForm = reactive<Device>({
  deviceId: '',
  deviceName: '',
  deviceType: '',
  status: 'OFFLINE'
})

const deviceRules: FormRules = {
  deviceId: [
    { required: true, message: '请输入设备ID', trigger: 'blur' }
  ],
  deviceName: [
    { required: true, message: '请输入设备名称', trigger: 'blur' }
  ],
  deviceType: [
    { required: true, message: '请选择设备类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择设备状态', trigger: 'change' }
  ]
}

const fetchDevices = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page - 1,
      size: pagination.value.size,
      deviceId: filters.value.deviceId || undefined,
      deviceName: filters.value.deviceName || undefined,
      status: filters.value.status || undefined
    }
    
    const response = await deviceAPI.getDeviceList(params)
    devices.value = response.data.content || []
    pagination.value.total = response.data.totalElements || 0
  } catch (error) {
    ElMessage.error('获取设备列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value = {
    deviceId: '',
    deviceName: '',
    status: ''
  }
  pagination.value.page = 1
  fetchDevices()
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(deviceForm, {
    deviceId: '',
    deviceName: '',
    deviceType: '',
    status: 'OFFLINE'
  })
  deviceDialog.value = true
}

const editDevice = (device: Device) => {
  isEdit.value = true
  Object.assign(deviceForm, device)
  deviceDialog.value = true
}

const saveDevice = async () => {
  if (!deviceFormRef.value) return
  
  try {
    await deviceFormRef.value.validate()
    
    if (isEdit.value) {
      await deviceAPI.updateDevice(deviceForm.id!, deviceForm)
      ElMessage.success('设备更新成功')
    } else {
      await deviceAPI.createDevice(deviceForm)
      ElMessage.success('设备添加成功')
    }
    
    deviceDialog.value = false
    fetchDevices()
  } catch (error) {
    ElMessage.error(isEdit.value ? '设备更新失败' : '设备添加失败')
    console.error(error)
  }
}

const deleteDevice = async (device: Device) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除设备 "${device.deviceName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deviceAPI.deleteDevice(device.id!)
    ElMessage.success('设备删除成功')
    fetchDevices()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('设备删除失败')
      console.error(error)
    }
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'ONLINE': return 'success'
    case 'OFFLINE': return 'danger'
    case 'MAINTENANCE': return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'ONLINE': return '在线'
    case 'OFFLINE': return '离线'
    case 'MAINTENANCE': return '维护中'
    default: return '未知'
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
  pagination.value.page = 1
  fetchDevices()
}

const handleCurrentChange = (val: number) => {
  pagination.value.page = val
  fetchDevices()
}

onMounted(() => {
  fetchDevices()
})
</script>

<style scoped>
.device-list-container {
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