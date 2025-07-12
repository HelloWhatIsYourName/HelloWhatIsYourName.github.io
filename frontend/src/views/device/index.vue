<template>
  <div class="device-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增设备
          </el-button>
        </div>
      </template>
      
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="设备名称">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入设备名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="在线" value="ONLINE" />
              <el-option label="离线" value="OFFLINE" />
              <el-option label="维护" value="MAINTENANCE" />
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
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="deviceId" label="设备ID" />
        <el-table-column prop="deviceType" label="设备类型" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              删除
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const tableData = ref([])
const searchForm = ref({
  keyword: '',
  status: ''
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const getStatusType = (status: string) => {
  const statusMap = {
    ONLINE: 'success',
    OFFLINE: 'info',
    MAINTENANCE: 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap = {
    ONLINE: '在线',
    OFFLINE: '离线',
    MAINTENANCE: '维护'
  }
  return statusMap[status] || '未知'
}

const handleAdd = () => {
  console.log('新增设备')
}

const handleEdit = (row: any) => {
  console.log('编辑设备', row)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除这个设备吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('删除成功')
  })
}

const handleSearch = () => {
  console.log('搜索', searchForm.value)
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    status: ''
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
}

const handleCurrentChange = (val: number) => {
  pagination.value.page = val
}

onMounted(() => {
  console.log('设备管理页面加载')
})
</script>

<style lang="scss" scoped>
.device-manage-container {
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