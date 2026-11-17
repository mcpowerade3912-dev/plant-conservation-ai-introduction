import { defineStore } from 'pinia';
import http from '../api/http';

export const useSettings = defineStore('settings', {
  state: () => ({
    systemName: '数智保育系统',
    supportContact: '',
    loaded: false
  }),
  actions: {
    async load() {
      try {
        const response = await http.get('/settings/public');
        this.systemName = response.data.data.systemName || '数智保育系统';
        this.supportContact = response.data.data.supportContact || '';
        this.loaded = true;
        document.title = this.systemName;
      } catch {
        document.title = this.systemName;
      }
    },
    setSystemName(name: string) {
      this.systemName = name || '数智保育系统';
      document.title = this.systemName;
    }
  }
});
