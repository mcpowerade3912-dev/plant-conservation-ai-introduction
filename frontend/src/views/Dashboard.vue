<template>
  <div>
    <div class="stat-grid">
      <div class="stat"><span class="muted">用户总数</span><strong>{{ stats.users }}</strong></div>
      <div class="stat"><span class="muted">引种记录</span><strong>{{ stats.introductions }}</strong></div>
      <div class="stat"><span class="muted">待鉴定</span><strong>{{ stats.pending }}</strong></div>
      <div class="stat"><span class="muted">在库植物</span><strong>{{ stats.plants }}</strong></div>
    </div>
    <div class="page-card" style="margin-top:20px">
      <h3>系统概览</h3>
      <p class="muted">欢迎，{{ auth.user?.nickname }}。当前角色为{{ auth.role }}。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import http from '../api/http';
import { useAuth } from '../stores/auth';

const auth = useAuth();
const stats = reactive({ users: 0, introductions: 0, pending: 0, plants: 0 });
onMounted(async () => {
  const response = await http.get('/dashboard/stats');
  Object.assign(stats, response.data.data);
});
</script>
