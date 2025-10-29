<template>
  <div class="user-manage-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <h1>用户管理 - 用户导航</h1>
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
            :data-source="userList"
            :loading="loading"
            row-key="id"
            :pagination="pagination"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'avatarUrl'">
                <a-avatar :src="record.avatarUrl" :size="64" />
              </template>
              <template v-if="column.key === 'userRole'">
                <a-tag :color="record.userRole === 1 ? 'green' : 'blue'">
                  {{ record.userRole === 1 ? '管理员' : '普通用户' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a @click="handleEdit(record)">编辑</a>
                  <a-popconfirm
                    title="确定要删除这个用户吗？"
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

    <!-- 编辑用户模态框 -->
    <a-modal
      v-model:open="editModalVisible"
      title="编辑用户"
      @ok="handleSaveEdit"
      @cancel="editModalVisible = false"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.username" />
        </a-form-item>
        <a-form-item label="用户账户">
          <a-input v-model:value="editForm.userAccount" />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="editForm.phone" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="editForm.email" />
        </a-form-item>
        <a-form-item label="用户角色">
          <a-select v-model:value="editForm.userRole">
            <a-select-option :value="0">普通用户</a-select-option>
            <a-select-option :value="1">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { searchUsers, deleteUser as deleteUserApi } from '@/services/user'
import type { CurrentUser } from '@/types/user'
import type { TableProps } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const userList = ref<CurrentUser[]>([])
const editModalVisible = ref(false)
const editForm = reactive<Partial<CurrentUser>>({})

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
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    copyable: true
  },
  {
    title: '用户账户',
    dataIndex: 'userAccount',
    key: 'userAccount',
    copyable: true
  },
  {
    title: '头像',
    key: 'avatarUrl',
    width: 100
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 80
  },
  {
    title: '电话',
    dataIndex: 'phone',
    key: 'phone',
    copyable: true
  },
  {
    title: '邮件',
    dataIndex: 'email',
    key: 'email',
    copyable: true
  },
  {
    title: '状态',
    dataIndex: 'userStatus',
    key: 'userStatus',
    width: 80
  },
  {
    title: '星球编号',
    dataIndex: 'planetCode',
    key: 'planetCode'
  },
  {
    title: '角色',
    key: 'userRole',
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

const fetchUserList = async () => {
  loading.value = true
  try {
    const response = await searchUsers()
    if (response.code === 0 && response.data) {
      userList.value = response.data
      pagination.total = response.data.length
    }
  } catch (error) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange: TableProps['onChange'] = (pag) => {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
}

const handleEdit = (record: CurrentUser) => {
  Object.assign(editForm, record)
  editModalVisible.value = true
}

const handleSaveEdit = () => {
  // 这里应该调用更新用户信息的API
  message.success('用户信息更新成功')
  editModalVisible.value = false
  fetchUserList()
}

const handleDelete = async (id: number) => {
  try {
    const response = await deleteUserApi(id)
    if (response.code === 0) {
      message.success('删除成功')
      fetchUserList()
    } else {
      message.error('删除失败')
    }
  } catch (error) {
    message.error('删除失败')
  }
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-manage-container {
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