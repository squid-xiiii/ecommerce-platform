import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
    const user = ref(null)
    const token = ref(null)
    const isLoggedIn = computed(() => !!user.value)

    const login = async (userName, password) => {
        try {
            const result = await userApi.login(userName, password)
            if (result) {
                user.value = result.user
                token.value = result.token
                localStorage.setItem('user', JSON.stringify(result.user))
                localStorage.setItem('token', result.token)
                return { success: true, data: result.user }
            }
            return { success: false, message: '登录失败' }
        } catch (error) {
            return { success: false, message: error.response?.data?.message || error.message || '登录失败' }
        }
    }

    const register = async (userData) => {
        try {
            const newUser = await userApi.register(userData)
            if (newUser) {
                user.value = newUser
                localStorage.setItem('user', JSON.stringify(newUser))
                return { success: true, data: newUser }
            }
            return { success: false, message: '注册失败' }
        } catch (error) {
            return { success: false, message: error.response?.data?.message || error.message || '注册失败' }
        }
    }

    const logout = () => {
        user.value = null
        token.value = null
        localStorage.removeItem('user')
        localStorage.removeItem('token')
    }

    const checkLogin = () => {
        const stored = localStorage.getItem('user')
        const storedToken = localStorage.getItem('token')
        if (stored && storedToken) {
            try {
                user.value = JSON.parse(stored)
                token.value = storedToken
            } catch  {}
        }
    }

    return { user, token, isLoggedIn, login, register, logout, checkLogin }
})
