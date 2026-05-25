<template>
  <div class="dashboard">
    <h2 class="page-title">数据看板</h2>

    <!-- 统计卡片 -->
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

    <!-- 商品分类统计 -->
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

    <!-- 热门商品排行 -->
    <div class="ranking-section">
      <h3>热门商品排行</h3>
      <div v-if="goodsRankingLoading" class="loading">加载中...</div>
      <div v-else-if="goodsRanking.length === 0" class="empty">暂无点击数据</div>
      <div v-else class="ranking-list">
        <div v-for="(goods, index) in goodsRanking" :key="goods.goodsId" class="ranking-item">
          <div class="ranking-number" :class="getRankClass(index)">{{ index + 1 }}</div>
          <div class="ranking-name">{{ goods.goodsName }}</div>
          <div class="ranking-count">{{ goods.clickCount }} 次点击</div>
          <div class="ranking-bar">
            <div class="ranking-bar-fill" :style="{ width: getClickPercent(goods.clickCount) + '%' }"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { statsApi, adminUserApi } from '@/api'

const stats = ref({
  totalGoods: 0,
  totalOrders: 0,
  totalUsers: 0,
  totalComments: 0,
  totalClicks: 0,
  totalCompletedOrders: 0,
  goodsByCategory: {}
})

const goodsRanking = ref([])
const goodsRankingLoading = ref(false)

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

const getClickPercent = (count) => {
  const max = Math.max(...goodsRanking.value.map(g => g.clickCount), 1)
  return (count / max * 100).toFixed(1)
}

const getRankClass = (index) => {
  if (index === 0) return 'rank-1'
  if (index === 1) return 'rank-2'
  if (index === 2) return 'rank-3'
  return ''
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

const loadGoodsRanking = async () => {
  goodsRankingLoading.value = true
  try {
    goodsRanking.value = await adminUserApi.getGoodsClickRanking() || []
    console.log('商品排行:', goodsRanking.value)
  } catch (error) {
    console.error('加载商品排行失败', error)
    goodsRanking.value = []
  } finally {
    goodsRankingLoading.value = false
  }
}

onMounted(() => {
  loadStats()
  loadGoodsRanking()
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
  margin-bottom: 20px;
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

/* 热门商品排行样式 */
.ranking-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.ranking-section h3 {
  font-size: 16px;
  margin-bottom: 16px;
  color: #333;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 12px;
  background: #f8f9fc;
  border-radius: 8px;
}

.ranking-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e9ecef;
  border-radius: 50%;
  font-weight: bold;
}

.ranking-number.rank-1 {
  background: #ffd700;
  color: #333;
}

.ranking-number.rank-2 {
  background: #c0c0c0;
  color: #333;
}

.ranking-number.rank-3 {
  background: #cd7f32;
  color: white;
}

.ranking-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.ranking-count {
  width: 80px;
  font-size: 13px;
  color: #f56c6c;
}

.ranking-bar {
  width: 150px;
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.ranking-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #36cea0);
  border-radius: 3px;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
