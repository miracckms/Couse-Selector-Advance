<template>
  <!-- Loading state while validating token -->
  <div v-if="isLoading" class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-100 to-purple-100 dark:from-gray-900 dark:to-gray-800">
    <div class="text-center">
      <div class="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl shadow-lg mb-4 animate-pulse">
        <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
        </svg>
      </div>
      <p class="text-gray-600 dark:text-gray-400 text-sm">Loading...</p>
    </div>
  </div>

  <Login v-else-if="!isAuthenticated" @login-success="handleLoginSuccess" />
  <MainApp v-else @logout="handleLogout" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Login from './components/Login.vue'
import MainApp from './App.vue'
import { authAPI, setOnAuthExpired } from './api/api.js'

const isAuthenticated = ref(false)
const isLoading = ref(true)

const checkAuth = async () => {
  if (!authAPI.isAuthenticated()) {
    isAuthenticated.value = false
    isLoading.value = false
    return
  }

  const isValid = await authAPI.validateToken()
  isAuthenticated.value = isValid
  if (!isValid) {
    authAPI.logout()
  }
  isLoading.value = false
}

const handleLoginSuccess = () => {
  isAuthenticated.value = true
}

const handleLogout = async () => {
  await authAPI.logout()
  isAuthenticated.value = false
}

// Listen for auth expiration from API interceptor (e.g. token expired mid-session)
setOnAuthExpired(() => {
  isAuthenticated.value = false
})

onMounted(() => {
  checkAuth()
})
</script>
