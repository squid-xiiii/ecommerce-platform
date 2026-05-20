<template>
  <div class="cart-container">
    <div class="cart-header">
      <h2 class="cart-title">购物车</h2>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>

    <div v-else-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车空空如也">
        <el-button type="primary" @click="router.push('/user/goods')">去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else>
      <div class="select-all-bar">
        <el-checkbox v-model="isAllSelected" @change="selectAll">全选</el-checkbox>
        <span class="selected-count">已选 {{ selectedCount }} 件商品</span>
      </div>

      <div class="cart-list">
        <div v-for="(item, index) in cartItems" :key="item.goodsId" class="cart-item">
          <el-checkbox v-model="item.selected" @change="handleCheckChange" class="item-checkbox" />

          <div class="item-info">
            <div class="item-img">
              <el-icon :size="40"><Goods /></el-icon>
            </div>
            <div class="item-details">
              <div class="item-name">{{ item.goodsName }}</div>
              <div class="item-price">¥{{ formatPrice(item.price) }}</div>
            </div>
          </div>

          <div class="item-controls">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              size="small"
              @change="updateQuantity(item, item.quantity)"
            />
            <div class="item-subtotal">¥{{ formatPrice(item.price * item.quantity) }}</div>
            <el-button type="danger" :icon="Delete" circle size="small" @click="removeItem(index)" />
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="total-info">
          <span>合计: </span>
          <span class="total-price">¥{{ formatPrice(selectedTotalPrice) }}</span>
          <span class="total-count">(已选 {{ selectedCount }} 件商品)</span>
        </div>
        <el-button type="danger" size="large" class="checkout-btn" @click="checkoutSelected" :disabled="selectedCount === 0">
          去结算 ({{ selectedCount }})
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Goods, Loading } from '@element-plus/icons-vue'
import { orderApi, cartApi, goodsApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const cartItems = ref([])
const loading = ref(false)

// 全选状态
const isAllSelected = ref(false)

// 计算选中的商品数量
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

// 计算选中商品的总价
const selectedTotalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const formatPrice = (price) => {
  return (price / 100).toFixed(2)
}

// 从后端加载购物车
const loadCart = async () => {
  if (!userStore.user) {
    cartItems.value = []
    return
  }

  loading.value = true
  try {
    const cart = await cartApi.getCart(userStore.user.userName)
    if (cart && cart.items && cart.items.length > 0) {
      cartItems.value = cart.items.map(item => ({
        ...item,
        selected: true
      }))
    } else {
      cartItems.value = []
    }
    updateSelectAllState()
  } catch (error) {
    console.error('加载购物车失败', error)
    cartItems.value = []
  } finally {
    loading.value = false
  }
}

// 更新全选状态
const updateSelectAllState = () => {
  if (cartItems.value.length === 0) {
    isAllSelected.value = false
    return
  }
  isAllSelected.value = cartItems.value.every(item => item.selected)
}

// 全选/全不选
const selectAll = (val) => {
  cartItems.value.forEach(item => {
    item.selected = val
  })
}

// 单项选择变化
const handleCheckChange = () => {
  updateSelectAllState()
}

// 更新商品数量
const updateQuantity = async (item, newQuantity) => {
  if (!userStore.user) return
  if (newQuantity < 1) return

  item.quantity = newQuantity
  try {
    await cartApi.updateCartItem(userStore.user.userName, item.goodsId, newQuantity)
    // 更新本地 selectedTotalPrice 会自动重新计算
  } catch (error) {
    ElMessage.error('更新数量失败')
    await loadCart() // 重新加载恢复数据
  }
}

// 删除商品
const removeItem = async (index) => {
  const item = cartItems.value[index]
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartApi.removeCartItem(userStore.user.userName, item.goodsId)
    cartItems.value.splice(index, 1)
    updateSelectAllState()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 结算选中的商品
const checkoutSelected = async () => {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  // 检查库存
  for (const item of selectedItems) {
    try {
      const goods = await goodsApi.getDetail(item.goodsId)
      if (goods.stock < item.quantity) {
        ElMessage.error(`${item.goodsName} 库存不足，当前库存: ${goods.stock}`)
        return
      }
    } catch (error) {
      ElMessage.error('检查库存失败')
      return
    }
  }

  try {
    const order = {
      orderId: Date.now(),
      userName: userStore.user.userName,
      goodsList: selectedItems.map(item => ({
        goodsId: item.goodsId,
        goodsName: item.goodsName,
        quantity: item.quantity,
        price: item.price
      })),
      // selectedTotalPrice 已为分（price 的单位为分），直接传递
      totalAmount: Math.round(selectedTotalPrice.value),
      status: 'PAID',
      createdDate: new Date()
    }

    await orderApi.createOrder(order)

    await loadCart()

    ElMessage.success('下单成功')
    router.push('/user/profile')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '下单失败')
  }
}

// 暴露方法给父组件
defineExpose({ loadCart })

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-container {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 80px;
}

.cart-header {
  background: white;
  padding: 15px 16px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.cart-title {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.select-all-bar {
  background: white;
  padding: 12px 16px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f5f5f5;
}

.selected-count {
  font-size: 12px;
  color: #999;
}

.cart-list {
  padding: 12px;
}

.cart-item {
  background: white;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-checkbox {
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  gap: 12px;
}

.item-img {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.item-details {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.item-price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.item-price::before {
  content: '¥';
  font-size: 12px;
}

.item-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.item-subtotal {
  color: #f56c6c;
  font-weight: bold;
  min-width: 70px;
  text-align: right;
}

.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #eee;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.total-info {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.total-price::before {
  content: '¥';
  font-size: 14px;
}

.total-count {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.checkout-btn {
  background: #f56c6c;
  border-color: #f56c6c;
}

.checkout-btn:disabled {
  background: #ccc;
  border-color: #ccc;
}

.empty-cart {
  padding: 60px 20px;
  text-align: center;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #999;
}
</style>
