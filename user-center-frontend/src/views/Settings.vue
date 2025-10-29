<template>
  <div class="settings-container">
    <!-- 顶部导航 -->
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo-section">
          <img alt="logo" src="/logo.svg" class="logo" />
          <h1>用户导航 - 2025</h1>
        </div>
        <div class="header-actions">
          <a-button @click="$router.push('/welcome')">返回首页</a-button>
          <a-button @click="handleLogout">退出登录</a-button>
        </div>
      </div>
    </a-layout-header>

    <!-- 主要内容 -->
    <a-layout-content class="content">
      <div class="settings-content">
        <a-row :gutter="24">
          <!-- 左侧菜单 -->
          <a-col :span="6">
            <a-card class="settings-menu">
              <a-menu
                v-model:selectedKeys="selectedKeys"
                mode="inline"
                @click="handleMenuClick"
              >
                <a-menu-item key="account">
                  <UserOutlined />
                  账户设置
                </a-menu-item>
                <a-menu-item key="privacy">
                  <LockOutlined />
                  隐私设置
                </a-menu-item>
                <a-menu-item key="notification">
                  <BellOutlined />
                  通知设置
                </a-menu-item>
                <a-menu-item key="appearance">
                  <BgColorsOutlined />
                  外观设置
                </a-menu-item>
                <a-menu-item key="security">
                  <SafetyCertificateOutlined />
                  安全设置
                </a-menu-item>
              </a-menu>
            </a-card>
          </a-col>

          <!-- 右侧内容 -->
          <a-col :span="18">
            <!-- 账户设置 -->
            <div v-show="activeTab === 'account'" class="settings-panel">
              <a-card title="账户设置" class="settings-card">
                <a-form :model="accountSettings" layout="vertical">
                  <a-form-item label="用户名">
                    <a-input v-model:value="accountSettings.username" />
                  </a-form-item>
                  <a-form-item label="邮箱">
                    <a-input v-model:value="accountSettings.email" />
                  </a-form-item>
                  <a-form-item label="手机号">
                    <a-input v-model:value="accountSettings.phone" />
                  </a-form-item>
                  <a-form-item label="时区">
                    <a-select v-model:value="accountSettings.timezone">
                      <a-select-option value="Asia/Shanghai">北京时间 (UTC+8)</a-select-option>
                      <a-select-option value="America/New_York">纽约时间 (UTC-5)</a-select-option>
                      <a-select-option value="Europe/London">伦敦时间 (UTC+0)</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveAccountSettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 隐私设置 -->
            <div v-show="activeTab === 'privacy'" class="settings-panel">
              <a-card title="隐私设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.profilePublic">
                      公开个人资料
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.showEmail">
                      显示邮箱地址
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.showPhone">
                      显示手机号码
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.allowSearch">
                      允许其他用户搜索到我
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="savePrivacySettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 通知设置 -->
            <div v-show="activeTab === 'notification'" class="settings-panel">
              <a-card title="通知设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item label="邮件通知">
                    <a-checkbox-group v-model:value="notificationSettings.email">
                      <a-checkbox value="login">登录提醒</a-checkbox>
                      <a-checkbox value="security">安全提醒</a-checkbox>
                      <a-checkbox value="system">系统通知</a-checkbox>
                    </a-checkbox-group>
                  </a-form-item>
                  <a-form-item label="站内通知">
                    <a-checkbox-group v-model:value="notificationSettings.inApp">
                      <a-checkbox value="message">消息通知</a-checkbox>
                      <a-checkbox value="mention">提及通知</a-checkbox>
                      <a-checkbox value="follow">关注通知</a-checkbox>
                    </a-checkbox-group>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveNotificationSettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 外观设置 -->
            <div v-show="activeTab === 'appearance'" class="settings-panel">
              <a-card title="外观设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item label="主题">
                    <a-radio-group v-model:value="appearanceSettings.theme">
                      <a-radio value="light">浅色主题</a-radio>
                      <a-radio value="dark">深色主题</a-radio>
                      <a-radio value="auto">跟随系统</a-radio>
                    </a-radio-group>
                  </a-form-item>
                  <a-form-item label="语言">
                    <a-select v-model:value="appearanceSettings.language">
                      <a-select-option value="zh-CN">简体中文</a-select-option>
                      <a-select-option value="en-US">English</a-select-option>
                      <a-select-option value="ja-JP">日本語</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveAppearanceSettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 安全设置 -->
            <div v-show="activeTab === 'security'" class="settings-panel">
              <a-card title="安全设置" class="settings-card">
                <div class="security-item">
                  <div class="security-info">
                    <h4>两步验证</h4>
                    <p>增强账户安全性</p>
                  </div>
                  <a-switch v-model:checked="securitySettings.twoFactorAuth" />
                </div>
                <div class="security-item">
                  <div class="security-info">
                    <h4>登录提醒</h4>
                    <p>新设备登录时发送通知</p>
                  </div>
                  <a-switch v-model:checked="securitySettings.loginAlert" />
                </div>
                <div class="security-item">
                  <div class="security-info">
                    <h4>会话管理</h4>
                    <p>管理所有活跃的登录会话</p>
                  </div>
                  <a-button @click="showSessionsModal">查看会话</a-button>
                </div>
                <a-form-item style="margin-top: 24px;">
                  <a-button type="primary" @click="saveSecuritySettings">保存设置</a-button>
                </a-form-item>
              </a-card>
            </div>
          </a-col>
        </a-row>
      </div>
    </a-layout-content>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  BellOutlined,
  BgColorsOutlined,
  SafetyCertificateOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('account')
const selectedKeys = ref(['account'])

// 账户设置
const accountSettings = reactive({
  username: userStore.currentUser?.username || '',
  email: userStore.currentUser?.email || '',
  phone: userStore.currentUser?.phone || '',
  timezone: 'Asia/Shanghai'
})

// 隐私设置
const privacySettings = reactive({
  profilePublic: true,
  showEmail: false,
  showPhone: false,
  allowSearch: true
})

// 通知设置
const notificationSettings = reactive({
  email: ['login', 'security'],
  inApp: ['message', 'mention']
})

// 外观设置
const appearanceSettings = reactive({
  theme: 'light',
  language: 'zh-CN'
})

// 安全设置
const securitySettings = reactive({
  twoFactorAuth: false,
  loginAlert: true
})

const handleMenuClick = ({ key }: { key: string }) => {
  activeTab.value = key
  selectedKeys.value = [key]
}

const saveAccountSettings = () => {
  message.success('账户设置已保存')
}

const savePrivacySettings = () => {
  message.success('隐私设置已保存')
}

const saveNotificationSettings = () => {
  message.success('通知设置已保存')
}

const saveAppearanceSettings = () => {
  message.success('外观设置已保存')
}

const saveSecuritySettings = () => {
  message.success('安全设置已保存')
}

const showSessionsModal = () => {
  message.info('会话管理功能开发中...')
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}
</script>

<style scoped>
.settings-container {
  min-height: 100vh;
  background: #f8fafc;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  padding: 0;
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo {
  width: 40px;
  height: 40px;
  margin-right: 16px;
  border-radius: 8px;
}

.logo-section h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.content {
  padding-top: 80px;
  padding-bottom: 40px;
}

.settings-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.settings-menu {
  margin-bottom: 24px;
}

.settings-panel {
  margin-bottom: 24px;
}

.settings-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.security-item:last-child {
  border-bottom: none;
}

.security-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
}

.security-info p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-content {
    padding: 0 16px;
  }

  .header-content {
    padding: 0 16px;
  }

  .header-actions {
    gap: 8px;
  }
}
</style>