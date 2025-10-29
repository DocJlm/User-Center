<template>
  <div class="container">
    <div class="content">
      <a-form
        :model="formData"
        :rules="rules"
        @finish="handleSubmit"
        class="register-form"
        layout="vertical"
      >
        <div class="logo-section">
          <img alt="logo" src="/logo.svg" class="logo" />
          <h1>用户导航 - 2025</h1>
          <p>
            <a href="https://www.zqcblog.com" target="_blank" rel="noreferrer">
              欢迎访问我的博客
            </a>
          </p>
        </div>

        <a-form-item label="账号" name="userAccount">
          <a-input
            v-model:value="formData.userAccount"
            placeholder="请输入账号"
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item label="密码" name="userPassword">
          <a-input-password
            v-model:value="formData.userPassword"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item label="确认密码" name="checkPassword">
          <a-input-password
            v-model:value="formData.checkPassword"
            placeholder="请再次输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item label="星球编号" name="planetCode">
          <a-input
            v-model:value="formData.planetCode"
            placeholder="请输入星球编号"
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <div class="form-actions">
          <a-checkbox v-model:checked="formData.autoLogin">自动登录</a-checkbox>
          <router-link to="/user/login">已有账号？去登录</router-link>
        </div>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            block
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { register as registerApi } from '@/services/user'
import AppFooter from '@/components/AppFooter.vue'
import type { RegisterParams } from '@/types/user'

const router = useRouter()
const loading = ref(false)

const formData = reactive<RegisterParams & { autoLogin: boolean }>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
  planetCode: '',
  autoLogin: true
})

const rules = {
  userAccount: [
    { required: true, message: '账号是必填项！' }
  ],
  userPassword: [
    { required: true, message: '密码是必填项！' },
    { min: 8, message: '长度不能小于 8' }
  ],
  checkPassword: [
    { required: true, message: '确认密码是必填项！' },
    { min: 8, message: '长度不能小于 8' },
    {
      validator: (_rule: any, value: string) => {
        if (value !== formData.userPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  planetCode: [
    { required: true, message: '星球编号是必填项！' }
  ]
}

const handleSubmit = async () => {
  if (formData.userPassword !== formData.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    const response = await registerApi({
      userAccount: formData.userAccount,
      userPassword: formData.userPassword,
      checkPassword: formData.checkPassword,
      planetCode: formData.planetCode
    })

    if (response.code === 0 && response.data) {
      message.success('注册成功！')
      router.push('/user/login')
    } else {
      message.error('注册失败，请重试！')
    }
  } catch (error) {
    message.error('注册失败，请重试！')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-form {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.logo-section h1 {
  margin: 16px 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
}

.logo-section p {
  margin: 0;
  color: #666;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-actions a {
  color: #1890ff;
  text-decoration: none;
}

.form-actions a:hover {
  text-decoration: underline;
}
</style>