<template>
  <div class="dashboard">
    <h2 class="page-title">数据看板</h2>

    <div class="stats-grid">
      <div v-for="stat in statCards" :key="stat.label" class="stat-card">
        <div class="stat-icon" :style="{ background: stat.color }">
          <el-icon><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card">
        <h3>商品分类统计</h3>
        <div v-if="goodsByCategory" class="category-stats">
          <div v-for="(count, category) in goodsByCategory" :key="category" class="category-item">
            <span class="category-name">{{ category }}</span>
            <span class="category-count">{{ count }} 件</span>
            <div class="category-bar">
              <div class="category-bar-fill" :style="{ width: getPercent(count, totalGoods) + '%' }"></div>
            </div>
          </div>
        </div>
        <div v-else class="loading">加载中...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { statsApi } from '@/api'

const stats = ref({
  totalGoods: 0,
  totalOrders: 0,
  totalUsers: 0,
  totalComments: 0,
  totalClicks: 0,
  totalCompletedOrders: 0,
  goodsByCategory: {}
})

const totalGoods = computed(() => stats.value.totalGoods)

const goodsByCategory = computed(() => stats.value.goodsByCategory)

const statCards = computed(() => [
  { label: '商品总数', value: stats.value.totalGoods, color: '#409eff', icon: 'Goods' },
  { label: '订单总数', value: stats.value.totalOrders, color: '#67c23a', icon: 'List' },
  { label: '用户总数', value: stats.value.totalUsers, color: '#e6a23c', icon: 'User' },
  { label: '评论总数', value: stats.value.totalComments, color: '#909399', icon: 'ChatDotRound' },
  { label: '点击总量', value: stats.value.totalClicks, color: '#f56c6c', icon: 'View' },
  { label: '已完成订单', value: stats.value.totalCompletedOrders, color: '#8e44ad', icon: 'DocumentChecked' }
])

const getPercent = (count, total) => {
  if (!total || total === 0) return 0
  return ((count / total) * 100).toFixed(1)
}

const loadStats = async () => {
  try {
    stats.value = await statsApi.getStats()
    console.log('统计数据:', stats.value)
  } catch (error) {
    console.error('加载统计数据失败', error)
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  background: #f0f2f5;
  min-height: 100%;
  padding: 20px;
}

.page-title {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.chart-card h3 {
  font-size: 16px;
  margin-bottom: 16px;
  color: #333;
}

.category-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.category-name {
  width: 100px;
  font-size: 14px;
  color: #333;
}

.category-count {
  width: 60px;
  font-size: 14px;
  color: #409eff;
  font-weight: bold;
}

.category-bar {
  flex: 1;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.category-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  border-radius: 4px;
  transition: width 0.3s;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
