<template>
  <div class="click-stats">
    <!-- 按页面统计 -->
    <div class="chart-card">
      <h3>按页面统计</h3>
      <el-table :data="pageStatsList" stripe>
        <el-table-column prop="pageCode" label="页面代码" />
        <el-table-column prop="count" label="点击次数" sortable />
        <el-table-column label="占比">
          <template #default="{ row }">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getPercent(row.count, clickStats.totalCount) + '%' }"></div>
              <span class="progress-text">{{ getPercent(row.count, clickStats.totalCount) }}%</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 最近点击记录 -->
    <div class="chart-card">
      <h3>最近点击记录</h3>
      <el-table :data="recentClicks" stripe>
        <el-table-column prop="pageCode" label="页面代码" width="150" />
        <el-table-column prop="clickPosition" label="点击位置" width="150" />
        <el-table-column prop="userId" label="用户" width="150" />
        <el-table-column prop="clickTime" label="点击时间" width="180">
          <template #default="{ row }">{{ formatDate(row.clickTime) }}</template>
        </el-table-column>
        <el-table-column prop="url" label="URL" show-overflow-tooltip />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminClickApi } from '@/api'

const clickStats = ref({ totalCount: 0, pageStats: {} })
const recentClicks = ref([])

const pageStatsList = computed(() => {
  const list = []
  for (const [pageCode, count] of Object.entries(clickStats.value.pageStats || {})) {
    list.push({ pageCode, count })
  }
  return list.sort((a, b) => b.count - a.count)
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const getPercent = (count, total) => {
  if (!total || total === 0) return 0
  return ((count / total) * 100).toFixed(1)
}

const loadData = async () => {
  try {
    const [stats, clicks] = await Promise.all([
      adminClickApi.getStats(),
      adminClickApi.getList(50)
    ])
    clickStats.value = stats
    recentClicks.value = clicks
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.click-stats {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.page-title {
  font-size: 18px;
  margin: 0 0 20px 0;
  color: #333;
}

.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  margin-bottom: 20px;
}

.total-label {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
  margin-bottom: 8px;
}

.total-value {
  color: white;
  font-size: 48px;
  font-weight: bold;
}

.chart-card {
  margin-top: 20px;
}

.chart-card h3 {
  font-size: 18px;
  margin-bottom: 16px;
  color: #333;
}

.progress-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-fill {
  height: 8px;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  border-radius: 4px;
  flex: 1;
}

.progress-text {
  font-size: 12px;
  color: #666;
  min-width: 40px;
}
</style>
