<template>
  <div class="app-shell">
    <aside class="app-side">
      <div class="side-brand">{{ settings.systemName }}</div>
      <el-menu router :default-active="$route.path" background-color="#163c36" text-color="#fff" active-text-color="#9ee6c3">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><span>系统首页</span></el-menu-item>
        <el-menu-item index="/introductions"><el-icon><List /></el-icon><span>引种记录</span></el-menu-item>
        <el-menu-item index="/plants" v-if="auth.role !== 'USER'"><el-icon><Grid /></el-icon><span>植物管理</span></el-menu-item>
        <el-menu-item index="/reviews" v-if="auth.role !== 'USER'"><el-icon><Checked /></el-icon><span>鉴定审核</span></el-menu-item>
        <el-menu-item index="/users" v-if="auth.role === 'ADMIN'"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
        <el-menu-item index="/settings" v-if="auth.role === 'ADMIN'"><el-icon><Setting /></el-icon><span>系统设置</span></el-menu-item>
        <el-menu-item index="/profile"><el-icon><Setting /></el-icon><span>个人资料</span></el-menu-item>
      </el-menu>
    </aside>
    <main class="app-main">
      <header class="topbar">
        <div><h2>{{ title }}</h2><div class="muted">{{ auth.user?.nickname }} · {{ auth.role }}</div></div>
        <el-button @click="logout">退出登录</el-button>
      </header>
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuth } from '../stores/auth';
import { useSettings } from '../stores/settings';

const route = useRoute();
const router = useRouter();
const auth = useAuth();
const settings = useSettings();
onMounted(() => settings.load());
const title = computed(() => ({
  dashboard: '系统首页', profile: '个人资料', users: '用户管理',
  introductions: '引种记录', reviews: '鉴定审核', plants: '植物管理',
  settings: '系统设置'
})[String(route.name)] || settings.systemName);

function logout() {
  auth.logout();
  router.push('/login');
}
</script>
