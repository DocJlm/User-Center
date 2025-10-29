<template>
  <div class="welcome-container">
    <!-- 顶部导航栏 -->
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo-section">
          <img alt="logo" src="/logo.svg" class="logo" />
          <h1>用户导航 - 2025</h1>
        </div>
        <div class="header-actions">
          <template v-if="userStore.isLoggedIn">
            <a-dropdown>
              <div class="user-info">
                <a-avatar :src="userStore.currentUser?.avatarUrl || '/default-avatar.png'" :size="40" />
                <span class="username">{{ userStore.currentUser?.username }}</span>
                <DownOutlined />
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile" @click="goToProfile">
                    <UserOutlined />
                    个人中心
                  </a-menu-item>
                  <a-menu-item key="settings" @click="goToSettings">
                    <SettingOutlined />
                    账户设置
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <template v-else>
            <router-link to="/user/login">
              <a-button type="primary" size="large">登录</a-button>
            </router-link>
            <router-link to="/user/register">
              <a-button size="large">注册</a-button>
            </router-link>
          </template>
        </div>
      </div>
    </a-layout-header>

    <!-- 主要内容区域 -->
    <a-layout-content class="content">
      <!-- 英雄区域 -->
      <div class="hero-section">
        <div class="hero-bg">
          <div class="hero-particles"></div>
        </div>
        <div class="hero-content">
          <h1 class="hero-title">
            <span class="gradient-text">欢迎来到用户中心</span>
          </h1>
          <p class="hero-subtitle">
            基于 Vue 3 + TypeScript + Ant Design Vue 构建的现代化用户管理系统
          </p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">注册用户</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.onlineCount }}</div>
              <div class="stat-label">在线用户</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.todayActive }}</div>
              <div class="stat-label">今日活跃</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能卡片区域 -->
      <div class="features-section">
        <div class="section-header">
          <h2>功能特性</h2>
          <p>探索我们提供的强大功能</p>
        </div>

        <div class="feature-cards">
          <!-- 个人中心 - 所有用户可见 -->
          <div class="feature-card" @click="goToProfile">
            <div class="card-icon">
              <UserOutlined />
            </div>
            <h3>个人中心</h3>
            <p>管理个人信息，修改头像、昵称，查看账户状态和活动记录</p>
            <div class="card-action">
              <span>进入个人中心</span>
              <ArrowRightOutlined />
            </div>
          </div>

          <!-- 数据统计 - 所有用户可见 -->
          <div class="feature-card" @click="goToStats">
            <div class="card-icon">
              <BarChartOutlined />
            </div>
            <h3>数据统计</h3>
            <p>查看个人数据分析、活动统计和使用情况报告</p>
            <div class="card-action">
              <span>查看统计</span>
              <ArrowRightOutlined />
            </div>
          </div>

          <!-- 消息中心 - 所有用户可见 -->
          <div class="feature-card" @click="goToMessages">
            <div class="card-icon">
              <MessageOutlined />
            </div>
            <h3>消息中心</h3>
            <p>接收系统通知、查看消息记录和管理消息设置</p>
            <div class="card-action">
              <span>查看消息</span>
              <ArrowRightOutlined />
            </div>
          </div>

          <!-- 用户管理 - 仅管理员可见 -->
          <div v-if="userStore.currentUser?.userRole === 1" class="feature-card admin-card" @click="goToUserManage">
            <div class="card-icon admin-icon">
              <TeamOutlined />
            </div>
            <h3>用户管理</h3>
            <p>管理系统用户、查看用户列表、编辑用户信息和权限设置</p>
            <div class="card-action">
              <span>进入管理</span>
              <ArrowRightOutlined />
            </div>
            <div class="admin-badge">管理员</div>
          </div>

          <!-- 系统设置 - 仅管理员可见 -->
          <div v-if="userStore.currentUser?.userRole === 1" class="feature-card admin-card" @click="goToSystemSettings">
            <div class="card-icon admin-icon">
              <SettingOutlined />
            </div>
            <h3>系统设置</h3>
            <p>配置系统参数、管理权限设置和系统维护</p>
            <div class="card-action">
              <span>系统设置</span>
              <ArrowRightOutlined />
            </div>
            <div class="admin-badge">管理员</div>
          </div>

          <!-- API文档 - 所有用户可见 -->
          <div class="feature-card" @click="goToApiDocs">
            <div class="card-icon">
              <ApiOutlined />
            </div>
            <h3>API文档</h3>
            <p>查看API接口文档、了解接口使用方法和示例</p>
            <div class="card-action">
              <span>查看文档</span>
              <ArrowRightOutlined />
            </div>
          </div>
        </div>
      </div>

      <!-- 最新动态区域 -->
      <div class="news-section">
        <div class="section-header">
          <h2>最新动态</h2>
          <p>了解平台最新功能和更新</p>
        </div>
        <div class="news-cards">
          <div v-for="news in newsList" :key="news.id" class="news-card">
            <div class="news-date">{{ formatDate(news.createTime) }}</div>
            <h3>{{ news.title }}</h3>
            <p>{{ news.content }}</p>
            <a-tag :color="news.type === 'feature' ? 'blue' : news.type === 'update' ? 'green' : 'orange'">
              {{ news.type === 'feature' ? '新功能' : news.type === 'update' ? '更新' : '公告' }}
            </a-tag>
          </div>
        </div>
      </div>
    </a-layout-content>

    <!-- 底部 -->
    <a-layout-footer class="footer">
      <div class="footer-content">
        <div class="footer-info">
          <div class="footer-logo">
            <img alt="logo" src="/logo.svg" class="logo" />
            <span>用户导航 2025</span>
          </div>
          <p>基于现代化技术栈构建的用户管理系统</p>
        </div>
        <div class="footer-links">
          <a href="https://www.zqcblog.com" target="_blank" rel="noopener noreferrer">
            <GlobalOutlined />
            我的博客
          </a>
          <a href="https://github.com" target="_blank" rel="noopener noreferrer">
            <GithubOutlined />
            GitHub
          </a>
          <a href="/docs" target="_blank" rel="noopener noreferrer">
            <BookOutlined />
            文档
          </a>
        </div>
      </div>
    </a-layout-footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
  DownOutlined,
  BarChartOutlined,
  MessageOutlined,
  TeamOutlined,
  ApiOutlined,
  ArrowRightOutlined,
  GlobalOutlined,
  GithubOutlined,
  BookOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 统计数据
const stats = ref({
  userCount: 1234,
  onlineCount: 56,
  todayActive: 234
})

// 最新动态
const newsList = ref([
  {
    id: 1,
    title: '个人中心功能上线',
    content: '现在您可以修改个人头像、昵称等个人信息了',
    type: 'feature',
    createTime: '2025-10-29T10:00:00Z'
  },
  {
    id: 2,
    title: '系统性能优化',
    content: '页面加载速度提升50%，用户体验更佳',
    type: 'update',
    createTime: '2025-10-28T15:30:00Z'
  },
  {
    id: 3,
    title: '数据统计功能更新',
    content: '新增了更多数据分析图表和导出功能',
    type: 'update',
    createTime: '2025-10-27T09:15:00Z'
  }
])

// 日期格式化
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 页面跳转方法
const goToProfile = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }
  router.push('/profile')
}

const goToSettings = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }
  router.push('/settings')
}

const goToStats = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }
  router.push('/stats')
}

const goToMessages = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }
  router.push('/messages')
}

const goToUserManage = () => {
  if (userStore.currentUser?.userRole !== 1) {
    message.error('需要管理员权限')
    return
  }
  router.push('/admin/user-manage')
}

const goToSystemSettings = () => {
  if (userStore.currentUser?.userRole !== 1) {
    message.error('需要管理员权限')
    return
  }
  router.push('/admin/system-settings')
}

const goToApiDocs = () => {
  window.open('https://www.zqcblog.com', '_blank')
}

const handleLogout = async () => {
  await userStore.logout()
  message.success('已退出登录')
}

onMounted(() => {
  // 模拟获取统计数据
  setTimeout(() => {
    stats.value = {
      userCount: Math.floor(Math.random() * 2000) + 1000,
      onlineCount: Math.floor(Math.random() * 100) + 20,
      todayActive: Math.floor(Math.random() * 500) + 100
    }
  }, 1000)
})
</script>

<style scoped>
.welcome-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 顶部导航栏 */
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
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-weight: 500;
  color: #333;
}

/* 主要内容区域 */
.content {
  padding-top: 64px;
  background: #f8fafc;
}

/* 英雄区域 */
.hero-section {
  position: relative;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.3;
}

.hero-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(circle at 20% 35%, rgba(255,255,255,0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 25%, rgba(255,255,255,0.1) 0%, transparent 50%),
    radial-gradient(circle at 50% 75%, rgba(255,255,255,0.1) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.hero-content {
  position: relative;
  text-align: center;
  color: white;
  z-index: 1;
  max-width: 800px;
  padding: 0 24px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 24px;
  line-height: 1.2;
}

.gradient-text {
  background: linear-gradient(45deg, #fff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 48px;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  opacity: 0.8;
}

/* 功能区域 */
.features-section {
  padding: 80px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-header h2 {
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 16px;
}

.section-header p {
  font-size: 18px;
  color: #64748b;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  border: 1px solid #e2e8f0;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.admin-card {
  border-left: 4px solid #667eea;
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  font-size: 28px;
  color: white;
}

.admin-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.feature-card p {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 24px;
  flex: 1;
}

.card-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #667eea;
  font-weight: 500;
}

.admin-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

/* 最新动态区域 */
.news-section {
  padding: 80px 24px;
  background: white;
}

.news-cards {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.news-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e2e8f0;
}

.news-date {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 12px;
}

.news-card h3 {
  color: #1e293b;
  margin-bottom: 12px;
  font-size: 18px;
}

.news-card p {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 16px;
}

/* 底部 */
.footer {
  background: #1e293b;
  color: white;
  padding: 48px 24px 24px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 32px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.footer-logo .logo {
  width: 32px;
  height: 32px;
}

.footer-logo span {
  font-size: 18px;
  font-weight: 600;
}

.footer-info p {
  color: #94a3b8;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: #94a3b8;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-stats {
    flex-direction: column;
    gap: 24px;
  }

  .feature-cards {
    grid-template-columns: 1fr;
  }

  .footer-content {
    flex-direction: column;
    text-align: center;
  }

  .header-content {
    padding: 0 16px;
  }

  .header-actions {
    gap: 8px;
  }

  .username {
    display: none;
  }
}
</style>