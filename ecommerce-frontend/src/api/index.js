import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000
})

// 请求拦截器 - 自动添加认证信息
api.interceptors.request.use(config => {
    // 普通用户信息
    const user = localStorage.getItem('user')
    if (user) {
        try {
            const userData = JSON.parse(user)
            config.headers['X-Username'] = userData.userName
        } catch  {}
    }

    // 管理员信息（不用 token，只用用户名）
    const adminInfo = localStorage.getItem('admin_info')
    if (adminInfo) {
        try {
            const adminData = JSON.parse(adminInfo)
            config.headers['X-Admin-Username'] = adminData.username
        } catch  {}
    }

    return config
})

// 响应拦截器 - 统一处理响应和错误
api.interceptors.response.use(
    response => {
        if (response.data && response.data.code === 0) {
            return response.data.data
        } else if (response.data && response.data.code !== 0) {
            // 401 未授权
            if (response.data.code === 401) {
                localStorage.removeItem('user')
                localStorage.removeItem('admin_info')
                window.location.href = '/login'
            }
            return Promise.reject(new Error(response.data.message || '请求失败'))
        }
        return response.data
    },
    error => {
        console.error('API Error:', error)

        // 处理 401 未授权错误
        if (error.response?.status === 401) {
            localStorage.removeItem('user')
            localStorage.removeItem('admin_info')
            window.location.href = '/login'
        }

        return Promise.reject(error)
    }
)

// ==================== 用户端 API ====================

export const userApi = {
    register: (user) => api.post('/users/register', user),
    login: (userName, password) => api.post('/users/login', { userName, password }),
    getUserInfo: (userName) => api.get(`/users/${userName}`),
    updateUser: (id, user) => api.put(`/users/${id}`, user)
}

export const goodsApi = {
    getList: (category) => api.get('/goods', { params: { category } }),
    getDetail: (goodsId) => api.get(`/goods/${goodsId}`)
}

export const orderApi = {
    createOrder: (order) => api.post('/orders', order),
    getUserOrders: (userName) => api.get(`/orders/user/${userName}`),
    getHistoryOrders: (userName) => api.get(`/orders/user/${userName}/history`),
    updateStatus: (id, status) => api.patch(`/orders/${id}/status`, null, { params: { status } }),
    deleteOrder: (id) => api.delete(`/orders/${id}`)
}

export const commentApi = {
    addComment: (comment) => api.post('/comments', comment),
    getGoodsComments: (goodsId) => api.get(`/comments/goods/${goodsId}`),
    getUserComments: (userName) => api.get(`/comments/user/${userName}`),
    deleteComment: (id) => api.delete(`/comments/${id}`)
}

export const clickApi = {
    logClick: (log) => api.post('/clicks', log)
}

export const cartApi = {
    getCart: (userId) => api.get(`/cart/${userId}`),
    addToCart: (userId, goodsId, quantity) => api.post(`/cart/${userId}/add`, { goodsId, quantity }),
    updateCartItem: (userId, goodsId, quantity) => api.put(`/cart/${userId}/update`, { goodsId, quantity }),
    removeCartItem: (userId, goodsId) => api.delete(`/cart/${userId}/remove/${goodsId}`),
    clearCart: (userId) => api.delete(`/cart/${userId}/clear`),
    syncCart: (userId, items) => api.post(`/cart/${userId}/sync`, items)
}

// ==================== 管理后台 API ====================

export const adminApi = {
    login: (username, password) => api.post('/admin/login', { username, password })
}

// 同时导出 adminLogin 兼容旧代码
export const adminLogin = adminApi.login

export const statsApi = {
    getStats: () => api.get('/admin/stats')
}

export const adminGoodsApi = {
    getList: () => api.get('/admin/goods'),
    getDetail: (goodsId) => api.get(`/admin/goods/${goodsId}`),
    create: (data) => api.post('/admin/goods', data),
    update: (id, data) => api.put(`/admin/goods/${id}`, data),
    delete: (id) => api.delete(`/admin/goods/${id}`)
}

export const adminOrderApi = {
    getList: () => api.get('/admin/orders'),
    updateStatus: (id, status) => api.patch(`/admin/orders/${id}/status`, null, { params: { status } }),
    getCompleted: () => api.get('/admin/orders/completed')
}

export const adminClickApi = {
    getList: (limit = 100) => api.get(`/admin/clicks?limit=${limit}`),
    getStats: () => api.get('/admin/clicks/stats')
}

export const adminLogApi = {
    getList: (limit = 100) => api.get(`/admin/logs?limit=${limit}`),
    getErrors: () => api.get('/admin/logs/errors')
}

export default api
