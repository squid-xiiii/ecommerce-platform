<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <div class="banner">
      <el-carousel height="160px" :interval="3000" indicator-position="none" @change="handleBannerChange">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="banner-item" :style="{ background: item.bg }" @click="logBannerClick(index)">
            {{ item.text }}
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-entry">
      <div class="entry-item" @click="handleEntryClick('热门推荐')">
        <div class="entry-icon">🔥</div>
        <span>热门推荐</span>
      </div>
      <div class="entry-item" @click="handleEntryClick('笔记本电脑')">
        <div class="entry-icon">💻</div>
        <span>笔记本电脑</span>
      </div>
      <div class="entry-item" @click="handleEntryClick('手机')">
        <div class="entry-icon">📱</div>
        <span>手机数码</span>
      </div>
      <div class="entry-item" @click="handleEntryClick('配件')">
        <div class="entry-icon">🎧</div>
        <span>配件外设</span>
      </div>
    </div>

    <!-- 热门推荐（个性化） -->
    <div class="section">
      <div class="section-header">
        <h3>为你推荐</h3>
        <span class="more" @click="handleEntryClick('更多商品')">更多 ></span>
      </div>
      <div v-if="recommendLoading" class="loading">加载推荐中...</div>
      <div v-else-if="recommendGoods.length === 0" class="empty">暂无推荐，先去逛逛吧</div>
      <div v-else class="goods-grid">
        <div v-for="goods in recommendGoods" :key="goods.goodsId" class="goods-card" @click="handleGoodsClick(goods.goodsId, goods.goodsInfo)">
          <div class="goods-img">
            <el-icon :size="32"><Goods /></el-icon>
          </div>
          <div class="goods-info">
            <div class="goods-title">{{ goods.goodsInfo.substring(0, 20) }}...</div>
            <div class="goods-price">¥{{ formatPrice(goods.price) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Goods } from '@element-plus/icons-vue'
import { goodsApi, clickApi, userRecommendApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const recommendGoods = ref([])
const recommendLoading = ref(false)
const hasLoaded = ref(false)  // 防止重复加载

const banners = [
  { text: '新品上市', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { text: '限时特惠', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { text: '爆款推荐', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
]

// 记录点击日志
const logClick = async (pageCode, clickPosition, pageContent, url) => {
  try {
    await clickApi.logClick({
      pageCode: pageCode,
      url: url,
      clickPosition: clickPosition,
      pageContent: pageContent,
      userId: userStore.user?.userName || '游客',
      clickTime: new Date()
    })
  } catch (error) {
    console.error('记录点击失败:', error)
  }
}

// 轮播图点击
const handleBannerChange = (index) => {
  console.log('轮播图切换到:', index)
}

const logBannerClick = (index) => {
  const banner = banners[index]
  logClick('home_banner', `banner_${index}`, banner.text, '/')
}

// 快捷入口点击
const handleEntryClick = async (entryName) => {
  logClick('quick_entry', entryName, entryName, '/')
  try {
    // 统一跳转到商品列表页，非热门/更多通过 query 传递分类
    if (entryName === '热门推荐' || entryName === '更多商品') {
      await router.push({ path: '/user/goods' })
    } else {
      await router.push({ path: '/user/goods', query: { category: entryName } })
    }
  } catch (err) {
    // 忽略重复导航或其它被阻止的导航错误
    console.warn('导航失败或被忽略:', err)
  }
}

// 商品点击
const handleGoodsClick = (goodsId, goodsName) => {
  logClick('goods_card', goodsId.toString(), goodsName, `/user/goods/${goodsId}`)
  router.push(`/user/goods/${goodsId}`)
}

const formatPrice = (price) => {
  return (price / 100).toFixed(2)
}

// 加载个性化推荐（只加载一次，不会消失）
const loadRecommendations = async () => {
  // 如果已经加载过，不再重复加载
  if (hasLoaded.value) return

  if (!userStore.user) {
    // 未登录时加载默认商品
    await loadDefaultGoods()
    hasLoaded.value = true
    return
  }

  recommendLoading.value = true
  try {
    const goodsIds = await userRecommendApi.getRecommendations(userStore.user.userName, 6)
    const goodsList = []
    for (const id of goodsIds) {
      try {
        const goods = await goodsApi.getDetail(id)
        goodsList.push(goods)
      } catch (e) {
        console.error('获取商品失败', id)
      }
    }
    recommendGoods.value = goodsList
    hasLoaded.value = true
  } catch (error) {
    console.error('加载推荐失败', error)
    await loadDefaultGoods()
    hasLoaded.value = true
  } finally {
    recommendLoading.value = false
  }
}

// 加载默认商品（未登录时）
const loadDefaultGoods = async () => {
  try {
    const allGoods = await goodsApi.getList()
    // 取前6个商品
    recommendGoods.value = allGoods.slice(0, 6)
  } catch (error) {
    console.error('加载商品失败', error)
    ElMessage.error('加载商品失败')
  }
}

// 监听用户登录状态变化，重新加载推荐
const refreshRecommendations = () => {
  // 重置加载状态，重新获取推荐
  hasLoaded.value = false
  recommendGoods.value = []
  loadRecommendations()
}

// 暴露刷新方法供父组件调用
defineExpose({ refreshRecommendations })

onMounted(() => {
  loadRecommendations()
  // 记录页面访问
  logClick('page_view', 'home', '首页访问', window.location.pathname)
})
</script>

<style scoped>
.home-container {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding-bottom: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.banner {
  margin: 12px 16px;
  border-radius: 20px;
  height: 160px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.banner-item {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  transition: transform 0.3s ease;
}

.quick-entry {
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 16px;
  margin: 12px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 10px 20px;
  border-radius: 16px;
}

.entry-item:hover {
  background: rgba(64, 158, 255, 0.08);
  transform: translateY(-3px);
}

.entry-icon {
  font-size: 32px;
}

.entry-item span {
  font-size: 13px;
  color: #5a6874;
  font-weight: 500;
}

.section {
  background: white;
  margin: 20px;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
}

.section-header h3 {
  font-size: 18px;
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}

.more {
  font-size: 13px;
  color: #409eff;
  cursor: pointer;
  transition: opacity 0.3s;
}

.more:hover {
  opacity: 0.7;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.goods-card {
  background: #f8f9fc;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.1);
  border-color: transparent;
}

.goods-img {
  height: 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: transform 0.3s ease;
}

.goods-card:hover .goods-img {
  transform: scale(1.02);
}

.goods-info {
  padding: 14px;
}

.goods-title {
  font-size: 13px;
  color: #2c3e50;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.goods-price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.goods-price::before {
  content: '¥';
  font-size: 12px;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}
</style>
