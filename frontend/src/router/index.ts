import { createRouter, createWebHistory } from 'vue-router';
import { useAuth } from '../stores/auth';
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/', redirect: '/dashboard' },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'profile', component: () => import('../views/Profile.vue') },
      { path: 'users', component: () => import('../views/Users.vue'), meta: { roles: ['ADMIN'] } },
      { path: 'introductions', component: () => import('../views/Introductions.vue') },
      { path: 'reviews', component: () => import('../views/Reviews.vue'), meta: { roles: ['ADMIN', 'REVIEWER'] } },
      { path: 'plants', component: () => import('../views/Plants.vue'), meta: { roles: ['ADMIN', 'REVIEWER'] } },
      { path: 'settings', component: () => import('../views/Settings.vue'), meta: { roles: ['ADMIN'] } }
    ]
  }
];

const router = createRouter({ history: createWebHistory(), routes });

router.beforeEach(to => {
  const auth = useAuth();
  if (!auth.isLogin && to.path !== '/login') return '/login';
  if (auth.isLogin && to.path === '/login') return '/dashboard';
  const roles = to.meta?.roles as string[] | undefined;
  if (roles && auth.role && !roles.includes(auth.role)) return '/dashboard';
  return true;
});

export default router;
