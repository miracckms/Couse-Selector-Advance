<template>
  <div class="h-full overflow-auto profile-page">
    <div class="max-w-2xl mx-auto px-4 sm:px-6 py-6 space-y-5">

      <!-- Page Header -->
      <div class="flex items-center gap-3">
        <button @click="$emit('close')" class="w-9 h-9 rounded-xl flex items-center justify-center profile-back-btn transition-all duration-200 hover:scale-105">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
        </button>
        <div>
          <h2 class="text-lg font-bold text-primary">{{ $t('profileTitle') }}</h2>
          <p class="text-xs text-secondary">{{ currentUser?.username }}</p>
        </div>
      </div>

      <!-- Success/Error Messages -->
      <transition name="fade">
        <div v-if="successMsg" class="flex items-center gap-2 px-4 py-3 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm">
          <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
          {{ $t(successMsg) }}
        </div>
      </transition>
      <transition name="fade">
        <div v-if="errorMsg" class="flex items-center gap-2 px-4 py-3 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
          <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          {{ errorMsg }}
        </div>
      </transition>

      <!-- ===== Personal Info Section ===== -->
      <div class="profile-card">
        <div class="profile-card-header">
          <div class="profile-card-icon bg-gradient-to-br from-indigo-500 to-purple-600">
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <h3 class="text-sm font-bold text-primary">{{ $t('personalInfo') }}</h3>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-4">
          <div class="profile-field">
            <label>{{ $t('profileFirstName') }}</label>
            <input v-model="form.firstName" type="text" :placeholder="$t('profileFirstName')" />
          </div>
          <div class="profile-field">
            <label>{{ $t('profileLastName') }}</label>
            <input v-model="form.lastName" type="text" :placeholder="$t('profileLastName')" />
          </div>
          <div class="profile-field">
            <label>{{ $t('profileEmail') }}</label>
            <input v-model="form.email" type="email" :placeholder="$t('profileEmail')" />
          </div>
          <div class="profile-field">
            <label>{{ $t('profileStudentId') }}</label>
            <input v-model="form.studentId" type="text" :placeholder="$t('profileStudentId')" />
          </div>
          <div class="profile-field sm:col-span-2">
            <label>{{ $t('profileDepartment') }}</label>
            <input v-model="form.department" type="text" :placeholder="$t('profileDepartment')" />
          </div>
        </div>

        <button @click="saveProfile" :disabled="savingProfile" class="mt-4 w-full sm:w-auto px-6 py-2.5 bg-gradient-to-r from-indigo-500 to-purple-600 text-white text-sm font-semibold rounded-xl hover:shadow-lg hover:shadow-indigo-500/25 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed">
          <span v-if="!savingProfile" class="flex items-center justify-center gap-2">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
            {{ $t('saveProfile') }}
          </span>
          <span v-else class="flex items-center justify-center gap-2">
            <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
            {{ $t('saving') }}
          </span>
        </button>
      </div>

      <!-- ===== Password Change Section ===== -->
      <div class="profile-card">
        <div class="profile-card-header">
          <div class="profile-card-icon bg-gradient-to-br from-rose-500 to-pink-600">
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
            </svg>
          </div>
          <h3 class="text-sm font-bold text-primary">{{ $t('changePassword') }}</h3>
        </div>

        <div class="space-y-3 mt-4">
          <div class="profile-field">
            <label>{{ $t('currentPassword') }}</label>
            <div class="relative">
              <input 
                v-model="passwordForm.currentPassword" 
                :type="showCurrentPassword ? 'text' : 'password'" 
                :placeholder="$t('currentPasswordPlaceholder')" 
              />
              <button 
                type="button"
                @click="showCurrentPassword = !showCurrentPassword" 
                class="absolute right-3 top-1/2 -translate-y-1/2 text-secondary hover:text-primary transition-colors"
              >
                <svg v-if="!showCurrentPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="profile-field">
            <label>{{ $t('newPassword') }}</label>
            <div class="relative">
              <input 
                v-model="passwordForm.newPassword" 
                :type="showNewPassword ? 'text' : 'password'" 
                :placeholder="$t('newPasswordPlaceholder')" 
              />
              <button 
                type="button"
                @click="showNewPassword = !showNewPassword" 
                class="absolute right-3 top-1/2 -translate-y-1/2 text-secondary hover:text-primary transition-colors"
              >
                <svg v-if="!showNewPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="profile-field">
            <label>{{ $t('confirmPassword') }}</label>
            <div class="relative">
              <input 
                v-model="passwordForm.confirmPassword" 
                :type="showConfirmPassword ? 'text' : 'password'" 
                :placeholder="$t('confirmPasswordPlaceholder')" 
              />
              <button 
                type="button"
                @click="showConfirmPassword = !showConfirmPassword" 
                class="absolute right-3 top-1/2 -translate-y-1/2 text-secondary hover:text-primary transition-colors"
              >
                <svg v-if="!showConfirmPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Password strength indicator -->
          <div v-if="passwordForm.newPassword" class="space-y-1.5">
            <div class="flex gap-1">
              <div v-for="i in 4" :key="i" class="h-1 flex-1 rounded-full transition-all duration-300"
                :class="i <= passwordStrength.level ? passwordStrength.colorClass : 'pw-bar-empty'"
              ></div>
            </div>
            <p class="text-[11px] font-medium" :class="passwordStrength.textClass">{{ $t(passwordStrength.labelKey) }}</p>
          </div>

          <!-- Validation message -->
          <p v-if="passwordForm.confirmPassword && passwordForm.newPassword !== passwordForm.confirmPassword" 
            class="text-xs text-red-400 flex items-center gap-1">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
            {{ $t('passwordMismatch') }}
          </p>
        </div>

        <button 
          @click="changePassword" 
          :disabled="changingPassword || !canChangePassword" 
          class="mt-4 w-full sm:w-auto px-6 py-2.5 bg-gradient-to-r from-rose-500 to-pink-600 text-white text-sm font-semibold rounded-xl hover:shadow-lg hover:shadow-rose-500/25 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <span v-if="!changingPassword" class="flex items-center justify-center gap-2">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
            {{ $t('changePasswordBtn') }}
          </span>
          <span v-else class="flex items-center justify-center gap-2">
            <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
            {{ $t('saving') }}
          </span>
        </button>
      </div>

      <!-- ===== Tab Customization Section ===== -->
      <div class="profile-card">
        <div class="profile-card-header">
          <div class="profile-card-icon bg-gradient-to-br from-amber-500 to-orange-600">
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7"/>
            </svg>
          </div>
          <div>
            <h3 class="text-sm font-bold text-primary">{{ $t('tabCustomization') }}</h3>
            <p class="text-[11px] text-secondary mt-0.5">{{ $t('tabCustomizationDesc') }}</p>
          </div>
        </div>

        <div class="mt-4 space-y-2">
          <div 
            v-for="(tab, index) in orderedTabs" 
            :key="tab.id"
            class="profile-tab-item"
            :class="{ 'is-hidden': isTabHidden(tab.id), 'is-dragging': dragIndex === index }"
            draggable="true"
            @dragstart="onDragStart(index, $event)"
            @dragover.prevent="onDragOver(index, $event)"
            @drop="onDrop(index)"
            @dragend="onDragEnd"
          >
            <!-- Drag Handle -->
            <div class="drag-handle">
              <svg class="w-4 h-4" viewBox="0 0 16 16" fill="currentColor">
                <circle cx="5" cy="4" r="1.2"/><circle cx="11" cy="4" r="1.2"/>
                <circle cx="5" cy="8" r="1.2"/><circle cx="11" cy="8" r="1.2"/>
                <circle cx="5" cy="12" r="1.2"/><circle cx="11" cy="12" r="1.2"/>
              </svg>
            </div>

            <!-- Tab Info -->
            <div class="flex items-center gap-2.5 flex-1 min-w-0">
              <div class="tab-icon-circle" :class="tab.colorClass">
                <svg class="w-3.5 h-3.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="tab.svgPath"/>
                </svg>
              </div>
              <span class="text-sm font-medium text-primary truncate">{{ $t(tab.labelKey) }}</span>
            </div>

            <!-- Default Chip -->
            <button 
              @click="setDefaultTab(tab.id)"
              class="default-chip"
              :class="{ 'is-default': localDefaultTab === tab.id }"
              :disabled="isTabHidden(tab.id)"
            >
              <svg v-if="localDefaultTab === tab.id" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"/></svg>
              {{ localDefaultTab === tab.id ? $t('defaultTabLabel') : $t('setDefault') }}
            </button>

            <!-- Visibility Toggle -->
            <button 
              @click="toggleTabVisibility(tab.id)"
              class="visibility-toggle"
              :class="{ 'is-visible': !isTabHidden(tab.id) }"
            >
              <svg v-if="!isTabHidden(tab.id)" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"/>
              </svg>
            </button>
          </div>
        </div>

        <p v-if="visibleTabCount <= 1" class="mt-3 text-xs text-amber-400 font-medium flex items-center gap-1.5">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"/></svg>
          {{ $t('minOneTab') }}
        </p>
      </div>

      <!-- Mobile Logout -->
      <button @click="handleMobileLogout" class="sm:hidden w-full py-3 rounded-xl border border-red-500/30 text-red-400 text-sm font-medium hover:bg-red-500/10 transition-colors flex items-center justify-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
        </svg>
        Logout
      </button>

    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { authAPI } from '../api/api.js'

const ALL_TABS = [
  { id: 'schedule', labelKey: 'scheduleTab', colorClass: 'from-indigo-500 to-blue-600', svgPath: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z' },
  { id: 'quota', labelKey: 'quotaTab', colorClass: 'from-emerald-500 to-teal-600', svgPath: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' },
  { id: 'grade', labelKey: 'gradeTab', colorClass: 'from-amber-500 to-orange-600', svgPath: 'M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z' },
]

export default {
  name: 'Profile',
  props: {
    tabOrder: { type: Array, default: () => null },
    hiddenTabs: { type: Array, default: () => null },
    defaultTab: { type: String, default: 'schedule' },
  },
  emits: ['close', 'update-tab-config', 'logout'],
  setup(props, { emit }) {
    const form = ref({ firstName: '', lastName: '', email: '', studentId: '', department: '' })
    const currentUser = ref(null)
    const savingProfile = ref(false)
    const successMsg = ref('')
    const errorMsg = ref('')

    const passwordForm = ref({ currentPassword: '', newPassword: '', confirmPassword: '' })
    const changingPassword = ref(false)
    const showCurrentPassword = ref(false)
    const showNewPassword = ref(false)
    const showConfirmPassword = ref(false)

    const localTabOrder = ref([...ALL_TABS.map(t => t.id)])
    const localHiddenTabs = ref([])
    const localDefaultTab = ref('schedule')
    const dragIndex = ref(null)

    onMounted(async () => {
      currentUser.value = authAPI.getCurrentUser()
      try {
        const profile = await authAPI.getProfile()
        form.value = {
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          email: profile.email || '',
          studentId: profile.studentId || '',
          department: profile.department || '',
        }
      } catch (e) { /* ignore */ }

      if (props.tabOrder && props.tabOrder.length > 0) localTabOrder.value = [...props.tabOrder]
      if (props.hiddenTabs) localHiddenTabs.value = [...props.hiddenTabs]
      if (props.defaultTab) localDefaultTab.value = props.defaultTab
    })

    const orderedTabs = computed(() =>
      localTabOrder.value.map(id => ALL_TABS.find(t => t.id === id)).filter(Boolean)
    )
    const visibleTabCount = computed(() =>
      localTabOrder.value.filter(id => !localHiddenTabs.value.includes(id)).length
    )
    const isTabHidden = (id) => localHiddenTabs.value.includes(id)

    const toggleTabVisibility = (id) => {
      const hidden = [...localHiddenTabs.value]
      const idx = hidden.indexOf(id)
      if (idx >= 0) {
        hidden.splice(idx, 1)
      } else {
        if (visibleTabCount.value <= 1) return
        hidden.push(id)
        if (localDefaultTab.value === id) {
          const first = localTabOrder.value.find(tid => !hidden.includes(tid))
          if (first) localDefaultTab.value = first
        }
      }
      localHiddenTabs.value = hidden
      emitConfig()
    }

    const setDefaultTab = (id) => {
      if (isTabHidden(id)) return
      localDefaultTab.value = id
      emitConfig()
    }

    const onDragStart = (index, event) => { dragIndex.value = index; event.dataTransfer.effectAllowed = 'move' }
    const onDragOver = (index, event) => { event.dataTransfer.dropEffect = 'move' }
    const onDrop = (targetIndex) => {
      if (dragIndex.value === null || dragIndex.value === targetIndex) return
      const order = [...localTabOrder.value]
      const [moved] = order.splice(dragIndex.value, 1)
      order.splice(targetIndex, 0, moved)
      localTabOrder.value = order
      dragIndex.value = null
      emitConfig()
    }
    const onDragEnd = () => { dragIndex.value = null }

    const emitConfig = () => {
      emit('update-tab-config', {
        tabOrder: [...localTabOrder.value],
        hiddenTabs: [...localHiddenTabs.value],
        defaultTab: localDefaultTab.value,
      })
    }

    const saveProfile = async () => {
      savingProfile.value = true; successMsg.value = ''; errorMsg.value = ''
      try {
        await authAPI.updateProfile(form.value)
        successMsg.value = 'profileSaved'
        setTimeout(() => { successMsg.value = '' }, 3000)
      } catch (e) {
        errorMsg.value = e.response?.data?.message || 'Failed to save profile'
      } finally { savingProfile.value = false }
    }

    const passwordStrength = computed(() => {
      const pw = passwordForm.value.newPassword
      if (!pw) return { level: 0, labelKey: '', colorClass: '', textClass: '' }
      let score = 0
      if (pw.length >= 6) score++
      if (pw.length >= 10) score++
      if (/[A-Z]/.test(pw) && /[a-z]/.test(pw)) score++
      if (/[0-9]/.test(pw) && /[^A-Za-z0-9]/.test(pw)) score++
      const levels = [
        { level: 1, labelKey: 'pwWeak', colorClass: 'bg-red-500', textClass: 'pw-text-red' },
        { level: 2, labelKey: 'pwFair', colorClass: 'bg-amber-500', textClass: 'pw-text-amber' },
        { level: 3, labelKey: 'pwGood', colorClass: 'bg-blue-500', textClass: 'pw-text-blue' },
        { level: 4, labelKey: 'pwStrong', colorClass: 'bg-emerald-500', textClass: 'pw-text-emerald' },
      ]
      return levels[Math.max(0, Math.min(score, 4) - 1)] || levels[0]
    })

    const canChangePassword = computed(() => {
      return passwordForm.value.currentPassword.length > 0 &&
        passwordForm.value.newPassword.length >= 6 &&
        passwordForm.value.newPassword === passwordForm.value.confirmPassword
    })

    const changePassword = async () => {
      if (!canChangePassword.value) return
      changingPassword.value = true
      successMsg.value = ''
      errorMsg.value = ''
      try {
        await authAPI.changePassword(passwordForm.value.currentPassword, passwordForm.value.newPassword)
        successMsg.value = 'passwordChanged'
        passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
        setTimeout(() => { successMsg.value = '' }, 3000)
      } catch (e) {
        errorMsg.value = e.response?.data?.message || 'Failed to change password'
      } finally {
        changingPassword.value = false
      }
    }

    const handleMobileLogout = () => { emit('logout') }

    return {
      form, currentUser, savingProfile, successMsg, errorMsg,
      passwordForm, changingPassword, showCurrentPassword, showNewPassword, showConfirmPassword,
      passwordStrength, canChangePassword, changePassword,
      orderedTabs, localDefaultTab, visibleTabCount, dragIndex,
      isTabHidden, toggleTabVisibility, setDefaultTab,
      onDragStart, onDragOver, onDrop, onDragEnd,
      saveProfile, handleMobileLogout,
    }
  }
}
</script>

<style scoped>
.profile-page {
  background: var(--bg-primary);
}

.profile-back-btn {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}
.profile-back-btn:hover {
  color: var(--text-primary);
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.profile-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 20px;
  box-shadow: var(--shadow-card, 0 1px 3px rgba(0,0,0,0.04));
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
}
.profile-card:hover {
  box-shadow: var(--shadow-md, 0 4px 16px rgba(0,0,0,0.06));
}

.profile-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile-card-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.profile-field label {
  display: block;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.profile-field input {
  width: 100%;
  padding: 10px 14px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-primary);
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
}
.profile-field input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}
.profile-field input::placeholder {
  color: var(--text-secondary);
  opacity: 0.5;
}

/* Tab items */
.profile-tab-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
}
.profile-tab-item:hover {
  border-color: var(--accent, #6366f1);
  transform: translateX(2px);
  box-shadow: var(--shadow-glow, 0 0 20px rgba(99, 102, 241, 0.1));
}
.profile-tab-item.is-hidden {
  opacity: 0.45;
}
.profile-tab-item.is-dragging {
  opacity: 0.6;
  transform: scale(0.98);
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.drag-handle {
  color: var(--text-secondary);
  cursor: grab;
  opacity: 0.4;
  transition: opacity 0.2s;
  padding: 2px;
}
.drag-handle:hover { opacity: 1; }
.drag-handle:active { cursor: grabbing; }

.tab-icon-circle {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: linear-gradient(135deg, var(--tw-gradient-from), var(--tw-gradient-to));
}

.default-chip {
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 3px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
  transition: all 0.2s ease;
  background: var(--bg-primary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}
.default-chip.is-default {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
}
.default-chip:not(.is-default):not(:disabled):hover {
  border-color: rgba(99, 102, 241, 0.4);
  color: #6366f1;
}

.visibility-toggle {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--text-secondary);
  border: 1px solid transparent;
}
.visibility-toggle.is-visible {
  color: #10b981;
}
.visibility-toggle:hover {
  background: var(--bg-primary);
  border-color: var(--border-color);
}

/* Password strength - work in both themes */
.pw-bar-empty { background: var(--border-color); opacity: 0.5; }
.pw-text-red { color: #ef4444; }
.pw-text-amber { color: #d97706; }
.pw-text-blue { color: #3b82f6; }
.pw-text-emerald { color: #10b981; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
