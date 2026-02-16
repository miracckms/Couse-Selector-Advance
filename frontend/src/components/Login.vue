<template>
  <div class="login-page" :class="isDark ? 'dark' : 'light'">
    <!-- Animated Background -->
    <div class="login-bg">
      <div class="bg-gradient"></div>
      <div class="bg-orb orb-1"></div>
      <div class="bg-orb orb-2"></div>
      <div class="bg-orb orb-3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="login-container">
      <!-- Logo & Branding -->
      <div class="login-brand">
        <div class="brand-icon">
          <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
        </div>
        <h1 class="brand-title">Ders Planlayici</h1>
        <p class="brand-subtitle">Yeditepe Universitesi</p>
      </div>

      <!-- Glass Card -->
      <div class="login-card">
        <!-- Tab Switcher -->
        <div class="tab-switcher">
          <div class="tab-slider" :class="activeTab === 'register' ? 'slide-right' : ''"></div>
          <button @click="activeTab = 'login'" class="tab-btn" :class="activeTab === 'login' ? 'active' : ''">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/></svg>
            Giris Yap
          </button>
          <button @click="activeTab = 'register'" class="tab-btn" :class="activeTab === 'register' ? 'active' : ''">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/></svg>
            Kayit Ol
          </button>
        </div>

        <!-- Messages -->
        <transition name="msg">
          <div v-if="errorMessage" class="msg msg-error">
            <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
            <span>{{ errorMessage }}</span>
          </div>
        </transition>
        <transition name="msg">
          <div v-if="successMessage" class="msg msg-success">
            <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
            <span>{{ successMessage }}</span>
          </div>
        </transition>

        <!-- Login Form -->
        <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="login-form">
          <div class="input-group">
            <div class="input-icon">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
            </div>
            <input v-model="loginForm.username" type="text" required placeholder="Kullanici adi" autocomplete="username" />
          </div>
          <div class="input-group">
            <div class="input-icon">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
            </div>
            <input v-model="loginForm.password" :type="showLoginPw ? 'text' : 'password'" required placeholder="Sifre" autocomplete="current-password" />
            <button type="button" class="input-action" @click="showLoginPw = !showLoginPw" tabindex="-1">
              <svg v-if="!showLoginPw" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"/></svg>
            </button>
          </div>
          <button type="submit" :disabled="loading" class="submit-btn">
            <span v-if="!loading" class="flex items-center justify-center gap-2">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/></svg>
              Giris Yap
            </span>
            <span v-else class="flex items-center justify-center gap-2">
              <svg class="animate-spin w-4.5 h-4.5" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
              Giris yapiliyor...
            </span>
          </button>
        </form>

        <!-- Register Form -->
        <form v-if="activeTab === 'register'" @submit.prevent="handleRegister" class="login-form">
          <div class="input-row">
            <div class="input-group">
              <div class="input-icon">
                <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
              </div>
              <input v-model="registerForm.firstName" type="text" placeholder="Ad" />
            </div>
            <div class="input-group">
              <div class="input-icon">
                <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
              </div>
              <input v-model="registerForm.lastName" type="text" placeholder="Soyad" />
            </div>
          </div>
          <div class="input-group">
            <div class="input-icon">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
            </div>
            <input v-model="registerForm.username" type="text" required placeholder="Kullanici adi *" autocomplete="username" />
          </div>
          <div class="input-group">
            <div class="input-icon">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
            </div>
            <input v-model="registerForm.email" type="email" required placeholder="E-posta *" autocomplete="email" />
          </div>
          <div class="input-row">
            <div class="input-group">
              <div class="input-icon">
                <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V8a2 2 0 00-2-2h-5m-4 0V5a2 2 0 114 0v1m-4 0a2 2 0 104 0"/></svg>
              </div>
              <input v-model="registerForm.studentId" type="text" placeholder="Ogrenci No" />
            </div>
            <div class="input-group">
              <div class="input-icon">
                <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/></svg>
              </div>
              <input v-model="registerForm.department" type="text" placeholder="Bolum" />
            </div>
          </div>
          <div class="input-group">
            <div class="input-icon">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
            </div>
            <input v-model="registerForm.password" type="password" required minlength="6" placeholder="Sifre * (min 6 karakter)" autocomplete="new-password" />
          </div>
          <div class="input-group">
            <div class="input-icon">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/></svg>
            </div>
            <input v-model="registerForm.confirmPassword" type="password" required placeholder="Sifre tekrar *" autocomplete="new-password" />
          </div>
          <button type="submit" :disabled="loading" class="submit-btn">
            <span v-if="!loading" class="flex items-center justify-center gap-2">
              <svg class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/></svg>
              Hesap Olustur
            </span>
            <span v-else class="flex items-center justify-center gap-2">
              <svg class="animate-spin w-4.5 h-4.5" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
              Olusturuluyor...
            </span>
          </button>
        </form>
      </div>

      <!-- Footer -->
      <p class="login-footer">CSE344 Spring 2026 Project</p>
    </div>
  </div>
</template>

<script>
import { authAPI } from '../api/api.js';

export default {
  name: 'Login',
  data() {
    return {
      activeTab: 'login',
      loading: false,
      showLoginPw: false,
      errorMessage: '',
      successMessage: '',
      isDark: document.documentElement.classList.contains('dark') || window.matchMedia('(prefers-color-scheme: dark)').matches,
      loginForm: { username: '', password: '' },
      registerForm: { username: '', email: '', password: '', confirmPassword: '', firstName: '', lastName: '', studentId: '', department: '' },
    };
  },
  methods: {
    async handleLogin() {
      this.errorMessage = '';
      this.loading = true;
      try {
        await authAPI.login(this.loginForm.username, this.loginForm.password);
        this.$emit('login-success');
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Kullanici adi veya sifre hatali';
      } finally {
        this.loading = false;
      }
    },
    async handleRegister() {
      this.errorMessage = '';
      this.successMessage = '';
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.errorMessage = 'Sifreler eslesmiyor';
        return;
      }
      this.loading = true;
      try {
        await authAPI.register({
          username: this.registerForm.username,
          email: this.registerForm.email,
          password: this.registerForm.password,
          firstName: this.registerForm.firstName,
          lastName: this.registerForm.lastName,
          studentId: this.registerForm.studentId,
          department: this.registerForm.department,
        });
        this.successMessage = 'Hesap olusturuldu! Giris yapabilirsiniz.';
        this.activeTab = 'login';
        this.registerForm = { username: '', email: '', password: '', confirmPassword: '', firstName: '', lastName: '', studentId: '', department: '' };
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Kayit basarisiz';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
/* ===== PAGE ===== */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, sans-serif;
}

/* ===== THEME COLORS ===== */
.login-page.dark {
  --lp-bg: #0c1222;
  --lp-surface: rgba(21, 29, 48, 0.7);
  --lp-border: rgba(148, 163, 184, 0.1);
  --lp-text: #f1f5f9;
  --lp-text-secondary: #94a3b8;
  --lp-text-muted: #64748b;
  --lp-input-bg: rgba(15, 23, 42, 0.5);
  --lp-input-border: rgba(148, 163, 184, 0.12);
  --lp-input-focus: rgba(99, 102, 241, 0.3);
  --lp-tab-bg: rgba(15, 23, 42, 0.4);
  --lp-tab-active: rgba(99, 102, 241, 0.15);
  --lp-orb1: rgba(99, 102, 241, 0.15);
  --lp-orb2: rgba(139, 92, 246, 0.12);
  --lp-orb3: rgba(6, 182, 212, 0.08);
}

.login-page.light {
  --lp-bg: #f0f2f5;
  --lp-surface: rgba(255, 255, 255, 0.85);
  --lp-border: rgba(15, 23, 42, 0.08);
  --lp-text: #0f172a;
  --lp-text-secondary: #475569;
  --lp-text-muted: #94a3b8;
  --lp-input-bg: rgba(241, 245, 249, 0.8);
  --lp-input-border: rgba(15, 23, 42, 0.1);
  --lp-input-focus: rgba(99, 102, 241, 0.2);
  --lp-tab-bg: rgba(241, 245, 249, 0.8);
  --lp-tab-active: rgba(99, 102, 241, 0.1);
  --lp-orb1: rgba(99, 102, 241, 0.12);
  --lp-orb2: rgba(139, 92, 246, 0.1);
  --lp-orb3: rgba(6, 182, 212, 0.06);
}

/* ===== ANIMATED BACKGROUND ===== */
.login-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: var(--lp-bg);
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: var(--lp-orb1);
  top: -10%;
  right: -5%;
  animation: float-orb 20s ease-in-out infinite;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: var(--lp-orb2);
  bottom: -15%;
  left: -10%;
  animation: float-orb 25s ease-in-out infinite reverse;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: var(--lp-orb3);
  top: 40%;
  left: 50%;
  animation: float-orb 18s ease-in-out infinite 5s;
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(148,163,184,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.03) 1px, transparent 1px);
  background-size: 48px 48px;
}

@keyframes float-orb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.05); }
  50% { transform: translate(-20px, 30px) scale(0.95); }
  75% { transform: translate(40px, 20px) scale(1.02); }
}

/* ===== CONTAINER ===== */
.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
}

/* ===== BRAND ===== */
.login-brand {
  text-align: center;
  margin-bottom: 32px;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.3);
  animation: pulse-icon 3s ease-in-out infinite;
}

@keyframes pulse-icon {
  0%, 100% { box-shadow: 0 8px 32px rgba(99, 102, 241, 0.3); }
  50% { box-shadow: 0 8px 48px rgba(99, 102, 241, 0.45); }
}

.brand-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--lp-text);
  letter-spacing: -0.02em;
  margin-bottom: 4px;
}

.brand-subtitle {
  font-size: 14px;
  color: var(--lp-text-muted);
  font-weight: 500;
}

/* ===== GLASS CARD ===== */
.login-card {
  background: var(--lp-surface);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--lp-border);
  border-radius: 20px;
  padding: 28px;
  box-shadow: 
    0 20px 60px rgba(0,0,0,0.1),
    0 0 0 1px var(--lp-border);
}

/* ===== TAB SWITCHER ===== */
.tab-switcher {
  display: flex;
  position: relative;
  background: var(--lp-tab-bg);
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
}

.tab-slider {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 8px;
  transition: transform 280ms cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.tab-slider.slide-right {
  transform: translateX(100%);
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 600;
  color: var(--lp-text-muted);
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  z-index: 1;
  transition: color 200ms ease;
}

.tab-btn.active {
  color: white;
}

/* ===== MESSAGES ===== */
.msg {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 16px;
}

.msg-error {
  background: rgba(244, 63, 94, 0.1);
  border: 1px solid rgba(244, 63, 94, 0.2);
  color: #fb7185;
}

.light .msg-error {
  background: #fff1f2;
  border-color: #fecdd3;
  color: #be123c;
}

.msg-success {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  color: #6ee7b7;
}

.light .msg-success {
  background: #ecfdf5;
  border-color: #a7f3d0;
  color: #047857;
}

.msg-enter-active, .msg-leave-active {
  transition: all 280ms cubic-bezier(0.4, 0, 0.2, 1);
}
.msg-enter-from, .msg-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== FORM ===== */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.input-group {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--lp-text-muted);
  pointer-events: none;
  transition: color 200ms ease;
  z-index: 1;
}

.input-group input {
  width: 100%;
  padding: 12px 14px 12px 40px;
  background: var(--lp-input-bg);
  border: 1px solid var(--lp-input-border);
  border-radius: 12px;
  color: var(--lp-text);
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
}

.input-group input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px var(--lp-input-focus);
}

.input-group input:focus + .input-icon,
.input-group input:focus ~ .input-icon,
.input-group:focus-within .input-icon {
  color: #818cf8;
}

.input-group input::placeholder {
  color: var(--lp-text-muted);
}

.input-action {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--lp-text-muted);
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: all 150ms ease;
}
.input-action:hover {
  color: var(--lp-text);
  background: var(--lp-tab-bg);
}

/* ===== SUBMIT BUTTON ===== */
.submit-btn {
  width: 100%;
  padding: 12px 24px;
  margin-top: 4px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== FOOTER ===== */
.login-footer {
  text-align: center;
  font-size: 12px;
  color: var(--lp-text-muted);
  margin-top: 24px;
  font-weight: 500;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 480px) {
  .login-container { max-width: 100%; }
  .login-card { padding: 24px 20px; border-radius: 16px; }
  .input-row { grid-template-columns: 1fr; }
}
</style>
