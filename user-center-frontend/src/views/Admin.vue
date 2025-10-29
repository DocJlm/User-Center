<template>
  <div class="admin-container">
    <a-layout>
      <a-layout-sider v-model:collapsed="collapsed" :trigger="null" collapsible>
        <div class="logo" />
        <a-menu
          v-model:selectedKeys="selectedKeys"
          theme="dark"
          mode="inline"
          @click="handleMenuClick"
        >
          <a-menu-item key="user-manage">
            <UserOutlined />
            <span>用户管理</span>
          </a-menu-item>
          <a-menu-item key="data-manage">
            <TableOutlined />
            <span>数据管理</span>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout>
        <a-layout-header style="background: #fff; padding: 0">
          <a-button
            type="text"
            :icon="h(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined)"
            @click="() => (collapsed = !collapsed)"
          />
          <div class="header-right">
            <a-avatar :src="userStore.currentUser?.avatarUrl" />
            <span>{{ userStore.currentUser?.username }}</span>
            <a-button @click="handleLogout">退出登录</a-button>
          </div>
        </a-layout-header>
        <a-layout-content :style="{ margin: '24px 16px', padding: '24px', background: '#fff' }">
          <router-view />
        </a-layout-content>
      </a-layout>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  UserOutlined,
  TableOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const selectedKeys = computed(() => [route.name as string])

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(`/admin/${key}`)
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/welcome')
}
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
}

.logo {
  height: 32px;
  margin: 16px;
  background: rgba(255, 255, 255, 0.3);
}

.header-right {
  float: right;
  display: flex;
  align-items: center;
  gap: 16px;
  padding-right: 24px;
}
</style>