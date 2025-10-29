// API响应基础类型
export interface BaseResponse<T> {
  code: number
  data: T
  message: string
  description: string
}

// 用户相关类型
export interface CurrentUser {
  id: number
  username: string
  userAccount: string
  avatarUrl?: string
  gender: number
  phone: string
  email: string
  userStatus: number
  planetCode: string
  userRole: number
  createTime: string
  updateTime?: string
  bio?: string
  lastLoginTime?: string
}

export interface LoginParams {
  userAccount: string
  userPassword: string
}

export interface RegisterParams {
  userAccount: string
  userPassword: string
  checkPassword: string
  planetCode: string
}

export interface LoginResult extends CurrentUser {}

export interface RegisterResult {
  id: number
}