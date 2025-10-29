<template>
  <div class="system-settings-container">
    <!-- 顶部导航 -->
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo-section">
          <img alt="logo" src="/logo.svg" class="logo" />
          <h1>系统设置 - 用户导航</h1>
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
                <a-menu-item key="basic">
                  <SettingOutlined />
                  基础设置
                </a-menu-item>
                <a-menu-item key="security">
                  <SafetyOutlined />
                  安全设置
                </a-menu-item>
                <a-menu-item key="notification">
                  <BellOutlined />
                  通知设置
                </a-menu-item>
                <a-menu-item key="backup">
                  <DatabaseOutlined />
                  备份管理
                </a-menu-item>
                <a-menu-item key="logs">
                  <FileTextOutlined />
                  系统日志
                </a-menu-item>
              </a-menu>
            </a-card>
          </a-col>

          <!-- 右侧内容 -->
          <a-col :span="18">
            <!-- 基础设置 -->
            <div v-show="activeTab === 'basic'" class="settings-panel">
              <a-card title="基础设置" class="settings-card">
                <a-form :model="basicSettings" layout="vertical">
                  <a-form-item label="系统名称">
                    <a-input v-model:value="basicSettings.systemName" />
                  </a-form-item>
                  <a-form-item label="系统描述">
                    <a-textarea v-model:value="basicSettings.systemDescription" :rows="3" />
                  </a-form-item>
                  <a-form-item label="系统版本">
                    <a-input v-model:value="basicSettings.systemVersion" />
                  </a-form-item>
                  <a-form-item label="维护模式">
                    <a-switch v-model:checked="basicSettings.maintenanceMode" />
                  </a-form-item>
                  <a-form-item label="最大用户数">
                    <a-input-number v-model:value="basicSettings.maxUsers" :min="1" style="width: 100%" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveBasicSettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 安全设置 -->
            <div v-show="activeTab === 'security'" class="settings-panel">
              <a-card title="安全设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item label="密码强度要求">
                    <a-checkbox-group v-model:value="securitySettings.passwordRequirements">
                      <a-checkbox value="length">至少8位</a-checkbox>
                      <a-checkbox value="uppercase">包含大写字母</a-checkbox>
                      <a-checkbox value="lowercase">包含小写字母</a-checkbox>
                      <a-checkbox value="numbers">包含数字</a-checkbox>
                      <a-checkbox value="symbols">包含特殊字符</a-checkbox>
                    </a-checkbox-group>
                  </a-form-item>
                  <a-form-item label="登录失败锁定">
                    <a-switch v-model:checked="securitySettings.loginLock" />
                  </a-form-item>
                  <a-form-item label="最大失败次数" v-if="securitySettings.loginLock">
                    <a-input-number v-model:value="securitySettings.maxFailures" :min="3" :max="10" />
                  </a-form-item>
                  <a-form-item label="锁定时间(分钟)" v-if="securitySettings.loginLock">
                    <a-input-number v-model:value="securitySettings.lockTime" :min="5" :max="1440" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveSecuritySettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 通知设置 -->
            <div v-show="activeTab === 'notification'" class="settings-panel">
              <a-card title="通知设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item label="管理员邮箱">
                    <a-input v-model:value="notificationSettings.adminEmail" />
                  </a-form-item>
                  <a-form-item label="系统异常通知">
                    <a-switch v-model:checked="notificationSettings.errorNotification" />
                  </a-form-item>
                  <a-form-item label="新用户注册通知">
                    <a-switch v-model:checked="notificationSettings.newUserNotification" />
                  </a-form-item>
                  <a-form-item label="每日统计报告">
                    <a-switch v-model:checked="notificationSettings.dailyReport" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveNotificationSettings">保存设置</a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </div>

            <!-- 备份管理 -->
            <div v-show="activeTab === 'backup'" class="settings-panel">
              <a-card title="备份管理" class="settings-card">
                <div class="backup-info">
                  <div class="backup-item">
                    <div class="backup-info-text">
                      <h4>自动备份</h4>
                      <p>定期自动备份系统数据</p>
                    </div>
                    <a-switch v-model:checked="backupSettings.autoBackup" />
                  </div>
                  <a-form-item label="备份频率" v-if="backupSettings.autoBackup">
                    <a-select v-model:value="backupSettings.backupFrequency">
                      <a-select-option value="daily">每日</a-select-option>
                      <a-select-option value="weekly">每周</a-select-option>
                      <a-select-option value="monthly">每月</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item label="保留备份数量" v-if="backupSettings.autoBackup">
                    <a-input-number v-model:value="backupSettings.backupRetention" :min="1" :max="30" />
                  </a-form-item>
                </div>
                <div class="backup-actions" style="margin-top: 24px;">
                  <a-space>
                    <a-button type="primary" @click="createBackup">立即备份</a-button>
                    <a-button @click="restoreBackup">恢复备份</a-button>
                    <a-button @click="downloadBackup">下载备份</a-button>
                  </a-space>
                </div>
              </a-card>
            </div>

            <!-- 系统日志 -->
            <div v-show="activeTab === 'logs'" class="settings-panel">
              <a-card title="系统日志" class="settings-card">
                <template #extra>
                  <a-space>
                    <a-range-picker v-model:value="dateRange" @change="filterLogs" />
                    <a-select v-model:value="logLevel" @change="filterLogs" style="width: 120px;">
                      <a-select-option value="all">全部</a-select-option>
                      <a-select-option value="error">错误</a-select-option>
                      <a-select-option value="warn">警告</a-select-option>
                      <a-select-option value="info">信息</a-select-option>
                    </a-select>
                    <a-button @click="refreshLogs">刷新</a-button>
                  </a-space>
                </template>

                <a-table
                  :columns="logColumns"
                  :data-source="logs"
                  :loading="loading"
                  :pagination="logPagination"
                  size="small"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'level'">
                      <a-tag :color="getLogLevelColor(record.level)">
                        {{ record.level }}
                      </a-tag>
                    </template>
                    <template v-if="column.key === 'time'">
                      {{ formatDateTime(record.time) }}
                    </template>
                  </template>
                </a-table>
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
  SettingOutlined,
  SafetyOutlined,
  BellOutlined,
  DatabaseOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('basic')
const selectedKeys = ref(['basic'])
const loading = ref(false)
const dateRange = ref<any[]>([])
const logLevel = ref('all')

// 基础设置
const basicSettings = reactive({
  systemName: '用户中心',
  systemDescription: '基于 Vue 3 + Spring Boot 构建的现代化用户管理系统',
  systemVersion: '1.0.0',
  maintenanceMode: false,
  maxUsers: 10000
})

// 安全设置
const securitySettings = reactive({
  passwordRequirements: ['length', 'numbers'],
  loginLock: true,
  maxFailures: 5,
  lockTime: 30
})

// 通知设置
const notificationSettings = reactive({
  adminEmail: 'admin@zqcblog.com',
  errorNotification: true,
  newUserNotification: true,
  dailyReport: false
})

// 备份设置
const backupSettings = reactive({
  autoBackup: true,
  backupFrequency: 'daily',
  backupRetention: 7
})

// 系统日志
const logs = ref([
  {
    id: 1,
    level: 'info',
    message: '用户 admin 登录系统',
    time: '2025-10-29T10:30:00Z',
    user: 'admin'
  },
  {
    id: 2,
    level: 'warn',
    message: '用户登录失败次数过多',
    time: '2025-10-29T09:15:00Z',
    user: 'unknown'
  },
  {
    id: 3,
    level: 'error',
    message: '数据库连接异常',
    time: '2025-10-28T18:45:00Z',
    user: 'system'
  }
])

const logColumns = [
  {
    title: '级别',
    dataIndex: 'level',
    key: 'level',
    width: 80
  },
  {
    title: '时间',
    dataIndex: 'time',
    key: 'time',
    width: 180
  },
  {
    title: '用户',
    dataIndex: 'user',
    key: 'user',
    width: 100
  },
  {
    title: '消息',
    dataIndex: 'message',
    key: 'message'
  }
]

const logPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100,
  showSizeChanger: true,
  showQuickJumper: true
})

const handleMenuClick = ({ key }: { key: string }) => {
  activeTab.value = key
  selectedKeys.value = [key]
}

const saveBasicSettings = () => {
  message.success('基础设置已保存')
}

const saveSecuritySettings = () => {
  message.success('安全设置已保存')
}

const saveNotificationSettings = () => {
  message.success('通知设置已保存')
}

const createBackup = () => {
  message.info('备份功能开发中...')
}

const restoreBackup = () => {
  message.info('恢复备份功能开发中...')
}

const downloadBackup = () => {
  message.info('下载备份功能开发中...')
}

const filterLogs = () => {
  message.info('日志筛选功能开发中...')
}

const refreshLogs = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('日志已刷新')
  }, 1000)
}

const getLogLevelColor = (level: string) => {
  const colors = {
    error: 'red',
    warn: 'orange',
    info: 'blue',
    debug: 'gray'
  }
  return colors[level] || 'blue'
}

const formatDateTime = (timeStr: string) => {
  return new Date(timeStr).toLocaleString('zh-CN')
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}
</script>

<style scoped>
.system-settings-container {
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

.backup-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.backup-item:last-child {
  border-bottom: none;
}

.backup-info-text h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
}

.backup-info-text p {
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