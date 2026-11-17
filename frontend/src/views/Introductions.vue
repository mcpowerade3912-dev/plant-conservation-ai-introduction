<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="按名称或来源搜索" clearable style="width:220px" />
      <el-select v-model="status" clearable placeholder="全部状态" style="width:150px">
        <el-option label="待鉴定" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-button type="primary" @click="open()"><el-icon><Plus /></el-icon>新增引种记录</el-button>
    </div>

    <el-table :data="filtered" border>
      <el-table-column label="图片" width="86">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" fit="cover" class="table-plant-image" :preview-src-list="[row.imageUrl]" preview-teleported />
          <span v-else class="image-placeholder">无图</span>
        </template>
      </el-table-column>
      <el-table-column prop="plantName" label="植物名称" min-width="110" />
      <el-table-column prop="scientificName" label="学名" min-width="130" />
      <el-table-column prop="origin" label="来源地" min-width="100" />
      <el-table-column prop="introductionDate" label="引种日期" width="115" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="status" label="状态" width="95">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑引种记录' : '新增引种记录'">
      <el-form :model="form" label-width="90px">
        <el-form-item label="植物名称">
          <div class="assist-input-row">
            <el-input v-model="form.plantName" placeholder="输入植物名称后可让 AI 推荐学名" />
            <el-button type="primary" plain :loading="aiLoading" @click="aiAssist">AI 补全</el-button>
          </div>
        </el-form-item>
        <el-form-item label="学名"><el-input v-model="form.scientificName" /></el-form-item>
        <el-form-item label="来源地"><el-input v-model="form.origin" /></el-form-item>
        <el-form-item label="引种日期"><el-date-picker v-model="form.introductionDate" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="1" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="植物图片">
          <div class="image-upload-block">
            <el-upload
              :show-file-list="false"
              :http-request="uploadImage"
              accept="image/jpeg,image/png,image/webp"
            >
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
      <section v-if="aiResult" class="ai-assist-card">
        <div class="ai-assist-head">
          <div>
            <strong>AI 推荐</strong>
            <span>结果仅供参考，请人工确认</span>
          </div>
          <el-button text @click="aiResult = null">关闭</el-button>
        </div>
        <div class="candidate-list">
          <article v-for="candidate in aiResult.candidates" :key="candidate.scientificName" class="candidate">
            <div class="candidate-main">
              <div>
                <strong>{{ candidate.scientificName || '未确认学名' }}</strong>
                <p>{{ candidate.family || '科未确认' }} · {{ candidate.genus || '属未确认' }}</p>
                <p v-if="candidate.commonNames?.length">别名：{{ candidate.commonNames.join('、') }}</p>
                <p v-if="candidate.note">{{ candidate.note }}</p>
              </div>
              <span class="confidence">{{ Math.round(candidate.confidence * 100) }}%</span>
            </div>
            <div class="candidate-actions">
              <span>来源地：{{ candidate.origin || '未确认' }}</span>
              <el-button size="small" type="success" @click="applyCandidate(candidate)">采用此候选</el-button>
            </div>
          </article>
        </div>
        <div v-if="aiResult.careTips?.length" class="care-tips">
          <strong>养护提示</strong>
          <ul><li v-for="tip in aiResult.careTips" :key="tip">{{ tip }}</li></ul>
        </div>
        <p v-if="aiResult.note" class="ai-note">{{ aiResult.note }}</p>
      </section>
      <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import http from '../api/http';
import type { Intro, IntroductionAssistResult, PlantSpeciesCandidate } from '../types';

const rows = ref<Intro[]>([]);
const keyword = ref('');
const status = ref('');
const visible = ref(false);
const aiLoading = ref(false);
const aiResult = ref<IntroductionAssistResult | null>(null);
const form = reactive({ id: 0, plantName: '', scientificName: '', origin: '', introductionDate: '', quantity: 1, description: '', imageUrl: '' });
const filtered = computed(() => rows.value.filter(row =>
  (!status.value || row.status === status.value) &&
  (!keyword.value || row.plantName.includes(keyword.value) || row.origin.includes(keyword.value))
));

async function load() {
  const response = await http.get('/introductions');
  rows.value = response.data.data;
}
function open(row?: Intro) {
  aiResult.value = null;
  Object.assign(form, row || { id: 0, plantName: '', scientificName: '', origin: '', introductionDate: '', quantity: 1, description: '', imageUrl: '' });
  visible.value = true;
}

async function aiAssist() {
  if (!form.plantName.trim()) {
    ElMessage.warning('请先填写植物名称');
    return;
  }
  aiLoading.value = true;
  try {
    const response = await http.post('/ai/introduction-assist', {
      plantName: form.plantName.trim(),
      origin: form.origin,
      description: form.description
    });
    aiResult.value = response.data.data;
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || error.message || 'AI 补全失败');
  } finally {
    aiLoading.value = false;
  }
}

function applyCandidate(candidate: PlantSpeciesCandidate) {
  form.scientificName = candidate.scientificName || form.scientificName;
  if (!form.origin && candidate.origin) form.origin = candidate.origin;
  ElMessage.success('已填入候选信息，请核对后保存');
}
async function save() {
  const payload = { plantName: form.plantName, scientificName: form.scientificName, origin: form.origin, introductionDate: form.introductionDate, quantity: form.quantity, description: form.description, imageUrl: form.imageUrl };
  if (form.id) await http.put('/introductions/' + form.id, payload);
  else await http.post('/introductions', payload);
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
  await ElMessageBox.confirm('确定删除这条引种记录吗？', '删除确认', { type: 'warning' });
  await http.delete('/introductions/' + id);
  ElMessage.success('删除成功');
  await load();
}
function statusText(value: string) {
  return ({ PENDING: '待鉴定', APPROVED: '已通过', REJECTED: '已驳回' } as Record<string, string>)[value] || value;
}
onMounted(load);
</script>
