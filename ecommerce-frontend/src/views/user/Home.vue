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

    <!-- 热门推荐 -->
    <div class="section">
      <div class="section-header">
        <h3>热门推荐</h3>
        <span class="more" @click="handleEntryClick('更多商品')">更多 ></span>
      </div>
      <div class="goods-grid">
        <div v-for="goods in hotRecommendGoods" :key="goods.goodsId" class="goods-card" @click="handleGoodsClick(goods.goodsId, goods.goodsInfo)">
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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Goods } from '@element-plus/icons-vue'
import { goodsApi, clickApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { cartApi } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const allGoods = ref([])

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
const handleEntryClick = (entryName) => {
  logClick('quick_entry', entryName, entryName, '/')
  if (entryName === '热门推荐') {
    router.push('/user/goods')
  } else if (entryName === '更多商品') {
    router.push('/user/goods')
  } else {
    router.push(`/user/goods?category=${entryName}`)
  }
}

// 商品点击
const handleGoodsClick = (goodsId, goodsName) => {
  logClick('goods_card', goodsId.toString(), goodsName, `/user/goods/${goodsId}`)
  router.push(`/user/goods/${goodsId}`)
}

// 热门推荐：各类别各2个
const hotRecommendGoods = computed(() => {
  const categories = ['笔记本电脑', '手机', '配件']
  const result = []
  categories.forEach(category => {
    const categoryGoods = allGoods.value.filter(g => g.category === category)
    result.push(...categoryGoods.slice(0, 2))
  })
  return result
})

const formatPrice = (price) => {
  return (price / 100).toFixed(2)
}

const goToGoods = () => {
  logClick('navigation', '商品', '商品列表页', '/user/goods')
  router.push('/user/goods')
}
const goToCategory = (category) => router.push(`/user/goods?category=${category}`)
const goToDetail = (id) => router.push(`/user/goods/${id}`)

const loadGoods = async () => {
  try {
    allGoods.value = await goodsApi.getList()
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

onMounted(() => {
  loadGoods()
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

/* 修改为3列布局 */
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
</style>
