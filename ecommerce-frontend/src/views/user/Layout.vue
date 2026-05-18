<template>
  <div class="user-layout">
    <div class="header">
      <div class="header-content">
        <div class="logo" @click="goTo('/user')">电商平台</div>
        <div class="nav-menu">
          <div class="nav-item" :class="{ active: activeMenu === 'home' }" @click="goTo('/user')">首页</div>
          <div class="nav-item" :class="{ active: activeMenu === 'goods' }" @click="goTo('/user/goods')">商品</div>
          <div class="nav-item" :class="{ active: activeMenu === 'cart' }" @click="goTo('/user/cart')">购物车</div>
          <div class="nav-item" :class="{ active: activeMenu === 'profile' }" @click="goTo('/user/profile')">我的</div>
        </div>
        <div class="user-info">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            <div class="cart-icon" @click="goTo('/user/cart')">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </div>
          </el-badge>
          <span class="user-name">{{ userStore.user?.nickName || userStore.user?.userName }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
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
import { ShoppingCart } from '@element-plus/icons-vue'
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

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}

.nav-menu {
  display: flex;
  font-size: 16px;
  padding: 10px;
  gap: 30px;
}

.nav-item {
  cursor: pointer;
  color: #666;
  font-size: 20px;
  padding: 14px;
  transition: color 0.3s;
}

.nav-item:hover, .nav-item.active {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-name {
  color: #333;
}

.cart-icon {
  cursor: pointer;
}

.main {
  min-height: calc(100vh - 60px);
}
</style>
