<template>
  <div class="auth-container">
    <div class="auth-card">
      <!-- 左侧装饰区 -->
      <div class="auth-left">
        <div class="brand">
          <div class="brand-icon">🛒</div>
          <div class="brand-name">E-Commerce</div>
          <div class="brand-slogan">发现好物 · 悦享生活</div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="auth-right">
        <div class="form-header">
          <h2>{{ role === 'admin' ? '管理员登录' : '欢迎回来' }}</h2>
          <p>{{ role === 'admin' ? '登录管理平台' : '登录您的电商账号' }}</p>
        </div>

        <!-- 身份选择 -->
        <div class="role-selector">
          <button
            class="role-btn"
            :class="{ active: role === 'user' }"
            @click="role = 'user'"
          >
            👤 普通用户
          </button>
          <button
            class="role-btn"
            :class="{ active: role === 'admin' }"
            @click="role = 'admin'"
          >
            🔒 管理员
          </button>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" size="large">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              :placeholder="role === 'admin' ? '管理员用户名' : '用户名'"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            @click="handleLogin"
            :loading="loading"
          >
            {{ role === 'admin' ? '登录' : '登录' }}
          </el-button>

          <div class="register-link" v-if="role === 'user'">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
        </el-form>


      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const userStore = useUserStore()
const adminStore = useAdminStore()
const formRef = ref()
const loading = ref(false)
const role = ref('user')

const form = reactive({
    username: '',
    password: ''
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true

            if (role.value === 'admin') {
                const result = await adminStore.login(form.username, form.password)
                if (result.success) {
                    ElMessage.success('管理员登录成功')
                    router.push('/admin')
                } else {
                    ElMessage.error(result.message || '管理员登录失败')
                }
            } else {
                const result = await userStore.login(form.username, form.password)
                if (result.success) {
                    ElMessage.success('用户登录成功')
                    router.push('/user')
                } else {
                    ElMessage.error(result.message || '用户登录失败')
                }
            }
            loading.value = false
        }
    })
}
</script>

<style scoped>
/* 整体容器 */
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg,white 0%, #ddd7dd 100%);
  padding: 20px;
}

/* 卡片容器 - 左右分栏设计 */
.auth-card {
  display: flex;
  width: 880px;
  max-width: 90vw;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

/* 左侧装饰区 */
.auth-left {
  flex: 1;
  background: linear-gradient(135deg, #409eff 0%, #36cea0 100%);
  padding: 48px 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.brand {
  text-align: center;
}

.brand-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.brand-name {
  font-size: 32px;
  font-weight: bold;
  letter-spacing: 2px;
  margin-bottom: 12px;
}

.brand-slogan {
  font-size: 14px;
  opacity: 0.85;
}

/* 右侧表单区 */
.auth-right {
  flex: 1;
  padding: 48px 40px;
  background: white;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: #999;
}

/* 身份选择按钮 */
.role-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
  background: #f5f7fa;
  border-radius: 12px;
  padding: 4px;
}

.role-btn {
  flex: 1;
  padding: 10px 0;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
}

.role-btn.active {
  background: white;
  color: #409eff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 表单样式 */
.login-btn {
  width: 100%;
  margin-top: 24px;
  border-radius: 10px;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

/* 注册链接 */
.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #999;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 测试账号提示 */
.test-tip {
  margin-top: 24px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.tip-icon {
  font-size: 14px;
}

.tip-text {
  flex: 1;
}

/* 响应式 - 小屏幕时隐藏左侧装饰区 */
@media (max-width: 640px) {
  .auth-left {
    display: none;
  }

  .auth-card {
    width: 400px;
  }
}

/* 表单项间距优化 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}
</style>
