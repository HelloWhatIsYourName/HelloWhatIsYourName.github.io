<template>
  <div class="sensor-data-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>传感器数据</span>
          <el-button type="primary" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </template>
      
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="设备ID">
            <el-input
              v-model="searchForm.deviceId"
              placeholder="请输入设备ID"
              clearable
            />
          </el-form-item>
          <el-form-item label="传感器类型">
            <el-select v-model="searchForm.sensorType" placeholder="请选择类型" clearable>
              <el-option label="弯曲传感器" value="FLEX" />
              <el-option label="应变传感器" value="STRAIN" />
              <el-option label="惯性测量单元" value="IMU" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="deviceId" label="设备ID" />
        <el-table-column prop="sensorType" label="传感器类型" />
        <el-table-column prop="sensorPosition" label="传感器位置" />
        <el-table-column prop="dataValue" label="数据值" />
        <el-table-column prop="timestamp" label="时间戳" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const tableData = ref([])
const searchForm = ref({
  deviceId: '',
  sensorType: ''
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const handleRefresh = () => {
  console.log('刷新数据')
  ElMessage.success('数据刷新成功')
}

const handleView = (row: any) => {
  console.log('查看详情', row)
}

const handleSearch = () => {
  console.log('搜索', searchForm.value)
}

const handleReset = () => {
  searchForm.value = {
    deviceId: '',
    sensorType: ''
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
}

const handleCurrentChange = (val: number) => {
  pagination.value.page = val
}

onMounted(() => {
  console.log('传感器数据页面加载')
})
</script>

<style lang="scss" scoped>
.sensor-data-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-form {
    margin-bottom: 20px;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>