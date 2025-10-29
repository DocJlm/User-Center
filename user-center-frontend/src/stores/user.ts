import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { CurrentUser } from '@/types/user'
import { login as loginApi, logout as logoutApi, getCurrentUser as getCurrentUserApi } from '@/services/user'

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<CurrentUser | null>(null)
  const isLoggedIn = ref(false)

  // 登录
  const login = async (loginParams: { userAccount: string; userPassword: string }) => {
    try {
      const response = await loginApi(loginParams)
      if (response.code === 0 && response.data) {
        currentUser.value = response.data
        isLoggedIn.value = true
        return true
      }
      return false
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('Logout failed:', error)
    } finally {
      currentUser.value = null
      isLoggedIn.value = false
    }
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    try {
      const response = await getCurrentUserApi()
      if (response.code === 0 && response.data) {
        currentUser.value = response.data
        isLoggedIn.value = true
        return true
      }
      return false
    } catch (error) {
      console.error('Fetch current user failed:', error)
      return false
    }
  }

  return {
    currentUser,
    isLoggedIn,
    login,
    logout,
    fetchCurrentUser
  }
})