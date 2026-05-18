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
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
  padding-bottom: 10px;
  max-width: 1200px;
  margin: 0 auto;
}

.banner {
  margin: 12px 16px;
  border-radius: 12px;
  overflow: hidden;
}

.banner-item {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
}

.quick-entry {
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 16px;
  margin: 12px 16px;
  border-radius: 12px;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.entry-icon {
  font-size: 28px;
}

.entry-item span {
  font-size: 12px;
  color: #666;
}

.section {
  background: white;
  margin: 12px 16px;
  border-radius: 12px;
  padding: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.section-header h3 {
  font-size: 16px;
  margin: 0;
  color: #333;
}

.more {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.goods-card {
  background: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}

.goods-card:active {
  transform: scale(0.98);
}

.goods-img {
  height: 220px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.goods-info {
  padding: 8px;
}

.goods-title {
  font-size: 12px;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-price {
  color: #f56c6c;
  font-size: 14px;
  font-weight: bold;
}
</style>
