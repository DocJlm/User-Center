<template>
  <div class="stats-container">
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
      <div class="stats-content">
        <!-- 统计概览 -->
        <div class="stats-overview">
          <h2>数据统计概览</h2>
          <div class="overview-cards">
            <div class="overview-card">
              <div class="card-icon">
                <UserOutlined />
              </div>
              <div class="card-content">
                <div class="card-number">{{ stats.totalUsers }}</div>
                <div class="card-label">总用户数</div>
                <div class="card-trend up">
                  <ArrowUpOutlined />
                  <span>+{{ stats.userGrowth }}%</span>
                </div>
              </div>
            </div>

            <div class="overview-card">
              <div class="card-icon">
                <TeamOutlined />
              </div>
              <div class="card-content">
                <div class="card-number">{{ stats.activeUsers }}</div>
                <div class="card-label">活跃用户</div>
                <div class="card-trend up">
                  <ArrowUpOutlined />
                  <span>+{{ stats.activeGrowth }}%</span>
                </div>
              </div>
            </div>

            <div class="overview-card">
              <div class="card-icon">
                <LoginOutlined />
              </div>
              <div class="card-content">
                <div class="card-number">{{ stats.todayLogins }}</div>
                <div class="card-label">今日登录</div>
                <div class="card-trend down">
                  <ArrowDownOutlined />
                  <span>-{{ stats.loginGrowth }}%</span>
                </div>
              </div>
            </div>

            <div class="overview-card">
              <div class="card-icon">
                <ClockCircleOutlined />
              </div>
              <div class="card-content">
                <div class="card-number">{{ stats.avgOnlineTime }}</div>
                <div class="card-label">平均在线时长</div>
                <div class="card-trend up">
                  <ArrowUpOutlined />
                  <span>+{{ stats.timeGrowth }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
          <a-row :gutter="24">
            <!-- 用户增长趋势 -->
            <a-col :span="16">
              <a-card title="用户增长趋势" class="chart-card">
                <div ref="userGrowthChart" class="chart-container"></div>
              </a-card>
            </a-col>

            <!-- 用户角色分布 -->
            <a-col :span="8">
              <a-card title="用户角色分布" class="chart-card">
                <div ref="roleDistributionChart" class="chart-container"></div>
              </a-card>
            </a-col>
          </a-row>

          <a-row :gutter="24" style="margin-top: 24px;">
            <!-- 登录统计 -->
            <a-col :span="12">
              <a-card title="登录统计" class="chart-card">
                <div ref="loginStatsChart" class="chart-container"></div>
              </a-card>
            </a-col>

            <!-- 活跃度统计 -->
            <a-col :span="12">
              <a-card title="用户活跃度" class="chart-card">
                <div ref="activityChart" class="chart-container"></div>
              </a-card>
            </a-col>
          </a-row>
        </div>

        <!-- 详细数据表格 -->
        <div class="data-table-section">
          <a-card title="用户活动详情" class="table-card">
            <template #extra>
              <a-space>
                <a-range-picker v-model:value="dateRange" @change="handleDateChange" />
                <a-button @click="refreshData">刷新数据</a-button>
                <a-button type="primary" @click="exportData">导出数据</a-button>
              </a-space>
            </template>

            <a-table
              :columns="tableColumns"
              :data-source="tableData"
              :loading="loading"
              :pagination="pagination"
              @change="handleTableChange"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'avatar'">
                  <a-avatar :src="record.avatar" :size="32" />
                </template>
                <template v-if="column.key === 'status'">
                  <a-badge :status="record.status === 'active' ? 'success' : 'default'" />
                  {{ record.status === 'active' ? '活跃' : '离线' }}
                </template>
                <template v-if="column.key === 'lastLogin'">
                  {{ formatDateTime(record.lastLogin) }}
                </template>
                <template v-if="column.key === 'action'">
                  <a @click="viewUserDetail(record)">查看详情</a>
                </template>
              </template>
            </a-table>
          </a-card>
        </div>
      </div>
    </a-layout-content>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  TeamOutlined,
  LoginOutlined,
  ClockCircleOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { getSystemStats } from '@/services/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const dateRange = ref<any[]>([])

// 统计数据
const stats = reactive({
  totalUsers: 1234,
  userGrowth: 12.5,
  activeUsers: 856,
  activeGrowth: 8.2,
  todayLogins: 234,
  loginGrowth: 3.1,
  avgOnlineTime: '2.5h',
  timeGrowth: 15.3
})

// 表格数据
const tableData = ref([
  {
    id: 1,
    username: '张三',
    email: 'zhangsan@example.com',
    avatar: '/avatar1.jpg',
    status: 'active',
    lastLogin: '2025-10-29T10:30:00Z',
    loginCount: 45,
    onlineTime: '3.2h'
  },
  {
    id: 2,
    username: '李四',
    email: 'lisi@example.com',
    avatar: '/avatar2.jpg',
    status: 'inactive',
    lastLogin: '2025-10-28T15:20:00Z',
    loginCount: 23,
    onlineTime: '1.8h'
  },
  {
    id: 3,
    username: '王五',
    email: 'wangwu@example.com',
    avatar: '/avatar3.jpg',
    status: 'active',
    lastLogin: '2025-10-29T09:15:00Z',
    loginCount: 67,
    onlineTime: '4.1h'
  }
])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条数据`
})

const tableColumns = [
  {
    title: '用户',
    key: 'avatar',
    width: 80
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email'
  },
  {
    title: '状态',
    key: 'status',
    width: 100
  },
  {
    title: '最后登录',
    key: 'lastLogin',
    width: 180
  },
  {
    title: '登录次数',
    dataIndex: 'loginCount',
    key: 'loginCount',
    width: 100
  },
  {
    title: '在线时长',
    dataIndex: 'onlineTime',
    key: 'onlineTime',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    width: 100
  }
]

// 图表DOM引用
const userGrowthChart = ref<HTMLDivElement>()
const roleDistributionChart = ref<HTMLDivElement>()
const loginStatsChart = ref<HTMLDivElement>()
const activityChart = ref<HTMLDivElement>()

// 模拟图表数据
const initCharts = () => {
  nextTick(() => {
    // 这里可以使用真实的图表库如 ECharts 或 Chart.js
    // 现在只是简单的占位符
    if (userGrowthChart.value) {
      userGrowthChart.value.innerHTML = '<div style="height: 300px; display: flex; align-items: center; justify-content: center; color: #999;">用户增长趋势图表</div>'
    }
    if (roleDistributionChart.value) {
      roleDistributionChart.value.innerHTML = '<div style="height: 300px; display: flex; align-items: center; justify-content: center; color: #999;">用户角色分布图表</div>'
    }
    if (loginStatsChart.value) {
      loginStatsChart.value.innerHTML = '<div style="height: 300px; display: flex; align-items: center; justify-content: center; color: #999;">登录统计图表</div>'
    }
    if (activityChart.value) {
      activityChart.value.innerHTML = '<div style="height: 300px; display: flex; align-items: center; justify-content: center; color: #999;">用户活跃度图表</div>'
    }
  })
}

// 获取统计数据
const fetchStats = async () => {
  loading.value = true
  try {
    // 从session获取当前用户信息
    const currentUser = userStore.currentUser

    // 模拟真实API调用
    const response = await getSystemStats()
    console.log('统计数据响应:', response)

    if (response && response.code === 0 && response.data) {
      // 更新统计数据
      Object.assign(stats, response.data)

      // 更新表格数据
      if (response.data.userList) {
        tableData.value = response.data.userList.map((user: any, index: number) => ({
          id: user.id,
          username: user.username,
          email: user.email,
          avatar: user.avatarUrl || '/default-avatar.png',
          status: user.userStatus === 1 ? 'active' : 'inactive',
          lastLogin: user.lastLoginTime || user.updateTime,
          loginCount: Math.floor(Math.random() * 100) + 1, // 模拟登录次数
          onlineTime: `${(Math.random() * 5 + 0.5).toFixed(1)}h` // 模拟在线时长
        }))
        pagination.total = response.data.userList.length
      }
    } else {
      // 如果API调用失败，使用模拟数据
      console.log('使用模拟统计数据')
      generateMockData()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 出错时使用模拟数据
    generateMockData()
  } finally {
    loading.value = false
  }
}

// 生成模拟数据
const generateMockData = () => {
  stats.totalUsers = Math.floor(Math.random() * 2000) + 1000
  stats.userGrowth = (Math.random() * 20 + 5).toFixed(1)
  stats.activeUsers = Math.floor(stats.totalUsers * (Math.random() * 0.3 + 0.5))
  stats.activeGrowth = (Math.random() * 15 + 3).toFixed(1)
  stats.todayLogins = Math.floor(Math.random() * 100 + 50)
  stats.loginGrowth = (Math.random() * 10 - 5).toFixed(1)
  stats.avgOnlineTime = `${(Math.random() * 3 + 1).toFixed(1)}h`
  stats.timeGrowth = (Math.random() * 20 + 5).toFixed(1)

  // 生成模拟用户数据
  const mockUsers = [
    { username: '张三', email: 'zhangsan@example.com', status: 'active' },
    { username: '李四', email: 'lisi@example.com', status: 'inactive' },
    { username: '王五', email: 'wangwu@example.com', status: 'active' },
    { username: '赵六', email: 'zhaoliu@example.com', status: 'active' },
    { username: '钱七', email: 'qianqi@example.com', status: 'inactive' }
  ]

  tableData.value = mockUsers.map((user, index) => ({
    id: index + 1,
    username: user.username,
    email: user.email,
    avatar: `/avatar${index + 1}.jpg`,
    status: user.status,
    lastLogin: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
    loginCount: Math.floor(Math.random() * 100) + 1,
    onlineTime: `${(Math.random() * 5 + 0.5).toFixed(1)}h`
  }))

  pagination.total = tableData.value.length
}

// 刷新数据
const refreshData = () => {
  loading.value = true
  setTimeout(() => {
    fetchStats()
    loading.value = false
    message.success('数据已刷新')
  }, 1000)
}

// 导出数据
const exportData = () => {
  message.info('数据导出功能开发中...')
}

// 日期范围变化
const handleDateChange = (dates: any) => {
  console.log('日期范围变化:', dates)
  refreshData()
}

// 表格变化
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  // 重新加载数据
}

// 查看用户详情
const viewUserDetail = (record: any) => {
  message.info(`查看用户 ${record.username} 的详细信息`)
}

// 日期格式化
const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}

onMounted(() => {
  fetchStats()
  initCharts()
})
</script>

<style scoped>
.stats-container {
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

.stats-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.stats-overview {
  margin-bottom: 32px;
}

.stats-overview h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.card-content {
  flex: 1;
}

.card-number {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.card-trend.up {
  color: #10b981;
}

.card-trend.down {
  color: #ef4444;
}

.charts-section {
  margin-bottom: 32px;
}

.chart-card {
  height: 100%;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.data-table-section {
  margin-bottom: 32px;
}

.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .overview-cards {
    grid-template-columns: 1fr;
  }

  .header-content {
    padding: 0 16px;
  }

  .header-actions {
    gap: 8px;
  }

  .stats-content {
    padding: 0 16px;
  }
}
</style>