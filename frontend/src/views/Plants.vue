<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索植物名称或来源" clearable style="width:220px" />
      <el-button type="primary" @click="open()"><el-icon><Plus /></el-icon>新增植物</el-button>
    </div>
    <el-table :data="filtered" border>
      <el-table-column label="图片" width="86">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" fit="cover" class="table-plant-image" :preview-src-list="[row.imageUrl]" preview-teleported />
          <span v-else class="image-placeholder">无图</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="植物名称" min-width="110" />
      <el-table-column prop="scientificName" label="学名" min-width="130" />
      <el-table-column prop="category" label="类别" min-width="100" />
      <el-table-column prop="origin" label="来源地" min-width="100" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '在库' : row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="170">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑植物' : '新增植物'">
      <el-form :model="form" label-width="90px">
        <el-form-item label="植物名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="学名"><el-input v-model="form.scientificName" /></el-form-item>
        <el-form-item label="类别"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="来源地"><el-input v-model="form.origin" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="1" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="在库" value="ACTIVE" /><el-option label="停用" value="INACTIVE" /></el-select></el-form-item>
        <el-form-item label="植物图片">
          <div class="image-upload-block">
            <el-upload :show-file-list="false" :http-request="uploadImage" accept="image/jpeg,image/png,image/webp">
              <div class="image-uploader">
                <el-image v-if="form.imageUrl" :src="form.imageUrl" fit="cover" />
                <div v-else class="image-upload-empty"><span>+</span><small>上传图片</small></div>
              </div>
            </el-upload>
            <div class="image-upload-tip">
              <span>支持 JPG、PNG、WebP，最大 5MB</span>
              <el-button v-if="form.imageUrl" link type="danger" @click="form.imageUrl = ''">移除图片</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import http from '../api/http';
import type { Plant } from '../types';

const rows = ref<Plant[]>([]);
const keyword = ref('');
const visible = ref(false);
const form = reactive({ id: 0, name: '', scientificName: '', category: '观赏植物', origin: '', quantity: 1, status: 'ACTIVE', imageUrl: '' });
const filtered = computed(() => rows.value.filter(row => !keyword.value || row.name.includes(keyword.value) || row.origin.includes(keyword.value)));

async function load() {
  const response = await http.get('/plants');
  rows.value = response.data.data;
}
function open(row?: Plant) {
  Object.assign(form, row || { id: 0, name: '', scientificName: '', category: '观赏植物', origin: '', quantity: 1, status: 'ACTIVE', imageUrl: '' });
  visible.value = true;
}
async function save() {
  const payload = { name: form.name, scientificName: form.scientificName, category: form.category, origin: form.origin, quantity: form.quantity, status: form.status, imageUrl: form.imageUrl };
  if (form.id) await http.put('/plants/' + form.id, payload);
  else await http.post('/plants', payload);
  visible.value = false;
  ElMessage.success('保存成功');
  await load();
}

async function uploadImage(options: any) {
  const data = new FormData();
  data.append('file', options.file);
  try {
    const response = await http.post('/files/plant-images', data);
    form.imageUrl = response.data.data.url;
    options.onSuccess(response.data);
    ElMessage.success('图片上传成功');
  } catch (error: any) {
    options.onError(error);
    ElMessage.error(error.response?.data?.message || '图片上传失败');
  }
}
async function remove(id: number) {
  await ElMessageBox.confirm('确定删除这株植物档案吗？', '删除确认', { type: 'warning' });
  await http.delete('/plants/' + id);
  ElMessage.success('删除成功');
  await load();
}
onMounted(load);
</script>
