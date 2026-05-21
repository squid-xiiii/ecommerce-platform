<template>
  <div class="profile-container">
    <!-- 用户信息头部 -->
    <div class="profile-header" @click="goToUserInfo">
      <div class="avatar">
        <el-icon :size="50"><User /></el-icon>
      </div>
      <div class="user-info">
        <div class="user-name">{{ userStore.user?.nickName || userStore.user?.userName || '游客' }}</div>
        <div class="user-phone">{{ userStore.user?.phone || '绑定手机号' }}</div>
      </div>
      <div class="arrow">
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 订单状态栏 -->
    <div class="order-stats">
      <div class="stat-item" @click="goToOrders('all')">
        <div class="stat-num">{{ allOrdersCount }}</div>
        <div class="stat-label">全部订单</div>
      </div>
      <div class="stat-item" @click="goToOrders('pending')">
        <div class="stat-num">{{ pendingCount }}</div>
        <div class="stat-label">待发货</div>
      </div>
      <div class="stat-item" @click="goToOrders('shipped')">
        <div class="stat-num">{{ shippedCount }}</div>
        <div class="stat-label">待收货</div>
      </div>
      <div class="stat-item" @click="goToOrders('completed')">
        <div class="stat-num">{{ completedCount }}</div>
        <div class="stat-label">已完成</div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-list">
      <div class="menu-item" @click="goToOrders('all')">
        <div class="menu-left">
          <el-icon color="#409eff"><List /></el-icon>
          <span>我的订单</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="goToComments">
        <div class="menu-left">
          <el-icon color="#67c23a"><ChatDotRound /></el-icon>
          <span>我的评价</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="goToUserInfo">
        <div class="menu-left">
          <el-icon color="#e6a23c"><Setting /></el-icon>
          <span>账户设置</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="goToAddress">
        <div class="menu-left">
          <el-icon color="#f56c6c"><Location /></el-icon>
          <span>收货地址</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <div class="logout-btn" @click="handleLogout">
      <el-button type="danger" plain size="large" style="width: 100%">退出登录</el-button>
    </div>

    <!-- ========== 订单列表弹窗 ========== -->
    <el-drawer v-model="orderDrawerVisible" :title="orderDrawerTitle" direction="rtl" size="60%">
  <div class="order-list-drawer">
    <div v-if="filteredOrders.length === 0" class="empty">暂无订单</div>
    <div v-for="order in filteredOrders" :key="order.id" class="order-item-drawer">
      <div class="order-header">
        <span>订单号: {{ order.orderId }}</span>
        <el-tag :type="getStatusType(order.status)" size="small">{{ getStatusText(order.status) }}</el-tag>
      </div>
      <div class="order-goods">
        <div v-for="item in order.goodsList" :key="item.goodsId" class="order-goods-item">
          <span>{{ item.goodsName }}</span>
          <span>x{{ item.quantity }}</span>
          <span>¥{{ formatPrice(item.price * item.quantity) }}</span>
        </div>
      </div>
      <div class="order-footer">
        <div class="order-total">总计: ¥{{ formatPrice(order.totalAmount) }}</div>
        <div class="order-actions">
          <!-- 待收货状态显示确认收货按钮 -->
          <el-button
            v-if="order.status === 'SHIPPED'"
            type="success"
            size="small"
            @click="confirmReceipt(order)"
          >
            <el-icon><Select /></el-icon> 确认收货
          </el-button>
          <!-- 已完成订单显示评论按钮 -->
          <el-button
            v-if="order.status === 'COMPLETED'"
            type="primary"
            size="small"
            @click="showCommentDialog(order)"
          >
            <el-icon><ChatDotRound /></el-icon> 评价
          </el-button>
          <el-button type="danger" size="small" @click="deleteOrder(order.id)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</el-drawer>
    <!-- ========== 评论列表弹窗 ========== -->
    <el-drawer v-model="commentDrawerVisible" title="我的评价" direction="rtl" size="60%">
      <div class="comment-list-drawer">
        <div v-if="myComments.length === 0" class="empty">暂无评价</div>
        <div v-for="comment in myComments" :key="comment.id" class="comment-item-drawer">
          <div class="comment-header">
            <span class="comment-goods">{{comment.goodsName}}</span>
            <span class="comment-stars">{{ '★'.repeat(comment.star) }}{{ '☆'.repeat(5-comment.star) }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-labels">
            <el-tag v-for="label in comment.commentLabels" :key="label" size="small">{{ label }}</el-tag>
          </div>
          <div class="comment-time">{{ formatDate(comment.createdTime) }}</div>
          <div class="comment-actions">
            <el-button type="danger" size="small" @click="deleteComment(comment.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- ========== 发表评论弹窗 ========== -->
    <el-drawer v-model="commentDialogVisible" title="发表评价" direction="rtl" size="60%">
      <div class="comment-form">
        <div class="comment-goods-info">
          <div class="comment-goods-name">{{ currentOrderGoodsName }}</div>
        </div>
        <el-form :model="commentForm" label-width="70px">
          <el-form-item label="评分">
            <el-rate v-model="commentForm.star" :texts="['很差', '较差', '一般', '较好', '很好']" show-text />
          </el-form-item>
          <el-form-item label="内容">
            <el-input v-model="commentForm.content" type="textarea" rows="4" placeholder="请写下您的评价" />
          </el-form-item>
          <el-form-item label="标签">
            <el-select v-model="commentForm.labels" multiple placeholder="选择标签" style="width: 100%">
              <el-option label="性价比高" value="性价比高" />
              <el-option label="物流快" value="物流快" />
              <el-option label="质量好" value="质量好" />
              <el-option label="服务好" value="服务好" />
              <el-option label="外观漂亮" value="外观漂亮" />
              <el-option label="功能强大" value="功能强大" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitComment" style="width: 100%">提交评价</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>

    <!-- ========== 地址管理弹窗 ========== -->
    <el-drawer v-model="addressDrawerVisible" title="收货地址" direction="rtl" size="60%">
      <div class="address-list">
        <div class="address-item">
          <div class="address-name">{{ userStore.user?.name || userStore.user?.nickName }}</div>
          <div class="address-phone">{{ userStore.user?.phone }}</div>
          <div class="address-detail">{{ userStore.user?.address || '请完善地址信息' }}</div>
          <el-button type="primary" size="small" plain @click="editAddress">编辑地址</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- ========== 个人信息编辑弹窗 ========== -->
    <el-drawer v-model="userInfoDrawerVisible" title="个人信息" direction="rtl" size="60%">
      <div class="user-info-form">
        <el-form :model="userInfoForm" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="userInfoForm.userName" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="userInfoForm.nickName" />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="userInfoForm.name" />
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number v-model="userInfoForm.age" :min="1" :max="120" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="userInfoForm.sex">
              <el-radio value="男">男</el-radio>
              <el-radio value="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="学校">
            <el-input v-model="userInfoForm.school" />
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="userInfoForm.codeNum" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userInfoForm.phone" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="userInfoForm.address" type="textarea" rows="2" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveUserInfo" style="width: 100%">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>

    <!-- 编辑地址弹窗 -->
    <el-dialog v-model="addressEditVisible" title="编辑地址" width="50%">
      <el-form :model="addressForm" label-width="70px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addressForm.address" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressEditVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, ArrowRight, List, ChatDotRound, Setting, Location, Delete
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { orderApi, commentApi, userApi, goodsApi } from '@/api'

const router = useRouter()
const userStore = useUserStore()

// 订单相关
const allOrders = ref([])
const orderDrawerVisible = ref(false)
const orderDrawerTitle = ref('全部订单')
const currentOrderFilter = ref('all')
const goods=ref('null')

const confirmReceipt = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '确认收货', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    })
    await orderApi.updateStatus(order.id, 'COMPLETED')
    ElMessage.success('收货成功，可以评价了')
    loadOrders()  // 刷新订单列表
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 评论相关
const myComments = ref([])
const commentDrawerVisible = ref(false)
const commentDialogVisible = ref(false)
const currentOrderGoodsName = ref('')
const currentOrderGoodsId = ref(null)

const commentForm = ref({
  star: 5,
  content: '',
  labels: []
})

// 用户信息相关
const userInfoDrawerVisible = ref(false)
const userInfoForm = ref({
  id: '',
  userName: '',
  nickName: '',
  name: '',
  age: null,
  sex: '',
  school: '',
  codeNum: '',
  phone: '',
  address: ''
})

// 地址相关
const addressDrawerVisible = ref(false)
const addressEditVisible = ref(false)
const addressForm = ref({
  name: '',
  phone: '',
  address: ''
})

// 修复价格格式化（单位：分 -> 元）
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

// 计算订单数量
const allOrdersCount = computed(() => allOrders.value.length)
const pendingCount = computed(() => allOrders.value.filter(o => o.status === 'PAID').length)
const shippedCount = computed(() => allOrders.value.filter(o => o.status === 'SHIPPED').length)
const completedCount = computed(() => allOrders.value.filter(o => o.status === 'COMPLETED').length)

const filteredOrders = computed(() => {
  if (currentOrderFilter.value === 'all') return allOrders.value
  if (currentOrderFilter.value === 'pending') return allOrders.value.filter(o => o.status === 'PAID')
  if (currentOrderFilter.value === 'shipped') return allOrders.value.filter(o => o.status === 'SHIPPED')
  if (currentOrderFilter.value === 'completed') return allOrders.value.filter(o => o.status === 'COMPLETED')
  return allOrders.value
})

// 加载订单
const loadOrders = async () => {
  if (!userStore.user) return
  try {
    allOrders.value = await orderApi.getUserOrders(userStore.user.userName)
  } catch (error) {
    console.error('加载订单失败', error)
  }
}

// 加载评论
const loadComments = async () => {
  if (!userStore.user) return
  try {
    myComments.value = await commentApi.getUserComments(userStore.user.userName)
  } catch (error) {
    console.error('加载评论失败', error)
    myComments.value = []
  }
}

// 显示评论弹窗
const showCommentDialog = async (order) => {
  if (order.goodsList && order.goodsList.length > 0) {
    currentOrderGoodsName.value = order.goodsList[0].goodsName
    currentOrderGoodsId.value = order.goodsList[0].goodsId
  }
  commentForm.value = { star: 5, content: '', labels: [] }
  commentDialogVisible.value = true
}

// 提交评论
const submitComment = async () => {
  if (!userStore.user) return
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }
  try {
    await commentApi.addComment({
      userName: userStore.user.userName,
      goodsId: currentOrderGoodsId.value,
      goodsName: goods.value.goodsInfo,
      content: commentForm.value.content,
      star: commentForm.value.star,
      commentLabels: commentForm.value.labels,
      createdTime: new Date()
    })
    ElMessage.success('评价成功')
    commentDialogVisible.value = false
    loadComments()
  } catch (error) {
    ElMessage.error('评价失败')
  }
}

// 删除订单
const deleteOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await orderApi.deleteOrder(orderId)
    ElMessage.success('删除成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 删除评论
const deleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await commentApi.deleteComment(commentId)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 跳转订单页面
const goToOrders = (filter) => {
  currentOrderFilter.value = filter
  switch(filter) {
    case 'all': orderDrawerTitle.value = '全部订单'; break
    case 'pending': orderDrawerTitle.value = '待发货订单'; break
    case 'shipped': orderDrawerTitle.value = '待收货订单'; break
    case 'completed': orderDrawerTitle.value = '已完成订单'; break
  }
  orderDrawerVisible.value = true
}

// 跳转评论页面
const goToComments = () => {
  loadComments()
  commentDrawerVisible.value = true
}

// 跳转用户信息
const goToUserInfo = () => {
  if (userStore.user) {
    userInfoForm.value = { ...userStore.user }
  }
  userInfoDrawerVisible.value = true
}

// 保存用户信息
const saveUserInfo = async () => {
  try {
    const updated = await userApi.updateUser(userInfoForm.value.id, userInfoForm.value)
    userStore.user = updated
    localStorage.setItem('user', JSON.stringify(updated))
    ElMessage.success('保存成功')
    userInfoDrawerVisible.value = false
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 跳转地址
const goToAddress = () => {
  addressForm.value = {
    name: userStore.user?.name || '',
    phone: userStore.user?.phone || '',
    address: userStore.user?.address || ''
  }
  addressDrawerVisible.value = true
}

// 编辑地址
const editAddress = () => {
  addressEditVisible.value = true
}

// 保存地址
const saveAddress = async () => {
  if (!userStore.user) return
  try {
    const updated = await userApi.updateUser(userStore.user.id, {
      ...userStore.user,
      name: addressForm.value.name,
      phone: addressForm.value.phone,
      address: addressForm.value.address
    })
    userStore.user = updated
    localStorage.setItem('user', JSON.stringify(updated))
    ElMessage.success('地址更新成功')
    addressEditVisible.value = false
    addressDrawerVisible.value = false
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {}
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.profile-container {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 20px;
}

/* 用户信息头部 */
.profile-header {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  padding: 40px 30px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
  cursor: pointer;
}

.avatar {
  width: 60px;
  height: 60px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 12px;
  opacity: 0.8;
}

.arrow {
  font-size: 20px;
}

/* 订单状态栏 */
.order-stats {
  background: white;
  display: flex;
  padding: 20px 0;
  margin-bottom: 10px;
}

.stat-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
}

.stat-num {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 功能菜单 */
.menu-list {
  background: white;
  margin-bottom: 10px;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 30px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-left span {
  font-size: 14px;
  color: #333;
}

/* 退出按钮 */
.logout-btn {
  padding: 0 20px;
  margin-top: 20px;
}

/* 弹窗内样式 */
.order-list-drawer, .comment-list-drawer, .address-list, .comment-form, .user-info-form {
  padding: 10px;
}

.order-item-drawer, .comment-item-drawer, .address-item {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #666;
}

.order-goods {
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}

.order-goods-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
}

.order-total {
  font-weight: bold;
  color: #f56c6c;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  color: #6cb1f5;
  margin-bottom: 8px;
}

.comment-stars {
  color: #ffc107;
  font-size: 14px;
  padding-bottom:10px ;
}

.comment-content {
  font-size: 16px;
  color: #333;
  margin-bottom: 6px;
  padding-bottom: 5px;
}

.comment-labels {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.comment-time {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.comment-actions {
  text-align: right;
}

.comment-goods-info {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
}

.comment-goods-name {
  font-size: 14px;
  color: #333;
}

.address-item {
  background: white;
  border: 1px solid #eee;
}

.address-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.address-phone {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.address-detail {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>

