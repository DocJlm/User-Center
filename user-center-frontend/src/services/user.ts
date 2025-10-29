import request from './request'
import type {
  BaseResponse,
  CurrentUser,
  LoginParams,
  RegisterParams,
  LoginResult,
  RegisterResult
} from '@/types/user'

// 用户登录
export const login = (params: LoginParams): Promise<BaseResponse<LoginResult>> => {
  return request.post('/user/login', params)
}

// 用户注册
export const register = (params: RegisterParams): Promise<BaseResponse<RegisterResult>> => {
  return request.post('/user/register', params)
}

// 用户登出
export const logout = (): Promise<BaseResponse<number>> => {
  return request.post('/user/logout')
}

// 获取当前用户
export const getCurrentUser = (): Promise<BaseResponse<CurrentUser>> => {
  return request.get('/user/current')
}

// 搜索用户
export const searchUsers = (username?: string): Promise<BaseResponse<CurrentUser[]>> => {
  return request.get('/user/search', { params: { username } })
}

// 删除用户
export const deleteUser = (id: number): Promise<BaseResponse<boolean>> => {
  return request.post('/user/delete', id)
}

// 更新用户信息
export const updateUserInfo = (userInfo: Partial<CurrentUser>): Promise<BaseResponse<CurrentUser>> => {
  return request.put('/user/update', userInfo)
}

// 修改密码
export const changePassword = (passwordData: { oldPassword: string; newPassword: string }): Promise<BaseResponse<boolean>> => {
  return request.post('/user/change-password', passwordData)
}

// 上传头像
export const uploadAvatar = (file: File): Promise<BaseResponse<string>> => {
  const formData = new FormData()
  formData.append('avatar', file)
  return request.post('/user/upload-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取用户活动记录
export const getUserActivities = (userId: number): Promise<BaseResponse<any[]>> => {
  return request.get(`/user/activities/${userId}`)
}

// 获取系统统计信息
export const getSystemStats = (): Promise<BaseResponse<any>> => {
  return request.get('/system/stats')
}