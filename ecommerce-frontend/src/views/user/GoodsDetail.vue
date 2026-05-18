<template>
  <div class="container" v-if="goods">
    <div class="detail-card">
      <div class="detail-img">
        <el-icon :size="120"><Goods /></el-icon>
      </div>
      <div class="detail-info">
        <h1 class="detail-title">{{ goods.goodsInfo }}</h1>
        <p class="detail-spec">{{ goods.specificationsInfo }}</p>
        <div class="detail-price">{{ formatPrice(goods.price) }}</div>
        <div class="detail-stock">库存: {{ goods.stock }}</div>
        <div class="detail-actions">
          <el-input-number v-model="quantity" :min="1" :max="goods.stock" style="width: 120px" />
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
          <el-button type="success" size="large" @click="buyNow">立即购买</el-button>
        </div>
      </div>
    </div>

    <div class="comments-section">
      <h3>商品评价</h3>
      <div v-if="comments.length === 0" class="empty">暂无评价</div>
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <span class="comment-user">{{ comment.userName }}</span>
          <span class="comment-stars">{{ '★'.repeat(comment.star) }}{{ '☆'.repeat(5-comment.star) }}</span>
          <span class="comment-time">{{ formatDate(comment.createdTime) }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-labels">
          <el-tag v-for="label in comment.commentLabels" :key="label" size="small">{{ label }}</el-tag>
        </div>
      </div>

      <el-divider />


    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Goods } from '@element-plus/icons-vue'
import { goodsApi, commentApi, orderApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { cartApi } from '@/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goods = ref(null)
const comments = ref([])
const quantity = ref(1)

const commentForm = ref({
    star: 5,
    content: '',
    labels: []
})

const formatPrice = (price) => {
    return (price / 100).toFixed(2)
}

const formatDate = (date) => {
    if (!date) return ''
    return new Date(date).toLocaleDateString()
}

const addToCart = async () => {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await cartApi.addToCart(userStore.user.userName, goods.value.goodsId, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败', error)
    ElMessage.error('加入购物车失败')
  }
}

const buyNow = async () => {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const order = {
      orderId: Date.now(),
      userName: userStore.user.userName,
      goodsList: [{
        goodsId: goods.value.goodsId,
        goodsName: goods.value.goodsInfo,
        quantity: quantity.value,
        price: goods.value.price
      }],
      totalAmount: goods.value.price * quantity.value,
      status: 'PAID',
      createdDate: new Date()
    }

    await orderApi.createOrder(order)
    ElMessage.success('下单成功，请到订单页面查看')  // 跳转到订单页面
  } catch (error) {
    console.error('下单失败', error)
    ElMessage.error('下单失败')
  }
}

const loadGoods = async () => {
    try {
        goods.value = await goodsApi.getDetail(parseInt(route.params.id))
    } catch (error) {
        ElMessage.error('加载商品失败')
    }
}

const loadComments = async () => {
    try {
        comments.value = await commentApi.getGoodsComments(parseInt(route.params.id))
    } catch (error) {
        comments.value = []
    }
}

onMounted(() => {
    loadGoods()
    loadComments()
})
</script>

<style scoped>
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}
.detail-card {
    display: flex;
    gap: 40px;
    background: white;
    border-radius: 12px;
    padding: 30px;
    margin-bottom: 30px;
}
.detail-img {
    flex-shrink: 0;
    width: 400px;
    height: 400px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
}
.detail-info {
    flex: 1;
}
.detail-title {
    font-size: 24px;
    margin-bottom: 12px;
}
.detail-spec {
    color: #666;
    margin-bottom: 20px;
    line-height: 1.6;
}
.detail-price {
    font-size: 32px;
    color: #f56c6c;
    font-weight: bold;
    margin: 20px 0;
}
.detail-price::before {
    content: '¥';
    font-size: 20px;
}
.detail-stock {
    color: #999;
    margin-bottom: 20px;
}
.detail-actions {
    display: flex;
    gap: 16px;
    align-items: center;
}
.comments-section {
    background: white;
    border-radius: 12px;
    padding: 30px;
}
.comment-item {
    border-bottom: 1px solid #eee;
    padding: 16px 0;
}
.comment-header {
    display: flex;
    gap: 16px;
    margin-bottom: 8px;
}
.comment-user {
    font-weight: bold;
}
.comment-stars {
    color: #ffc107;
}
.comment-time {
    color: #999;
    font-size: 12px;
}
.comment-content {
    margin: 8px 0;
}
.comment-labels {
    display: flex;
    gap: 8px;
}
.empty {
    text-align: center;
    padding: 30px;
    color: #999;
}
</style>
