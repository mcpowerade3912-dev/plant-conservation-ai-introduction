<template>
  <div class="page-card">
    <div class="toolbar">
      <el-button type="primary" @click="open()"><el-icon><Plus /></el-icon>新增用户</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="username" label="用户名" min-width="100" />
      <el-table-column prop="nickname" label="昵称" min-width="110" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="160" />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="170">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑用户' : '新增用户'">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role"><el-option label="管理员" value="ADMIN" /><el-option label="审核员" value="REVIEWER" /><el-option label="普通用户" value="USER" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" :value="1" /><el-option label="停用" :value="0" /></el-select></el-form-item>
        <el-form-item label="初始密码"><el-input v-model="form.password" type="password" show-password placeholder="新增时默认 123456" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import http from '../api/http';
import type { User } from '../types';

const rows = ref<User[]>([]);
const visible = ref(false);
const form = reactive({ id: 0, username: '', nickname: '', role: 'USER', status: 1, password: '' });

async function load() {
  const response = await http.get('/users');
  rows.value = response.data.data;
}
function open(row?: User) {
  Object.assign(form, row ? { ...row, password: '' } : { id: 0, username: '', nickname: '', role: 'USER', status: 1, password: '' });
  visible.value = true;
}
async function save() {
  const payload = { username: form.username, nickname: form.nickname, role: form.role, status: form.status, password: form.password };
  if (form.id) await http.put('/users/' + form.id, payload);
  else await http.post('/users', payload);
  visible.value = false;
  ElMessage.success('保存成功');
  await load();
}
async function remove(id: number) {
  await ElMessageBox.confirm('确定删除该用户吗？', '删除确认', { type: 'warning' });
  await http.delete('/users/' + id);
  ElMessage.success('删除成功');
  await load();
}
onMounted(load);
</script>
