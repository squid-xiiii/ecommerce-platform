<template>
  <div class="system-logs">
    <h2 class="page-title">系统日志</h2>

    <div class="filter-bar">
      <el-radio-group v-model="logType" @change="loadLogs">
        <el-radio-button value="all">全部日志</el-radio-button>
        <el-radio-button value="error">错误日志</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="logs" stripe style="width: 100%">
      <el-table-column prop="level" label="级别" width="100">
        <template #default="{ row }">
          <el-tag :type="getLevelType(row.level)" size="small">{{ row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="message" label="消息" min-width="300" show-overflow-tooltip />
      <el-table-column prop="stackTrace" label="堆栈信息" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.stackTrace">{{ row.stackTrace.substring(0, 100) }}...</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminLogApi } from '@/api'

const logs = ref([])
const logType = ref('all')

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const getLevelType = (level) => {
  const map = { 'INFO': 'info', 'WARN': 'warning', 'ERROR': 'danger' }
  return map[level] || 'info'
}

const loadLogs = async () => {
  try {
    if (logType.value === 'all') {
      logs.value = await adminLogApi.getList(200)
    } else {
      logs.value = await adminLogApi.getErrors()
    }
  } catch (error) {
    ElMessage.error('加载日志失败')
  }
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.system-logs {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.page-title {
  font-size: 18px;
  margin: 0 0 20px 0;
  color: #333;
}

.filter-bar {
  margin-bottom: 20px;
}
</style>
