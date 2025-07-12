<template>
  <div class="gesture-data-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>手势识别数据</span>
          <el-button type="primary" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </template>
      
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="手势名称">
            <el-input
              v-model="searchForm.gestureName"
              placeholder="请输入手势名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="用户ID">
            <el-input
              v-model="searchForm.userId"
              placeholder="请输入用户ID"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="gestureName" label="手势名称" />
        <el-table-column prop="confidence" label="置信度" width="100">
          <template #default="scope">
            <el-progress 
              :percentage="Math.round(scope.row.confidence * 100)" 
              :color="getConfidenceColor(scope.row.confidence)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="deviceId" label="设备ID" />
        <el-table-column prop="recognitionTime" label="识别时间" />
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
  gestureName: '',
  userId: ''
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const getConfidenceColor = (confidence: number) => {
  if (confidence >= 0.8) return '#67C23A'
  if (confidence >= 0.6) return '#E6A23C'
  return '#F56C6C'
}

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
    gestureName: '',
    userId: ''
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
}

const handleCurrentChange = (val: number) => {
  pagination.value.page = val
}

onMounted(() => {
  console.log('手势识别数据页面加载')
})
</script>

<style lang="scss" scoped>
.gesture-data-container {
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