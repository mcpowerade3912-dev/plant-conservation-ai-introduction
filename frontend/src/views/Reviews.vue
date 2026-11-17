<template>
  <div class="page-card">
    <el-table :data="rows" border>
      <el-table-column prop="plantName" label="植物名称" min-width="110" />
      <el-table-column prop="scientificName" label="学名" min-width="130" />
      <el-table-column prop="origin" label="来源地" min-width="100" />
      <el-table-column prop="creatorName" label="提交人" width="100" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column label="操作" width="230">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="review(row.id, 'APPROVED')">通过</el-button>
          <el-button size="small" type="danger" @click="review(row.id, 'REJECTED')">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import http from '../api/http';
import type { Intro } from '../types';

const rows = ref<Intro[]>([]);
async function load() {
  const response = await http.get('/reviews/pending');
  rows.value = response.data.data;
}
async function review(id: number, result: string) {
  await http.post('/reviews/' + id, { result, comment: result === 'APPROVED' ? '鉴定通过' : '资料不完整，退回修改' });
  ElMessage.success(result === 'APPROVED' ? '已通过并写入植物档案' : '已驳回');
  await load();
}
onMounted(load);
</script>
