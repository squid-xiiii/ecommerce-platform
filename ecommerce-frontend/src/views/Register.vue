<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="auth-title">注册账号</h1>

      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="nickName">
          <el-input v-model="form.nickName" placeholder="昵称" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="form.name" placeholder="真实姓名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" size="large" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item prop="address">
          <el-input v-model="form.address" placeholder="地址" size="large" :prefix-icon="Location" />
        </el-form-item>
        <el-form-item prop="age">
          <el-input v-model.number="form.age" type="number" placeholder="年龄" size="large" />
        </el-form-item>
        <el-form-item prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-button type="primary" size="large" class="auth-btn" @click="handleRegister" :loading="loading">
          注册
        </el-button>

        <div class="auth-footer">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Location } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
    userName: '',
    password: '',
    confirmPassword: '',
    nickName: '',
    name: '',
    phone: '',
    address: '',
    age: null,
    sex: '男'
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
    if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
    ],
    nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
    age: [{ type: 'number', min: 1, max: 120, message: '请输入有效的年龄', trigger: 'blur' }]
}

const handleRegister = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const registerData = {
                userName: form.userName,
                password: form.password,
                nickName: form.nickName,
                name: form.name,
                phone: form.phone,
                address: form.address,
                age: form.age,
                sex: form.sex,
                createdAt: new Date()
            }
            const result = await userStore.register(registerData)
            loading.value = false
            if (result.success) {
                ElMessage.success('注册成功')
                router.push('/')
            } else {
                ElMessage.error(result.message)
            }
        }
    })
}
</script>

<style scoped>
.auth-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 40px 0;
}
.auth-card {
    width: 520px;
    padding: 40px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.auth-title {
    font-size: 26px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 16px;
}
.auth-subtitle {
    text-align: center;
    color: #666;
    margin-bottom: 32px;
}
.auth-btn {
    width: 100%;
    margin-top: 16px;
}
.auth-footer {
    text-align: center;
    margin-top: 24px;
    color: #666;
}
.auth-footer a {
    color: #409eff;
    text-decoration: none;
}
</style>
