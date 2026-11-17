<template>
  <div class="settings-page">
    <div class="settings-grid">
      <section class="settings-card">
        <div class="settings-title">
          <div><h3>基础设置</h3><p>保存后立即影响系统展示和业务流程</p></div>
          <el-tag type="info">{{ form.aiApiKeyConfigured ? 'API Key 已配置' : '未配置 API Key' }}</el-tag>
        </div>

        <el-form label-position="top">
          <el-form-item label="系统名称">
            <el-input v-model="form.systemName" maxlength="30" show-word-limit />
          </el-form-item>
          <el-form-item label="客服联系方式">
            <el-input v-model="form.supportContact" maxlength="30" placeholder="电话、邮箱或其他联系方式" />
          </el-form-item>
          <div class="form-two">
            <el-form-item label="默认植物分类">
              <el-input v-model="form.defaultPlantCategory" maxlength="100" placeholder="观赏植物" />
            </el-form-item>
            <el-form-item label="图片上传上限（MB）">
              <el-input-number v-model="form.uploadMaxSizeMb" :min="1" :max="20" style="width:100%" />
            </el-form-item>
          </div>
        </el-form>
      </section>

      <section class="settings-card">
        <div class="settings-title">
          <div><h3>AI 接口</h3><p>支持兼容 OpenAI Responses API 的接口</p></div>
          <el-switch v-model="form.aiEnabled" active-text="启用" inactive-text="关闭" />
        </div>

        <el-form label-position="top">
          <el-form-item label="接口地址">
            <el-input v-model="form.aiBaseUrl" placeholder="https://api.openai.com/v1" />
          </el-form-item>
          <div class="form-two">
            <el-form-item label="模型名称">
              <el-input v-model="form.aiModel" placeholder="gpt-4.1-mini" />
            </el-form-item>
            <el-form-item label="请求超时（秒）">
              <el-input-number v-model="form.aiTimeoutSeconds" :min="5" :max="180" style="width:100%" />
            </el-form-item>
          </div>
          <el-form-item label="每分钟调用上限">
            <el-input-number v-model="form.aiRateLimitPerMinute" :min="1" :max="200" style="width:100%" />
          </el-form-item>
          <el-form-item label="API Key">
            <el-input v-model="form.aiApiKey" type="password" show-password autocomplete="new-password" placeholder="留空则保持现有 Key" />
            <p class="field-tip">Key 使用 AES-GCM 加密保存，页面不会回显明文。</p>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="form.clearAiApiKey">清除已保存的 API Key</el-checkbox>
          </el-form-item>
        </el-form>
      </section>
    </div>

    <div class="settings-actions">
      <span>{{ form.updateTime ? `最近更新：${form.updateTime.replace('T', ' ').slice(0, 16)}` : '尚未保存设置' }}</span>
      <el-button @click="load">重新加载</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存设置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import http from '../api/http';
import { useSettings } from '../stores/settings';
import type { SystemSettings } from '../types';

const settingsStore = useSettings();
const saving = ref(false);
const form = reactive<SystemSettings & { aiApiKey: string; clearAiApiKey: boolean }>({
  systemName: '数智保育系统',
  supportContact: '',
  defaultPlantCategory: '观赏植物',
  uploadMaxSizeMb: 5,
  aiEnabled: true,
  aiBaseUrl: 'https://api.openai.com/v1',
  aiModel: 'gpt-4.1-mini',
  aiTimeoutSeconds: 60,
  aiRateLimitPerMinute: 20,
  aiApiKeyConfigured: false,
  updateTime: '',
  aiApiKey: '',
  clearAiApiKey: false
});

async function load() {
  try {
    const response = await http.get('/admin/settings');
    const data = response.data.data as SystemSettings;
    Object.assign(form, data, { aiApiKey: '', clearAiApiKey: false });
    settingsStore.setSystemName(data.systemName);
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '设置加载失败');
  }
}

async function save() {
  if (!form.systemName.trim()) {
    ElMessage.warning('请填写系统名称');
    return;
  }
  saving.value = true;
  try {
    const payload = {
      systemName: form.systemName.trim(),
      supportContact: form.supportContact,
      defaultPlantCategory: form.defaultPlantCategory,
      uploadMaxSizeMb: form.uploadMaxSizeMb,
      aiEnabled: form.aiEnabled,
      aiBaseUrl: form.aiBaseUrl,
      aiModel: form.aiModel,
      aiTimeoutSeconds: form.aiTimeoutSeconds,
      aiRateLimitPerMinute: form.aiRateLimitPerMinute,
      aiApiKey: form.aiApiKey || undefined,
      clearAiApiKey: form.clearAiApiKey
    };
    const response = await http.put('/admin/settings', payload);
    const data = response.data.data as SystemSettings;
    Object.assign(form, data, { aiApiKey: '', clearAiApiKey: false });
    settingsStore.setSystemName(data.systemName);
    ElMessage.success('设置已保存');
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '设置保存失败');
  } finally {
    saving.value = false;
  }
}

onMounted(load);
</script>
