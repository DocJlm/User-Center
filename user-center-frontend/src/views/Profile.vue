<template>
  <div class="profile-container">
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
      <div class="profile-content">
        <!-- 个人信息卡片 -->
        <a-card class="profile-card" title="个人信息">
          <template #extra>
            <a-button type="primary" @click="editMode = !editMode">
              {{ editMode ? '取消编辑' : '编辑资料' }}
            </a-button>
          </template>

          <div class="profile-header">
            <div class="avatar-section">
              <div class="avatar-upload">
                <a-avatar
                  :src="formData.avatarUrl || '/default-avatar.png'"
                  :size="120"
                  class="profile-avatar"
                />
                <div v-if="editMode" class="avatar-overlay" @click="showAvatarModal = true">
                  <CameraOutlined />
                  <span>更换头像</span>
                </div>
              </div>
              <div class="avatar-info">
                <h3>{{ formData.username }}</h3>
                <p>{{ formData.userAccount }}</p>
                <a-tag :color="formData.userRole === 1 ? 'gold' : 'blue'">
                  {{ formData.userRole === 1 ? '管理员' : '普通用户' }}
                </a-tag>
              </div>
            </div>
          </div>

          <a-form :model="formData" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="用户名">
                  <a-input
                    v-model:value="formData.username"
                    :disabled="!editMode"
                    placeholder="请输入用户名"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="用户账户">
                  <a-input
                    v-model:value="formData.userAccount"
                    disabled
                    placeholder="用户账户不可修改"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="性别">
                  <a-select
                    v-model:value="formData.gender"
                    :disabled="!editMode"
                    placeholder="请选择性别"
                  >
                    <a-select-option :value="0">保密</a-select-option>
                    <a-select-option :value="1">男</a-select-option>
                    <a-select-option :value="2">女</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="星球编号">
                  <a-input
                    v-model:value="formData.planetCode"
                    :disabled="!editMode"
                    placeholder="请输入星球编号"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="手机号">
                  <a-input
                    v-model:value="formData.phone"
                    :disabled="!editMode"
                    placeholder="请输入手机号"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱">
                  <a-input
                    v-model:value="formData.email"
                    :disabled="!editMode"
                    placeholder="请输入邮箱"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="个人简介">
              <a-textarea
                v-model:value="formData.bio"
                :disabled="!editMode"
                :rows="4"
                placeholder="介绍一下自己..."
                show-count
                :maxlength="200"
              />
            </a-form-item>

            <div v-if="editMode" class="form-actions">
              <a-button @click="editMode = false">取消</a-button>
              <a-button type="primary" @click="handleSave" :loading="loading">
                保存修改
              </a-button>
            </div>
          </a-form>
        </a-card>

        <!-- 账户安全卡片 -->
        <a-card class="security-card" title="账户安全">
          <div class="security-items">
            <div class="security-item">
              <div class="security-info">
                <LockOutlined class="security-icon" />
                <div>
                  <h4>登录密码</h4>
                  <p>上次修改: {{ formatDate(userInfo?.updateTime) }}</p>
                </div>
              </div>
              <a-button @click="showPasswordModal = true">修改密码</a-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <MobileOutlined class="security-icon" />
                <div>
                  <h4>手机号绑定</h4>
                  <p>{{ formData.phone ? '已绑定' : '未绑定' }}</p>
                </div>
              </div>
              <a-button>{{ formData.phone ? '更换' : '绑定' }}</a-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <MailOutlined class="security-icon" />
                <div>
                  <h4>邮箱绑定</h4>
                  <p>{{ formData.email ? '已绑定' : '未绑定' }}</p>
                </div>
              </div>
              <a-button>{{ formData.email ? '更换' : '绑定' }}</a-button>
            </div>
          </div>
        </a-card>

        <!-- 活动记录卡片 -->
        <a-card class="activity-card" title="最近活动">
          <a-timeline>
            <a-timeline-item
              v-for="activity in activities"
              :key="activity.id"
              :color="activity.type === 'login' ? 'green' : 'blue'"
            >
              <div class="activity-item">
                <div class="activity-header">
                  <span class="activity-title">{{ activity.title }}</span>
                  <span class="activity-time">{{ formatDateTime(activity.createTime) }}</span>
                </div>
                <p class="activity-desc">{{ activity.description }}</p>
              </div>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </div>
    </a-layout-content>

    <!-- 头像上传模态框 -->
    <a-modal
      v-model:open="showAvatarModal"
      title="更换头像"
      @ok="handleAvatarUpload"
      @cancel="showAvatarModal = false"
    >
      <div class="avatar-upload-content">
        <a-upload
          list-type="picture-card"
          :show-upload-list="false"
          :before-upload="beforeUpload"
          @change="handleFileChange"
        >
          <img v-if="previewAvatar" :src="previewAvatar" alt="avatar" class="preview-image" />
          <div v-else class="upload-placeholder">
            <PlusOutlined />
            <div style="margin-top: 8px">上传图片</div>
          </div>
        </a-upload>
        <div class="upload-tips">
          <p>支持 JPG、PNG 格式，文件大小不超过 2MB</p>
        </div>
      </div>
    </a-modal>

    <!-- 修改密码模态框 -->
    <a-modal
      v-model:open="showPasswordModal"
      title="修改密码"
      @ok="handlePasswordChange"
      @cancel="resetPasswordForm"
    >
      <a-form :model="passwordForm" layout="vertical">
        <a-form-item label="当前密码" name="oldPassword">
          <a-input-password v-model:value="passwordForm.oldPassword" />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="passwordForm.newPassword" />
        </a-form-item>
        <a-form-item label="确认新密码" name="confirmPassword">
          <a-input-password v-model:value="passwordForm.confirmPassword" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  CameraOutlined,
  LockOutlined,
  MobileOutlined,
  MailOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, changePassword } from '@/services/user'
import type { CurrentUser } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const editMode = ref(false)
const loading = ref(false)
const showAvatarModal = ref(false)
const showPasswordModal = ref(false)
const previewAvatar = ref('')
const avatarFile = ref<File | null>(null)

const userInfo = ref<CurrentUser | null>(null)

const formData = reactive({
  username: '',
  userAccount: '',
  avatarUrl: '',
  gender: 0,
  phone: '',
  email: '',
  planetCode: '',
  bio: '',
  userRole: 0
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const activities = ref([
  {
    id: 1,
    title: '登录系统',
    description: '通过账号密码登录',
    type: 'login',
    createTime: '2025-10-29T10:30:00Z'
  },
  {
    id: 2,
    title: '修改个人信息',
    description: '更新了用户名和邮箱',
    type: 'update',
    createTime: '2025-10-28T15:20:00Z'
  },
  {
    id: 3,
    title: '修改密码',
    description: '成功修改登录密码',
    type: 'security',
    createTime: '2025-10-27T09:15:00Z'
  }
])

// 初始化用户信息
const initUserInfo = () => {
  if (userStore.currentUser) {
    userInfo.value = userStore.currentUser
    Object.assign(formData, userStore.currentUser)
  }
}

// 保存个人信息
const handleSave = async () => {
  loading.value = true
  try {
    // 构建更新数据，只包含可修改的字段
    const updateData = {
      username: formData.username,
      gender: formData.gender,
      phone: formData.phone,
      email: formData.email,
      planetCode: formData.planetCode,
      bio: formData.bio || ''
    }

    console.log('发送更新数据:', updateData)

    // 模拟API调用
    const response = await updateUserInfo(updateData)
    console.log('API响应:', response)

    if (response && response.code === 0) {
      message.success('个人信息更新成功')
      editMode.value = false
      // 更新store中的用户信息
      await userStore.fetchCurrentUser()
      initUserInfo()
    } else {
      message.error(response?.message || '更新失败，请重试')
    }
  } catch (error) {
    console.error('更新失败:', error)
    message.error('更新失败，请重试')
  } finally {
    loading.value = false
  }
}

// 头像上传前的验证
const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 文件选择处理
const handleFileChange = (info: any) => {
  if (info.file.status === 'uploading') {
    return
  }
  if (info.file.originFileObj) {
    avatarFile.value = info.file.originFileObj
    const reader = new FileReader()
    reader.onload = (e) => {
      previewAvatar.value = e.target?.result as string
    }
    reader.readAsDataURL(info.file.originFileObj)
  }
}

// 处理头像上传
const handleAvatarUpload = async () => {
  if (!avatarFile.value) {
    message.error('请选择要上传的图片')
    return
  }

  loading.value = true
  try {
    console.log('上传头像文件:', avatarFile.value.name)

    // 调用真实的上传API
    const response = await uploadAvatar(avatarFile.value)
    console.log('头像上传响应:', response)

    if (response && response.code === 0 && response.data) {
      // 更新本地头像URL
      formData.avatarUrl = response.data
      message.success('头像上传成功')
      showAvatarModal.value = false
      previewAvatar.value = ''
      avatarFile.value = null

      // 刷新用户信息
      await userStore.fetchCurrentUser()
      initUserInfo()
    } else {
      console.error('上传失败响应:', response)
      message.error(response?.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    message.error('头像上传失败，请重试')
  } finally {
    loading.value = false
  }
}

// 修改密码
const handlePasswordChange = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  if (passwordForm.newPassword.length < 6) {
    message.error('新密码长度不能少于6位')
    return
  }

  loading.value = true
  try {
    const response = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    if (response.code === 0) {
      message.success('密码修改成功')
      showPasswordModal.value = false
      resetPasswordForm()
    } else {
      message.error('密码修改失败，请检查原密码是否正确')
    }
  } catch (error) {
    message.error('密码修改失败')
  } finally {
    loading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

// 日期格式化
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '未知'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}

onMounted(() => {
  initUserInfo()
})
</script>

<style scoped>
.profile-container {
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

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.profile-card {
  grid-column: 1 / -1;
}

.profile-header {
  margin-bottom: 32px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-upload {
  position: relative;
  cursor: pointer;
}

.profile-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-upload:hover .avatar-overlay {
  opacity: 1;
}

.avatar-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
}

.avatar-info p {
  margin: 0 0 12px 0;
  color: #64748b;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.security-card,
.activity-card {
  height: fit-content;
}

.security-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.security-icon {
  font-size: 20px;
  color: #667eea;
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

.activity-item {
  padding-left: 8px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.activity-title {
  font-weight: 500;
  color: #1e293b;
}

.activity-time {
  font-size: 14px;
  color: #64748b;
}

.activity-desc {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.avatar-upload-content {
  text-align: center;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #64748b;
}

.upload-tips {
  margin-top: 16px;
}

.upload-tips p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .header-content {
    padding: 0 16px;
  }

  .header-actions {
    gap: 8px;
  }
}
</style>