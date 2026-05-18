import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminApi } from '@/api'

export const useAdminStore = defineStore('admin', () => {
    const admin = ref(null)
    const isLoggedIn = computed(() => !!admin.value)

    const login = async (username, password) => {
        console.log('adminStore.login called:', username, password)
        try {
            const result = await adminApi.login(username, password)
            console.log('adminApi.login result:', result)
            if (result && result.admin) {
                admin.value = result.admin
                localStorage.setItem('admin_info', JSON.stringify(result.admin))
                return { success: true }
            }
            return { success: false, message: '登录失败：返回数据格式错误' }
        } catch (error) {
            console.error('Admin login error:', error)
            const message = error.response?.data?.message || error.message || '登录失败'
            return { success: false, message }
        }
    }

    const logout = () => {
        admin.value = null
        localStorage.removeItem('admin_info')
    }

    const checkLogin = () => {
        const stored = localStorage.getItem('admin_info')
        if (stored) {
            try {
                admin.value = JSON.parse(stored)
                console.log('Admin restored from storage:', admin.value)
            } catch (e) {
                console.error('Parse admin error:', e)
            }
        }
    }

    return { admin, isLoggedIn, login, logout, checkLogin }
})
