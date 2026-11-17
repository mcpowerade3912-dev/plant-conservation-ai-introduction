import { defineStore } from 'pinia';
import http from '../api/http';
import type { User } from '../types';

export const useAuth = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null') as User | null
  }),
  getters: {
    isLogin: state => !!state.token,
    role: state => state.user?.role || 'USER'
  },
  actions: {
    async login(username: string, password: string) {
      const response = await http.post('/login', { username, password });
      this.token = response.data.data.token;
      this.user = response.data.data.user;
      localStorage.setItem('token', this.token);
      localStorage.setItem('user', JSON.stringify(this.user));
    },
    logout() {
      this.token = '';
      this.user = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
    setUser(user: User) {
      this.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    }
  }
});
