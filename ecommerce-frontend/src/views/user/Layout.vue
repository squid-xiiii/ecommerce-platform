<template>
  <div class="user-layout">
    <div class="header">
      <div class="header-content">
        <div class="logo" @click="goTo('/user')">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">E-Store</span>
        </div>
        <div class="nav-menu">
          <div class="nav-item" :class="{ active: activeMenu === 'home' }" @click="goTo('/user')">
            <span>首页</span>
            <div class="nav-indicator"></div>
          </div>
          <div class="nav-item" :class="{ active: activeMenu === 'goods' }" @click="goTo('/user/goods')">
            <span>商品</span>
            <div class="nav-indicator"></div>
          </div>
          <div class="nav-item" :class="{ active: activeMenu === 'cart' }" @click="goTo('/user/cart')">
            <span>购物车</span>
            <div class="nav-indicator"></div>
          </div>
          <div class="nav-item" :class="{ active: activeMenu === 'profile' }" @click="goTo('/user/profile')">
            <span>我的</span>
            <div class="nav-indicator"></div>
          </div>
        </div>
        <div class="user-info">
          <!-- 消息通知铃铛 -->
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
            <div class="message-icon" @click="openMessages">
              <el-icon :size="22"><Bell /></el-icon>
            </div>
          </el-badge>

          <!-- 购物车图标（保留但放在消息铃铛后面） -->
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <div class="cart-icon" @click="goTo('/user/cart')">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </div>
          </el-badge>

          <el-dropdown trigger="click" @command="handleDropdown">
            <div class="user-avatar">
              <el-icon :size="20"><User /></el-icon>
              <span class="user-name">{{ userStore.user?.nickName || userStore.user?.userName }}</span>
              <el-icon :size="14"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="messages">消息通知</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    <div class="main">
      <router-view />
    </div>

    <!-- 消息通知弹窗 -->
    <el-drawer v-model="messageDrawerVisible" title="消息通知" direction="rtl" size="50%">
      <div v-if="userMessages.length === 0" class="empty">暂无消息</div>
      <div v-for="msg in userMessages" :key="msg.adId" class="message-item" @click="markAsRead(msg)">
        <div class="message-content">{{ msg.content }}</div>
        <div class="message-time">{{ formatDate(msg.sentTime) }}</div>
        <div class="message-status" :class="msg.status">{{ msg.status === 'read' ? '已读' : '未读' }}</div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, User, ArrowDown, Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { adminUserApi } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeMenu = ref('home')
const cartCount = ref(0)
const unreadCount = ref(0)
const userMessages = ref([])
const messageDrawerVisible = ref(false)

watch(() => route.path, (path) => {
  if (path === '/user') activeMenu.value = 'home'
  else if (path === '/user/goods') activeMenu.value = 'goods'
  else if (path === '/user/cart') activeMenu.value = 'cart'
  else if (path === '/user/profile') activeMenu.value = 'profile'
}, { immediate: true })

const updateCartCount = () => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  cartCount.value = cart.reduce((sum, item) => sum + item.quantity, 0)
}

// 加载用户消息
const loadUserMessages = async () => {
  if (!userStore.user) return
  try {
    const messages = await adminUserApi.getUserMessages(userStore.user.userName)
    userMessages.value = messages || []
    unreadCount.value = userMessages.value.filter(m => m.status !== 'read').length
  } catch (error) {
    console.error('加载消息失败', error)
  }
}

// 打开消息弹窗
const openMessages = () => {
  messageDrawerVisible.value = true
}

// 标记消息为已读
const markAsRead = async (msg) => {
  if (msg.status === 'read') return
  try {
    await adminUserApi.markMessageRead(userStore.user.userName, msg.adId)
    msg.status = 'read'
    unreadCount.value = userMessages.value.filter(m => m.status !== 'read').length
  } catch (error) {
    console.error('标记失败', error)
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

onMounted(() => {
  updateCartCount()
  window.addEventListener('storage', updateCartCount)
  loadUserMessages()
  // 定时刷新消息
  setInterval(() => {
    if (userStore.user) {
      loadUserMessages()
    }
  }, 30000)
})

const goTo = (path) => router.push(path)

const handleDropdown = (command) => {
  if (command === 'profile') {
    router.push('/user/profile')
  } else if (command === 'messages') {
    openMessages()
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #409eff 0%, #36cea0 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.nav-menu {
  display: flex;
  gap: 8px;
  font-size: 18px;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 40px;
}

.nav-item {
  position: relative;
  cursor: pointer;
  padding: 8px 28px;
  border-radius: 32px;
  transition: all 0.3s ease;
  color: #5a6874;
  font-weight: 500;
}

.nav-item:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.nav-item.active {
  background: #409eff;
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.nav-indicator {
  display: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.message-badge :deep(.el-badge__content),
.cart-badge :deep(.el-badge__content) {
  background: #f56c6c;
  border: none;
  font-size: 10px;
  height: 16px;
  line-height: 16px;
  padding: 0 5px;
}

.message-icon, .cart-icon {
  cursor: pointer;
  color: #5a6874;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
}

.message-icon:hover, .cart-icon:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 40px;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background: #e8eaed;
}

.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.main {
  min-height: calc(100vh - 70px);
}

/* 消息弹窗样式 */
.message-item {
  padding: 16px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: background 0.3s;
}

.message-item:hover {
  background: #f8f9fc;
}

.message-content {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.message-status {
  font-size: 11px;
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
}

.message-status.sent {
  background: #e6f7ff;
  color: #1890ff;
}

.message-status.read {
  background: #f6ffed;
  color: #52c41a;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
