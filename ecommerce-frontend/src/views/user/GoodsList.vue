<template>
  <div class="container">
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 500px" clearable @clear="loadGoods" @keyup.enter="loadGoods">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="category" placeholder="商品分类" clearable @change="loadGoods">
        <el-option label="笔记本电脑" value="笔记本电脑" />
        <el-option label="手机" value="手机" />
        <el-option label="配件" value="配件" />
      </el-select>
    </div>

    <div class="goods-grid">
      <div v-for="goods in pagedGoodsList" :key="goods.goodsId" class="goods-card" @click="goToDetail(goods.goodsId)">
        <div class="goods-card-img">
          <el-icon :size="48"><Goods /></el-icon>
        </div>
        <div class="goods-card-info">
          <div class="goods-card-title">{{ goods.goodsInfo }}</div>
          <div class="goods-card-price">{{ formatPrice(goods.price) }}</div>
          <el-button type="primary" size="small" @click.stop="addToCart(goods)">加入购物车</el-button>
        </div>
      </div>
    </div>

    <div v-if="goodsList.length === 0" class="empty">暂无商品</div>

    <div v-if="isQueried && goodsList.length > pageSize" class="pagination-wrapper" style="text-align:center;margin-top:16px;">
      <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="goodsList.length" layout="prev, pager, next" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Goods } from '@element-plus/icons-vue'
import { goodsApi, clickApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { cartApi } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const allGoods = ref([])
const keyword = ref('')
const category = ref('')

const pageSize = 9
const currentPage = ref(1)
const isQueried = computed(() => {
  return (keyword.value && keyword.value.trim()) || category.value
})

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

const goodsList = computed(() => {
  let result = allGoods.value
  if (category.value) {
    result = result.filter(g => g.category === category.value)
  }
  if (keyword.value && keyword.value.trim()) {
    const searchTerm = keyword.value.trim().toLowerCase()
    result = result.filter(g =>
      g.goodsInfo.toLowerCase().includes(searchTerm) ||
      (g.specificationsInfo && g.specificationsInfo.toLowerCase().includes(searchTerm))
    )
  }
  return result
})

const pagedGoodsList = computed(() => {
  if (!isQueried.value) return goodsList.value
  const start = (currentPage.value - 1) * pageSize
  return goodsList.value.slice(start, start + pageSize)
})

const formatPrice = (price) => {
  return (price / 100).toFixed(2)
}

const loadGoods = async () => {
  try {
    allGoods.value = await goodsApi.getList()
    currentPage.value = 1
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const handleCategoryChange = (val) => {
  logClick('category_filter', val, `筛选分类:${val}`, route.path)
  currentPage.value = 1
}

const handleSearch = () => {
  logClick('search', keyword.value, `搜索关键词:${keyword.value}`, route.path)
  currentPage.value = 1
}

const goToDetail = (id, goodsInfo) => {
  logClick('goods_detail', id.toString(), goodsInfo, `/user/goods/${id}`)
  router.push(`/user/goods/${id}`)
}

const addToCart = async (goods) => {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await cartApi.addToCart(userStore.user.userName, goods.goodsId, 1)
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败', error)
    ElMessage.error('加入购物车失败')
  }
}

onMounted(() => {
  loadGoods()
  if (route.query.category) {
    currentCategory.value = route.query.category
  }
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
  logClick('page_view', 'goods_list', '商品列表页访问', route.path)
})
</script>

<style scoped>
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}
.filter-bar {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
}
.goods-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}
.goods-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    transition: transform 0.3s;
    cursor: pointer;
}
.goods-card:hover {
    transform: translateY(-4px);
}
.goods-card-img {
    height: 200px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
}
.goods-card-info {
    padding: 16px;
}
.goods-card-title {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.goods-card-price {
    color: #f56c6c;
    margin-bottom: 8px;
    font-size: 20px;
    font-weight: bold;
}
.goods-card-price::before {
    content: '¥';
    font-size: 14px;
}
.goods-card-stock {
    font-size: 12px;
    color: #999;
    margin: 8px 0;
}
.empty {
    text-align: center;
    padding: 60px;
    color: #999;
}
</style>
