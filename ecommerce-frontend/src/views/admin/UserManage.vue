<template>
  <div class="user-manage">
    <h2 class="page-title">用户信息管理</h2>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ totalUsers }}</div>
        <div class="stat-label">总用户数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ totalClicks }}</div>
        <div class="stat-label">总点击量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ avgClicksPerUser }}</div>
        <div class="stat-label">人均点击</div>
      </div>
    </div>

    <!-- 用户列表 -->
    <el-table :data="users" stripe style="width: 100%" @row-click="showUserDetail">
      <el-table-column prop="userName" label="用户名" width="120" />
      <el-table-column prop="nickName" label="昵称" width="120" />
      <el-table-column prop="age" label="年龄" width="120" />
      <el-table-column prop="school" label="学校" width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column label="点击量" width="100">
        <template #default="{ row }">
          <span class="click-count">{{ getUserClickCount(row.userName) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后活跃" width="180">
        <template #default="{ row }">{{ getLastActive(row.userName) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click.stop="showUserDetail(row)">查看详情</el-button>
          <el-button type="success" size="small" @click.stop="showSendAd(row)">发送通知</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 用户详情弹窗 -->
    <el-drawer v-model="userDetailVisible" :title="currentUser?.nickName || currentUser?.userName" direction="rtl" size="60%">
      <div v-if="currentUser" class="user-detail">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="info-row"><span class="info-label">用户名：</span><span>{{ currentUser.userName }}</span></div>
          <div class="info-row"><span class="info-label">昵称：</span><span>{{ currentUser.nickName }}</span></div>
          <div class="info-row"><span class="info-label">姓名：</span><span>{{ currentUser.name }}</span></div>
          <div class="info-row"><span class="info-label">性别：</span><span>{{ currentUser.sex }}</span></div>
          <div class="info-row"><span class="info-label">手机：</span><span>{{ currentUser.phone }}</span></div>
          <div class="info-row"><span class="info-label">地址：</span><span>{{ currentUser.address }}</span></div>
          <div class="info-row"><span class="info-label">总点击：</span><span class="highlight">{{ userStats?.totalClicks || 0 }} 次</span></div>
        </div>

        <!-- 修复：添加空值判断 -->
        <div class="detail-section">
          <h4>点击历史</h4>
          <div v-if="!userStats?.clickHistory || userStats.clickHistory.length === 0" class="empty">暂无点击记录</div>
          <div v-else class="click-history">
            <div v-for="item in userStats.clickHistory" :key="item.goodsId" class="click-item">
              <div class="click-item-info">
                <span class="click-item-name">{{ item.goodsName || `商品ID:${item.goodsId}` }}</span>
                <span class="click-item-count">点击 {{ item.count }} 次</span>
              </div>
              <div class="click-item-bar">
                <div class="click-item-bar-fill" :style="{ width: getItemClickPercent(item.count) + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>个性化推荐</h4>
          <div v-if="recommendations.length === 0" class="empty">暂无推荐</div>
          <div class="recommend-grid">
            <div v-for="goods in recommendations" :key="goods.goodsId" class="recommend-card" @click="goToGoods(goods.goodsId)">
              <div class="recommend-img"><el-icon :size="32"><Goods /></el-icon></div>
              <div class="recommend-info">
                <div class="recommend-name">{{ goods.goodsInfo?.substring(0, 20) }}...</div>
                <div class="recommend-price">¥{{ formatPrice(goods.price) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 发送广告弹窗 -->
    <el-dialog v-model="adDialogVisible" title="发送通知" width="400px">
      <el-input v-model="adContent" type="textarea" rows="6" placeholder="请输入通知内容" />
      <div class="ad-tip">通知将显示在用户的消息中心</div>
      <template #footer>
        <el-button @click="adDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendAd" :loading="adSending">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminUserApi, goodsApi } from '@/api'
import { Goods } from '@element-plus/icons-vue'

const users = ref([])
const userStatsMap = ref({})
const goodsRanking = ref([])
const userDetailVisible = ref(false)
const adDialogVisible = ref(false)
const currentUser = ref(null)
const userStats = ref(null)
const recommendations = ref([])
const adContent = ref('')
const adSending = ref(false)

const totalUsers = computed(() => users.value.length)
const totalClicks = computed(() => {
  return Object.values(userStatsMap.value).reduce((sum, s) => sum + (s?.totalClicks || 0), 0)
})
const avgClicksPerUser = computed(() => {
  return totalUsers.value ? (totalClicks.value / totalUsers.value).toFixed(1) : 0
})

const formatPrice = (price) => (price / 100).toFixed(2)

const getClickPercent = (count) => {
  const max = Math.max(...goodsRanking.value.map(g => g.clickCount), 1)
  return (count / max * 100).toFixed(1)
}

const getItemClickPercent = (count) => {
  const max = Math.max(...(userStats.value?.clickHistory?.map(c => c.count) || [1]))
  return (count / max * 100).toFixed(1)
}

const getUserClickCount = (userId) => userStatsMap.value[userId]?.totalClicks || 0

const getLastActive = (userId) => {
  const last = userStatsMap.value[userId]?.lastClickTime
  return last ? new Date(last).toLocaleString() : '从未'
}

const loadUsers = async () => {
  try {
    users.value = await adminUserApi.getAllUsers()
    for (const user of users.value) {
      try {
        const stats = await adminUserApi.getUserClickStats(user.userName)
        userStatsMap.value[user.userName] = stats || { totalClicks: 0, clickHistory: [] }
      } catch (e) {
        console.error('加载用户统计失败', e)
        userStatsMap.value[user.userName] = { totalClicks: 0, clickHistory: [] }
      }
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

const loadGoodsRanking = async () => {
  try {
    goodsRanking.value = await adminUserApi.getGoodsClickRanking() || []
  } catch (error) {
    console.error('加载商品排行失败', error)
    goodsRanking.value = []
  }
}

const showUserDetail = async (user) => {
  currentUser.value = user
  userDetailVisible.value = true

  try {
    // 加载用户统计
    const stats = await adminUserApi.getUserClickStats(user.userName)
    userStats.value = stats || { totalClicks: 0, clickHistory: [] }

    // 加载推荐商品
    try {
      const goodsIds = await adminUserApi.getRecommendations(user.userName, 6) || []
      const goodsList = []
      for (const id of goodsIds) {
        try {
          const goods = await goodsApi.getDetail(id)
          goodsList.push(goods)
        } catch (e) {
          console.error('获取商品失败', id, e)
        }
      }
      recommendations.value = goodsList
    } catch (e) {
      console.error('加载推荐失败', e)
      recommendations.value = []
    }
  } catch (error) {
    console.error('加载用户详情失败', error)
    ElMessage.error('加载用户详情失败')
  }
}

const showSendAd = (user) => {
  currentUser.value = user
  adContent.value = ''
  adDialogVisible.value = true
}

const sendAd = async () => {
  if (!adContent.value.trim()) {
    ElMessage.warning('请输入广告内容')
    return
  }

  adSending.value = true
  try {
    await adminUserApi.sendAd(currentUser.value.userName, adContent.value)
    ElMessage.success('广告发送成功，用户将在消息中心查收')
    adDialogVisible.value = false
    adContent.value = ''
  } catch (error) {
    console.error('发送广告失败', error)
    ElMessage.error(error.response?.data?.message || '发送失败，请检查后端接口')
  } finally {
    adSending.value = false
  }
}

const goToGoods = (id) => {
  window.open(`/user/goods/${id}`, '_blank')
}

onMounted(() => {
  loadUsers()
  loadGoodsRanking()
})
</script>

<style scoped>
/* 整体容器 */
.user-manage {
  background: #f5f7fa;
  border-radius: 20px;
  padding: 24px;
  min-height: 100%;
}

/* 页面标题 */
.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 24px 0;
  color: #1a2c3e;
  position: relative;
  padding-left: 16px;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #409eff, #36cea0);
  border-radius: 2px;
}

/* 统计卡片行 */
.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 28px;
}

/* 统计卡片 */
.stat-card {
  flex: 1;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fc 100%);
  border-radius: 20px;
  padding: 20px 24px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(64, 158, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #36cea0);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #1a2c3e;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #7a8a9a;
  margin-top: 8px;
  font-weight: 500;
}

/* 表格样式优化 */
.el-table {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.el-table :deep(.el-table__header th) {
  background: #f8f9fc;
  color: #1a2c3e;
  font-weight: 600;
  font-size: 14px;
}

.el-table :deep(.el-table__row:hover) {
  background: rgba(64, 158, 255, 0.04);
}

/* 点击量数字 */
.click-count {
  color: #409eff;
  font-weight: 600;
  font-size: 16px;
}

/* 用户详情弹窗样式 */
.user-detail {
  padding: 20px;
}

.detail-section {
  margin-bottom: 28px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eef2f6;
  color: #1a2c3e;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-section h4::before {
  content: '';
  width: 4px;
  height: 18px;
  background: linear-gradient(135deg, #409eff, #36cea0);
  border-radius: 2px;
}

.info-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  width: 90px;
  color: #7a8a9a;
  font-size: 14px;
}

.info-row span:last-child {
  color: #1a2c3e;
  font-weight: 500;
}

.highlight {
  color: #409eff !important;
  font-weight: 600 !important;
}

/* 点击历史列表 */
.click-history {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.click-item {
  padding: 14px 16px;
  background: #f8f9fc;
  border-radius: 14px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.click-item:hover {
  background: #ffffff;
  border-color: rgba(64, 158, 255, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.click-item-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.click-item-name {
  font-size: 14px;
  color: #1a2c3e;
  font-weight: 500;
}

.click-item-count {
  font-size: 12px;
  color: #f56c6c;
  font-weight: 500;
  background: rgba(245, 108, 108, 0.1);
  padding: 2px 8px;
  border-radius: 20px;
}

.click-item-bar {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.click-item-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #36cea0);
  border-radius: 3px;
  transition: width 0.3s ease;
}

/* 推荐商品网格 */
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.recommend-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8f9fc;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.recommend-card:hover {
  background: #ffffff;
  border-color: rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.recommend-img {
  width: 55px;
  height: 55px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.recommend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.recommend-name {
  font-size: 13px;
  color: #1a2c3e;
  margin-bottom: 6px;
  font-weight: 500;
  line-height: 1.4;
}

.recommend-price {
  color: #f56c6c;
  font-size: 15px;
  font-weight: 700;
}

.recommend-price::before {
  content: '¥';
  font-size: 12px;
}

/* 广告提示 */
.ad-tip {
  font-size: 12px;
  color: #7a8a9a;
  margin-top: 8px;
  padding: 10px 12px;
  background: #f8f9fc;
  border-radius: 10px;
  border-left: 3px solid #409eff;
}

/* 空状态 */
.empty {
  text-align: center;
  padding: 50px;
  color: #a0b0c0;
  font-size: 14px;
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 40px;
  color: #a0b0c0;
}

/* 弹窗内表单样式 */
.user-info-form {
  padding: 8px;
}

.user-info-form .el-form-item {
  margin-bottom: 18px;
}

.user-info-form .el-form-item__label {
  font-weight: 500;
  color: #1a2c3e;
}

/* 按钮样式优化 */

/* 抽屉头部样式 */
:deep(.el-drawer__header) {
  padding: 20px 24px;
  margin-bottom: 0;
  border-bottom: 1px solid #f0f2f5;
}

:deep(.el-drawer__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1a2c3e;
}

:deep(.el-drawer__body) {
  padding: 0;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f0f2f5;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c0c8d0;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a0b0c0;
}
</style>
