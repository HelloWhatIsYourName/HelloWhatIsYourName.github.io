<template>
  <div class="learning-data-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>学习记录</span>
          <el-button type="primary" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </template>
      
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="用户ID">
            <el-input
              v-model="searchForm.userId"
              placeholder="请输入用户ID"
              clearable
            />
          </el-form-item>
          <el-form-item label="手势名称">
            <el-input
              v-model="searchForm.gestureName"
              placeholder="请输入手势名称"
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
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="gestureName" label="手势名称" />
        <el-table-column prop="practiceCount" label="练习次数" width="100" />
        <el-table-column prop="successCount" label="成功次数" width="100" />
        <el-table-column prop="successRate" label="成功率" width="100">
          <template #default="scope">
            <el-progress 
              :percentage="Math.round((scope.row.successCount / scope.row.practiceCount) * 100)"
              :color="getSuccessColor(scope.row.successCount / scope.row.practiceCount)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="averageConfidence" label="平均置信度" width="120">
          <template #default="scope">
            <el-tag :type="getConfidenceType(scope.row.averageConfidence)">
              {{ (scope.row.averageConfidence * 100).toFixed(1) }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastPracticeTime" label="最后练习时间" />
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
  userId: '',
  gestureName: ''
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const getSuccessColor = (rate: number) => {
  if (rate >= 0.8) return '#67C23A'
  if (rate >= 0.6) return '#E6A23C'
  return '#F56C6C'
}

const getConfidenceType = (confidence: number) => {
  if (confidence >= 0.8) return 'success'
  if (confidence >= 0.6) return 'warning'
  return 'danger'
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
    userId: '',
    gestureName: ''
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
}

const handleCurrentChange = (val: number) => {
  pagination.value.page = val
}

onMounted(() => {
  console.log('学习记录页面加载')
})
</script>

<style lang="scss" scoped>
.learning-data-container {
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