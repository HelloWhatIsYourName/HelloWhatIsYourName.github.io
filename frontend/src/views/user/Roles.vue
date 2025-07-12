<template>
  <div class="roles-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>
      
      <el-table :data="roleData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" />
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
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const roleData = ref([
  {
    id: 1,
    roleName: 'ADMIN',
    description: '管理员角色',
    createdAt: '2024-01-01 12:00:00'
  },
  {
    id: 2,
    roleName: 'USER',
    description: '普通用户角色',
    createdAt: '2024-01-01 12:00:00'
  }
])

const handleAdd = () => {
  console.log('新增角色')
}

const handleEdit = (row: any) => {
  console.log('编辑角色', row)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除这个角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('删除成功')
  })
}

onMounted(() => {
  console.log('角色管理页面加载')
})
</script>

<style lang="scss" scoped>
.roles-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>