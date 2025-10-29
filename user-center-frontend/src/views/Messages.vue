<template>
  <div class="messages-container">
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
      <div class="messages-content">
        <a-row :gutter="24">
          <!-- 消息列表 -->
          <a-col :span="16">
            <a-card title="消息列表" class="messages-card">
              <template #extra>
                <a-space>
                  <a-button @click="markAllAsRead">全部已读</a-button>
                  <a-button @click="refreshMessages">刷新</a-button>
                </a-space>
              </template>

              <a-list
                :data-source="messages"
                :loading="loading"
                item-layout="horizontal"
              >
                <template #renderItem="{ item }">
                  <a-list-item
                    :class="{ 'unread-item': !item.isRead }"
                    @click="selectMessage(item)"
                    style="cursor: pointer; padding: 16px; border-radius: 8px; margin-bottom: 8px;"
                  >
                    <a-list-item-meta>
                      <template #avatar>
                        <a-avatar :style="{ backgroundColor: getTypeColor(item.type) }">
                          <component :is="getTypeIcon(item.type)" />
                        </a-avatar>
                      </template>
                      <template #title>
                        <div class="message-title">
                          <span>{{ item.title }}</span>
                          <a-tag :color="getTypeColor(item.type)" size="small">
                            {{ getTypeText(item.type) }}
                          </a-tag>
                          <span class="message-time">{{ formatTime(item.createTime) }}</span>
                        </div>
                      </template>
                      <template #description>
                        <div class="message-desc">{{ item.content }}</div>
                      </template>
                    </a-list-item-meta>
                    <template #actions>
                      <a v-if="!item.isRead" @click.stop="markAsRead(item.id)">标记已读</a>
                      <a @click.stop="deleteMessage(item.id)" style="color: #ff4d4f;">删除</a>
                    </template>
                  </a-list-item>
                </template>
              </a-list>

              <div v-if="messages.length === 0" class="empty-messages">
                <a-empty description="暂无消息" />
              </div>
            </a-card>
          </a-col>

          <!-- 消息详情 -->
          <a-col :span="8">
            <a-card title="消息详情" class="message-detail-card">
              <div v-if="selectedMessage" class="message-detail">
                <div class="detail-header">
                  <div class="detail-type">
                    <a-avatar :style="{ backgroundColor: getTypeColor(selectedMessage.type) }" size="large">
                      <component :is="getTypeIcon(selectedMessage.type)" />
                    </a-avatar>
                    <div>
                      <h3>{{ selectedMessage.title }}</h3>
                      <a-tag :color="getTypeColor(selectedMessage.type)">
                        {{ getTypeText(selectedMessage.type) }}
                      </a-tag>
                    </div>
                  </div>
                  <div class="detail-time">
                    {{ formatDateTime(selectedMessage.createTime) }}
                  </div>
                </div>

                <div class="detail-content">
                  <p>{{ selectedMessage.content }}</p>
                </div>

                <div class="detail-actions">
                  <a-button v-if="!selectedMessage.isRead" type="primary" @click="markAsRead(selectedMessage.id)">
                    标记已读
                  </a-button>
                  <a-button @click="deleteMessage(selectedMessage.id)" danger>
                    删除消息
                  </a-button>
                </div>
              </div>

              <div v-else class="no-message-selected">
                <a-empty description="请选择一条消息查看详情" />
              </div>
            </a-card>

            <!-- 消息设置 -->
            <a-card title="消息设置" class="message-settings-card" style="margin-top: 16px;">
              <a-form layout="vertical">
                <a-form-item label="消息提醒">
                  <a-switch v-model:checked="settings.enableNotification" />
                </a-form-item>
                <a-form-item label="声音提醒">
                  <a-switch v-model:checked="settings.enableSound" />
                </a-form-item>
                <a-form-item label="自动删除已读消息">
                  <a-switch v-model:checked="settings.autoDelete" />
                </a-form-item>
                <a-form-item label="保留天数" v-if="settings.autoDelete">
                  <a-input-number v-model:value="settings.retentionDays" :min="1" :max="365" />
                </a-form-item>
              </a-form>
            </a-card>
          </a-col>
        </a-row>
      </div>
    </a-layout-content>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  BellOutlined,
  UserOutlined,
  SafetyOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const selectedMessage = ref<any>(null)

const messages = ref([
  {
    id: 1,
    title: '系统更新通知',
    content: '用户中心系统已完成更新，新增了个人中心、数据统计等功能，欢迎体验！',
    type: 'system',
    isRead: false,
    createTime: '2025-10-29T10:30:00Z'
  },
  {
    id: 2,
    title: '登录提醒',
    content: '您的账号在新设备上登录，如非本人操作请及时修改密码',
    type: 'security',
    isRead: true,
    createTime: '2025-10-28T15:20:00Z'
  },
  {
    id: 3,
    title: '欢迎使用',
    content: '欢迎注册用户中心，请完善您的个人信息以获得更好的使用体验',
    type: 'welcome',
    isRead: true,
    createTime: '2025-10-27T09:15:00Z'
  }
])

const settings = reactive({
  enableNotification: true,
  enableSound: false,
  autoDelete: false,
  retentionDays: 30
})

const getTypeColor = (type: string) => {
  const colors = {
    system: '#1890ff',
    security: '#f5222d',
    welcome: '#52c41a',
    user: '#722ed1'
  }
  return colors[type] || '#1890ff'
}

const getTypeIcon = (type: string) => {
  const icons = {
    system: InfoCircleOutlined,
    security: SafetyOutlined,
    welcome: CheckCircleOutlined,
    user: UserOutlined
  }
  return icons[type] || BellOutlined
}

const getTypeText = (type: string) => {
  const texts = {
    system: '系统',
    security: '安全',
    welcome: '欢迎',
    user: '用户'
  }
  return texts[type] || '通知'
}

const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))

  if (hours < 1) return '刚刚'
  if (hours < 24) return `${hours}小时前`
  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (timeStr: string) => {
  return new Date(timeStr).toLocaleString('zh-CN')
}

const selectMessage = (msg: any) => {
  selectedMessage.value = msg
  if (!msg.isRead) {
    markAsRead(msg.id)
  }
}

const markAsRead = (messageId: number) => {
  const msg = messages.value.find(m => m.id === messageId)
  if (msg) {
    msg.isRead = true
    message.success('已标记为已读')
  }
}

const deleteMessage = (messageId: number) => {
  const index = messages.value.findIndex(m => m.id === messageId)
  if (index > -1) {
    messages.value.splice(index, 1)
    if (selectedMessage.value?.id === messageId) {
      selectedMessage.value = null
    }
    message.success('消息已删除')
  }
}

const markAllAsRead = () => {
  messages.value.forEach(msg => {
    msg.isRead = true
  })
  message.success('所有消息已标记为已读')
}

const refreshMessages = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('消息已刷新')
  }, 1000)
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}

onMounted(() => {
  // 自动选择第一条消息
  if (messages.value.length > 0) {
    selectMessage(messages.value[0])
  }
})
</script>

<style scoped>
.messages-container {
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

.messages-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.messages-card,
.message-detail-card,
.message-settings-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
}

.unread-item {
  background: #f6ffed;
  border-left: 4px solid #52c41a;
}

.message-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.message-time {
  font-size: 12px;
  color: #64748b;
  margin-left: auto;
}

.message-desc {
  color: #64748b;
  margin-top: 4px;
}

.empty-messages {
  text-align: center;
  padding: 40px 0;
}

.message-detail {
  padding: 16px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-type {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-type h3 {
  margin: 0;
  font-size: 16px;
}

.detail-time {
  font-size: 12px;
  color: #64748b;
}

.detail-content {
  background: #f8fafc;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.detail-actions {
  display: flex;
  gap: 8px;
}

.no-message-selected {
  text-align: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-content {
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