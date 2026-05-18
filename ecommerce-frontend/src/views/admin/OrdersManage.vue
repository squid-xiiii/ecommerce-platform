<template>
  <div class="orders-manage">
    <h2 class="page-title">订单管理</h2>

    <el-tabs v-model="activeTab">
      <!-- 未完成订单（PAID 和 SHIPPED） -->
      <el-tab-pane label="未完成订单" name="pending">
        <el-table :data="pendingOrders" stripe style="width: 100%">
          <el-table-column prop="orderId" label="订单号" width="150" />
          <el-table-column prop="userName" label="用户" width="120" />
          <el-table-column prop="totalAmount" label="总金额" width="120">
            <template #default="{ row }">¥{{ (formatPrice(row.totalAmount)/100).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="商品" min-width="200">
            <template #default="{ row }">
              <div v-for="item in row.goodsList" :key="item.goodsId" class="order-goods-item">
                {{ item.goodsName }} x{{ item.quantity }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdDate" label="下单时间" width="160">
            <template #default="{ row }">{{ formatDate(row.createdDate) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <!-- 只有待发货状态才显示发货按钮 -->
              <el-button
                v-if="row.status === 'PAID'"
                type="primary"
                size="small"
                @click="shipOrder(row)"
              >
                发货
              </el-button>
              <span v-else class="no-action">-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 已完成订单 -->
      <el-tab-pane label="已完成订单" name="completed">
        <el-table :data="completedOrders" stripe style="width: 100%">
          <el-table-column prop="orderId" label="订单号" width="150" />
          <el-table-column prop="userName" label="用户" width="120" />
          <el-table-column prop="totalAmount" label="总金额" width="120">
            <template #default="{ row }">¥{{ (formatPrice(row.totalAmount)/100).toFixed(2) }}</template>
          </el-table-column>
          <!-- 显示商品列表 -->
          <el-table-column label="商品" min-width="200">
            <template #default="{ row }">
              <div v-if="row.goodsList">
                <div v-for="item in parseGoodsList(row.goodsList)" :key="item.goodsId" class="order-goods-item">
                  {{ item.goodsName }} x{{ item.quantity }}
                </div>
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success">已完成</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="completedDate" label="完成时间" width="160">
            <template #default="{ row }">{{ formatDate(row.completedDate) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminOrderApi } from '@/api'

const activeTab = ref('pending')
const allOrders = ref([])

// 未完成订单：PAID 和 SHIPPED（排除 COMPLETED）
const pendingOrders = computed(() => {
  return allOrders.value.filter(order =>
    order.status === 'PAID' || order.status === 'SHIPPED'
  )
})

// 已完成订单：通过 API 从 MySQL 获取
const completedOrders = ref([])

const formatPrice = (price) => {
  if (!price && price !== 0) return '0.00'
  return (price / 100).toFixed(2)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const getStatusText = (status) => {
  const map = { 'PAID': '待发货', 'SHIPPED': '待收货', 'COMPLETED': '已完成' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 'PAID': 'warning', 'SHIPPED': 'primary', 'COMPLETED': 'success' }
  return map[status] || 'info'
}

const parseGoodsList = (goodsList) => {
  if (typeof goodsList === 'string') {
    try {
      return JSON.parse(goodsList)
    } catch (e) {
      return []
    }
  }
  return goodsList || []
}


// 发货操作：将状态从 PAID 改为 SHIPPED
const shipOrder = async (order) => {
  try {
    await ElMessageBox.confirm(`确定要将订单 ${order.orderId} 发货吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await adminOrderApi.updateStatus(order.id, 'SHIPPED')
    ElMessage.success('发货成功')
    loadAllOrders()  // 刷新订单列表
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const loadAllOrders = async () => {
  try {
    allOrders.value = await adminOrderApi.getList()
  } catch (error) {
    console.error('加载订单失败', error)
    ElMessage.error('加载订单失败')
  }
}

const loadCompletedOrders = async () => {
  try {
    completedOrders.value = await adminOrderApi.getCompleted()
  } catch (error) {
    console.error('加载已完成订单失败', error)
    ElMessage.error('加载已完成订单失败')
  }
}

onMounted(() => {
  loadAllOrders()
  loadCompletedOrders()
})
</script>

<style scoped>
.orders-manage {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.page-title {
  font-size: 18px;
  margin: 0 0 20px 0;
  color: #333;
}

.order-goods-item {
  font-size: 12px;
  color: #666;
  margin: 2px 0;
}

.no-action {
  color: #ccc;
}
</style>
