import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/welcome'
  },
  {
    path: '/welcome',
    name: 'Welcome',
    component: () => import('@/views/Welcome.vue')
  },
  {
    path: '/user',
    children: [
      {
        path: '/user/login',
        name: 'Login',
        component: () => import('@/views/user/Login.vue')
      },
      {
        path: '/user/register',
        name: 'Register',
        component: () => import('@/views/user/Register.vue')
      }
    ]
  },
  {
    path: '/profile',
    name: 'Profile',
    meta: { requiresAuth: true },
    component: () => import('@/views/Profile.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    meta: { requiresAuth: true },
    component: () => import('@/views/Settings.vue')
  },
  {
    path: '/stats',
    name: 'Stats',
    meta: { requiresAuth: true },
    component: () => import('@/views/Stats.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    meta: { requiresAuth: true },
    component: () => import('@/views/Messages.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('@/views/Admin.vue'),
    children: [
      {
        path: '/admin/user-manage',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue')
      },
      {
        path: '/admin/system-settings',
        name: 'SystemSettings',
        component: () => import('@/views/admin/SystemSettings.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = !!userStore.currentUser
  const isAdmin = userStore.currentUser?.userRole === 1

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/user/login')
    return
  }

  if (to.meta.requiresAdmin && !isAdmin) {
    next('/welcome')
    return
  }

  next()
})

export default router