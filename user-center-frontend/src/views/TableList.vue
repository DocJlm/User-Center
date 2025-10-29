<template>
  <div class="table-list-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <h1>数据表格 - 用户导航</h1>
          <div class="header-actions">
            <a-button @click="$router.push('/welcome')">返回首页</a-button>
            <a-button @click="handleLogout">退出登录</a-button>
          </div>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <div class="table-container">
          <a-table
            :columns="columns"
            :data-source="dataList"
            :loading="loading"
            row-key="id"
            :pagination="pagination"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-badge :status="record.status === 'active' ? 'success' : 'default'" />
                {{ record.status === 'active' ? '活跃' : '非活跃' }}
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a @click="handleView(record)">查看</a>
                  <a @click="handleEdit(record)">编辑</a>
                  <a-popconfirm
                    title="确定要删除这条记录吗？"
                    @confirm="handleDelete(record.id)"
                  >
                    <a style="color: #ff4d4f">删除</a>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </div>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import type { TableProps } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const dataList = ref<any[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条数据`
})

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: '状态',
    key: 'status',
    width: 100
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 150
  }
]

const fetchDataList = async () => {
  loading.value = true
  try {
    // 模拟数据，实际应该调用真实的API
    const mockData = Array.from({ length: 50 }, (_, index) => ({
      id: index + 1,
      name: `数据项 ${index + 1}`,
      description: `这是数据项 ${index + 1} 的描述信息`,
      status: index % 2 === 0 ? 'active' : 'inactive',
      createTime: new Date(Date.now() - Math.random() * 10000000000).toISOString()
    }))

    dataList.value = mockData
    pagination.total = mockData.length
  } catch (error) {
    message.error('获取数据列表失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange: TableProps['onChange'] = (pag) => {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
}

const handleView = (record: any) => {
  message.info(`查看记录: ${record.name}`)
}

const handleEdit = (record: any) => {
  message.info(`编辑记录: ${record.name}`)
}

const handleDelete = (id: number) => {
  message.success('删除成功')
  fetchDataList()
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}

onMounted(() => {
  fetchDataList()
})
</script>

<style scoped>
.table-list-container {
  min-height: 100vh;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
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

.header-content h1 {
  margin: 0;
  font-size: 20px;
  color: #1890ff;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.content {
  padding: 24px;
  background: #f0f2f5;
}

.table-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>