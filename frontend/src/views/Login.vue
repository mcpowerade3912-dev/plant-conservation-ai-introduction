<template>
  <div class="login-page">
    <section class="login-intro">
      <h1>{{ settings.systemName }}</h1>
      <p>面向植物引种、鉴定审核与保育档案管理的综合实践系统</p>
      <div class="feature-list">
        <div class="feature">引种记录管理</div>
        <div class="feature">鉴定审核流程</div>
        <div class="feature">植物档案管理</div>
        <div class="feature">分级权限控制</div>
      </div>
    </section>
    <section class="login-form">
      <div class="login-box">
        <h2>欢迎登录</h2>
        <p class="muted">请使用测试账号登录系统</p>
        <el-form @submit.prevent="submit">
          <el-form-item label="用户名"><el-input v-model="username" placeholder="请输入用户名" /></el-form-item>
          <el-form-item label="密码"><el-input v-model="password" type="password" show-password placeholder="请输入密码" @keyup.enter="submit" /></el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="submit">登录</el-button>
        </el-form>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useAuth } from '../stores/auth';
import { useSettings } from '../stores/settings';

const username = ref('admin');
const password = ref('123456');
const loading = ref(false);
const auth = useAuth();
const settings = useSettings();
const router = useRouter();
onMounted(() => settings.load());

async function submit() {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }
  loading.value = true;
  try {
    await auth.login(username.value, password.value);
    ElMessage.success('登录成功');
    router.push('/dashboard');
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || error.message || '登录失败');
  } finally {
    loading.value = false;
  }
}
</script>
