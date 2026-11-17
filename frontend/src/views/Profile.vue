<template>
  <div class="page-card" style="max-width:680px">
    <h3>个人资料</h3>
    <el-form :model="form" label-width="90px">
      <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
      <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="新密码"><el-input v-model="form.password" type="password" show-password placeholder="不修改请留空" /></el-form-item>
      <el-button type="primary" @click="save">保存资料</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import http from '../api/http';
import { useAuth } from '../stores/auth';

const auth = useAuth();
const form = reactive({ username: auth.user?.username || '', nickname: auth.user?.nickname || '', phone: auth.user?.phone || '', email: auth.user?.email || '', password: '' });

async function save() {
  const response = await http.put('/users/me', {
    nickname: form.nickname, phone: form.phone, email: form.email, password: form.password
  });
  auth.setUser(response.data.data);
  form.password = '';
  ElMessage.success('资料已更新');
}
</script>
