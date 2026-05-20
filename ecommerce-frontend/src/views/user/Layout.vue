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
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, User, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeMenu = ref('home')
const cartCount = ref(0)

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

onMounted(() => {
  updateCartCount()
  window.addEventListener('storage', updateCartCount)
})

const goTo = (path) => router.push(path)

const handleDropdown = (command) => {
  if (command === 'profile') {
    router.push('/user/profile')
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

.cart-badge :deep(.el-badge__content) {
  background: #f56c6c;
  border: none;
  font-size: 10px;
  height: 16px;
  line-height: 16px;
  padding: 0 5px;
}

.cart-icon {
  cursor: pointer;
  color: #5a6874;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
}

.cart-icon:hover {
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
</style>
