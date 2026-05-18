import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'

// 登录页面
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'

// 用户端布局
import UserLayout from '@/views/user/Layout.vue'
import Home from '@/views/user/Home.vue'
import GoodsList from '@/views/user/GoodsList.vue'
import GoodsDetail from '@/views/user/GoodsDetail.vue'
import Cart from '@/views/user/Cart.vue'
import Profile from '@/views/user/Profile.vue'

// 管理端布局
import AdminLayout from '@/views/admin/Layout.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import GoodsManage from '@/views/admin/GoodsManage.vue'
import OrdersManage from '@/views/admin/OrdersManage.vue'
import ClickStats from '@/views/admin/ClickStats.vue'
import SystemLogs from '@/views/admin/SystemLogs.vue'

const routes = [
    { path: '/login', name: 'Login', component: Login },
    { path: '/register', name: 'Register', component: Register },

    // 用户端路由 - 所有用户端页面都以 /user 开头
    {
        path: '/user',
        component: UserLayout,
        meta: { requiresAuth: true, role: 'user' },
        children: [
            { path: '', name: 'UserHome', component: Home },
            { path: 'goods', name: 'UserGoodsList', component: GoodsList },
            { path: 'goods/:id', name: 'UserGoodsDetail', component: GoodsDetail },
            { path: 'cart', name: 'UserCart', component: Cart },
            { path: 'profile', name: 'UserProfile', component: Profile }
        ]
    },

    // 管理端路由 - 所有管理端页面都以 /admin 开头
    {
        path: '/admin',
        component: AdminLayout,
        meta: { requiresAuth: true, role: 'admin' },
        children: [
            { path: '', name: 'Dashboard', component: Dashboard },
            { path: 'goods', name: 'GoodsManage', component: GoodsManage },
            { path: 'orders', name: 'OrdersManage', component: OrdersManage },
            { path: 'clicks', name: 'ClickStats', component: ClickStats },
            { path: 'logs', name: 'SystemLogs', component: SystemLogs }
        ]
    },

    // 默认重定向到用户端
    { path: '/', redirect: '/user' },

    // 404 重定向
    { path: '/:pathMatch(.*)*', redirect: '/user' }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫 - 修复 next 警告
router.beforeEach((to, from) => {
    const userStore = useUserStore()
    const adminStore = useAdminStore()

    userStore.checkLogin()
    adminStore.checkLogin()

    // 检查是否需要登录
    if (to.meta.requiresAuth) {
        const requiredRole = to.meta.role

        if (requiredRole === 'user' && !userStore.isLoggedIn) {
            return '/login'
        }
        if (requiredRole === 'admin' && !adminStore.isLoggedIn) {
            return '/login'
        }
    }

    // 如果已登录，访问登录页则重定向到对应首页
    if (to.path === '/login') {
        if (adminStore.isLoggedIn) {
            return '/admin'
        }
        if (userStore.isLoggedIn) {
            return '/user'
        }
    }

    return true
})

export default router
