<template>
  <div :class="isDarkMode ? 'dark-theme' : 'light-theme'" class="h-screen overflow-hidden max-w-full">
    <div class="h-full app-bg flex flex-col overflow-hidden">
      
      <!-- Top Bar -->
      <header class="header-bg border-b border-color flex-shrink-0 overflow-hidden">
        <div class="flex items-center justify-between px-3 sm:px-4 py-2 sm:py-2.5 min-w-0">
          <div class="flex items-center gap-2 sm:gap-3 min-w-0 flex-shrink-1">
            <div class="w-8 h-8 sm:w-9 sm:h-9 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-lg sm:rounded-xl flex items-center justify-center shadow-lg">
              <svg class="w-4 h-4 sm:w-5 sm:h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
              </svg>
            </div>
            <div>
              <h1 class="font-bold text-sm sm:text-base text-primary leading-tight">{{ $t('appTitle') }}</h1>
              <p class="text-[10px] sm:text-xs text-secondary hidden sm:block leading-tight">{{ $t('university') }}</p>
            </div>
          </div>
          
          <div class="flex items-center gap-1.5 sm:gap-2 flex-shrink-0">
            <!-- Username + Logout -->
            <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 rounded-xl border user-info-pill">
              <span class="text-sm font-medium text-secondary">{{ currentUser?.username || 'User' }}</span>
              <button @click="handleLogout" class="p-1 rounded-lg logout-btn transition-colors" title="Logout">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                </svg>
              </button>
            </div>
            
            <!-- Active Users Badge - Hidden on mobile -->
            <div v-if="activeUserCount > 0" class="hidden md:flex items-center gap-2 px-3 py-1.5 rounded-xl active-users-badge">
              <span class="relative flex h-2.5 w-2.5">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-500"></span>
              </span>
              <span class="text-sm font-medium">{{ $t('activeUsers', { count: activeUserCount }) }}</span>
            </div>
            
            <!-- Language Toggle -->
            <button @click="toggleLanguage" class="w-9 h-9 sm:h-10 sm:w-auto sm:px-3 rounded-xl toggle-btn transition-all duration-300 hover:scale-105 flex items-center justify-center gap-2">
              <span class="text-base sm:text-lg">{{ locale === 'tr' ? '🇹🇷' : '🇬🇧' }}</span>
              <span class="font-bold text-sm text-primary hidden sm:inline">{{ locale === 'tr' ? 'TR' : 'EN' }}</span>
            </button>
            
            <!-- Theme Toggle -->
            <button @click="toggleTheme" class="w-9 h-9 sm:w-10 sm:h-10 rounded-xl toggle-btn transition-all duration-300 hover:scale-105 flex items-center justify-center">
              <svg v-if="isDarkMode" class="w-4 h-4 sm:w-5 sm:h-5 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464 4.95l.707.707a1 1 0 001.414-1.414l-.707-.707a1 1 0 00-1.414 1.414zm2.12-10.607a1 1 0 010 1.414l-.706.707a1 1 0 11-1.414-1.414l.707-.707a1 1 0 011.414 0zM17 11a1 1 0 100-2h-1a1 1 0 100 2h1zm-7 4a1 1 0 011 1v1a1 1 0 11-2 0v-1a1 1 0 011-1zM5.05 6.464A1 1 0 106.465 5.05l-.708-.707a1 1 0 00-1.414 1.414l.707.707zm1.414 8.486l-.707.707a1 1 0 01-1.414-1.414l.707-.707a1 1 0 011.414 1.414zM4 11a1 1 0 100-2H3a1 1 0 000 2h1z" clip-rule="evenodd"/>
              </svg>
              <svg v-else class="w-4 h-4 sm:w-5 sm:h-5 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z"/>
              </svg>
            </button>

            <!-- Mobile Menu Toggle -->
            <button @click="mobileMenuOpen = !mobileMenuOpen" class="w-9 h-9 rounded-xl toggle-btn lg:hidden transition-transform duration-300 hover:scale-105 flex items-center justify-center">
              <svg v-if="!mobileMenuOpen" class="w-5 h-5 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
              </svg>
              <svg v-else class="w-5 h-5 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>

            <!-- Profile Button (far right) -->
            <button 
              @click="showProfile = !showProfile"
              class="w-9 h-9 sm:w-10 sm:h-10 rounded-xl transition-all duration-300 hover:scale-105 flex items-center justify-center"
              :class="showProfile 
                ? 'bg-gradient-to-br from-indigo-500 to-purple-600 shadow-lg shadow-indigo-500/25' 
                : 'toggle-btn'"
              :title="$t('profileTitle')"
            >
              <svg class="w-5 h-5" :class="showProfile ? 'text-white' : 'text-primary'" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              </svg>
            </button>
          </div>
        </div>
        
        <!-- Tab Navigation in Top Bar (dynamic, user-customizable) -->
        <div class="topbar-tabs">
          <button 
            v-for="tab in visibleTabs"
            :key="tab.id"
            @click="showProfile = false; activeTab = tab.id"
            class="topbar-tab"
            :class="!showProfile && activeTab === tab.id ? 'active' : ''"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="tab.icon"/>
            </svg>
            <span>{{ $t(tab.labelKey) }}</span>
            <span v-if="tab.id === 'quota' && quotaWatchList.length > 0" class="topbar-tab-badge">{{ quotaWatchList.length }}</span>
            <span v-if="tab.id === 'grade' && gradeCourses.length > 0" class="topbar-tab-badge">{{ gradeCourses.length }}</span>
          </button>
        </div>
      </header>

      <div class="flex flex-1 overflow-hidden">
        <!-- Sidebar (hidden when profile is open) -->
        <aside 
          v-show="!showProfile"
          :class="[
            'fixed lg:relative inset-y-0 right-0 lg:inset-auto z-40 w-full sm:w-96 lg:w-80 max-w-full sidebar-bg lg:border-r border-color transform transition-transform duration-300 lg:transform-none flex flex-col flex-shrink-0 shadow-xl lg:shadow-none',
            mobileMenuOpen ? 'translate-x-0' : 'translate-x-full lg:translate-x-0'
          ]"
        >
          <!-- ===== TOP FIXED SECTION ===== -->
          <div class="flex-shrink-0 p-3 space-y-2 border-b border-color">
            <!-- Mobile Header with Close Button -->
            <div class="flex items-center justify-between lg:hidden mb-2">
              <h2 class="text-lg font-bold text-primary">{{ activeTab === 'schedule' ? $t('courses') : activeTab === 'quota' ? $t('quotaCheck') : $t('gradeCalculator') }}</h2>
              <button @click="mobileMenuOpen = false" class="p-2 rounded-lg toggle-btn">
                <svg class="w-5 h-5 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
              </button>
            </div>

            <!-- Aktif Dönem Card (Compact) -->
            <div class="season-card px-3 py-2 rounded-xl animate-fade-in flex items-center justify-between">
              <div class="flex items-center gap-2">
                <svg class="w-4 h-4 opacity-80" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                <span class="text-xs opacity-80">{{ $t('semester') }}:</span>
              </div>
              <span class="text-sm font-bold" v-if="activeSeason">{{ activeSeason.year }} {{ getLocalizedName(activeSeason) }}</span>
            </div>
            
            <!-- ===== SCHEDULE TAB CONTENT ===== -->
            <template v-if="activeTab === 'schedule'">
              <!-- Bölüm Seçimi -->
              <div class="space-y-1 relative">
                <select 
                  v-model="departmentId" 
                  @change="onDepartmentChange"
                  :disabled="loadingCourses"
                  class="w-full px-3 py-2 input-bg rounded-xl text-primary text-sm focus:ring-2 focus:ring-indigo-500 outline-none cursor-pointer transition-all duration-200 disabled:opacity-70"
                >
                  <option value="all">{{ $t('allDepartments') }}</option>
                  <option v-for="dept in filteredDepartments" :key="dept.id" :value="dept.id">
                    {{ getLocalizedName(dept) }}
                  </option>
                </select>
                <!-- Loading indicator -->
                <div v-if="loadingCourses" class="absolute right-3 top-1/2 -translate-y-1/2">
                  <svg class="animate-spin h-4 w-4 text-indigo-500" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                  </svg>
                </div>
              </div>

              <!-- Mod Seçimi -->
              <div v-if="availableCourses.length > 0" class="animate-fade-in">
                <div class="mode-toggle-container">
                  <div 
                    class="mode-toggle-slider"
                    :class="scheduleMode === 'MANUAL' ? 'slide-right' : 'slide-left'"
                  ></div>
                  <button 
                    @click="scheduleMode = 'AUTO'"
                    class="mode-toggle-btn"
                    :class="scheduleMode === 'AUTO' ? 'active' : ''"
                  >
                    <span class="mode-icon">🤖</span>
                    <span>{{ $t('auto') }}</span>
                  </button>
                  <button 
                    @click="scheduleMode = 'MANUAL'"
                    class="mode-toggle-btn"
                    :class="scheduleMode === 'MANUAL' ? 'active' : ''"
                  >
                    <span class="mode-icon">✋</span>
                    <span>{{ $t('manual') }}</span>
                  </button>
                </div>
                <p class="mode-description">
                  {{ scheduleMode === 'AUTO' ? $t('autoModeDesc') : $t('manualModeDesc') }}
                </p>
              </div>
            </template>

            <!-- ===== QUOTA TAB CONTENT (TOP) ===== -->
            <template v-if="activeTab === 'quota'">
              <!-- Department Selector -->
              <div class="space-y-1 relative">
                <select 
                  v-model="departmentId" 
                  @change="onDepartmentChange"
                  :disabled="loadingCourses"
                  class="w-full px-3 py-2 input-bg rounded-xl text-primary text-sm focus:ring-2 focus:ring-indigo-500 outline-none cursor-pointer transition-all duration-200 disabled:opacity-70"
                >
                  <option value="all">{{ $t('allDepartments') }}</option>
                  <option v-for="dept in filteredDepartments" :key="dept.id" :value="dept.id">
                    {{ getLocalizedName(dept) }}
                  </option>
                </select>
                <!-- Loading indicator -->
                <div v-if="loadingCourses" class="absolute right-3 top-1/2 -translate-y-1/2">
                  <svg class="animate-spin h-4 w-4 text-indigo-500" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                  </svg>
                </div>
              </div>
            </template>

            <!-- ===== GRADE TAB CONTENT (TOP) ===== -->
            <template v-if="activeTab === 'grade'">
              <p class="mode-description">
                {{ $t('gradeDescription') }}
              </p>
              <!-- Department Selector -->
              <div class="space-y-1 relative">
                <select 
                  v-model="departmentId" 
                  @change="onDepartmentChange"
                  :disabled="loadingCourses"
                  class="w-full px-3 py-2 input-bg rounded-xl text-primary text-sm focus:ring-2 focus:ring-indigo-500 outline-none cursor-pointer transition-all duration-200 disabled:opacity-70"
                >
                  <option value="all">{{ $t('allDepartments') }}</option>
                  <option v-for="dept in filteredDepartments" :key="dept.id" :value="dept.id">
                    {{ getLocalizedName(dept) }}
                  </option>
                </select>
                <div v-if="loadingCourses" class="absolute right-3 top-1/2 -translate-y-1/2">
                  <svg class="animate-spin h-4 w-4 text-indigo-500" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                  </svg>
                </div>
              </div>
            </template>
          </div>

          <!-- ===== MIDDLE SCROLLABLE SECTION ===== -->
          <div class="flex-1 flex flex-col min-h-0 overflow-hidden p-3" style="max-height: 50%">
            <!-- SCHEDULE TAB: Ders Listesi -->
            <div v-if="activeTab === 'schedule' && availableCourses.length > 0" class="courses-list-card animate-fade-in">
              <!-- Header -->
              <div class="courses-list-header">
                <span class="courses-list-title">{{ $t('courses') }}</span>
                <span class="courses-list-count">{{ groupedCourses.length }}</span>
              </div>
              
              <!-- Search -->
              <input 
                v-model="courseSearch"
                type="text"
                :placeholder="$t('searchPlaceholder')"
                class="courses-search-input"
              />
              
              <!-- Scrollable Course List -->
              <div class="courses-list-content space-y-1">
                <div 
                  v-for="course in filteredCourses" 
                  :key="course.code"
                  class="rounded-xl overflow-hidden"
                >
                  <!-- AUTO Mode: Click to select entire course -->
                  <div 
                    v-if="scheduleMode === 'AUTO'"
                    @click="toggleCourseAuto(course.code)"
                    class="px-3 py-2 cursor-pointer flex items-center gap-2 course-item transition-all duration-200"
                    :class="isCourseSelectedAuto(course.code) ? 'selected' : ''"
                  >
                    <div 
                      class="w-4 h-4 rounded border-2 flex items-center justify-center flex-shrink-0 transition-all duration-200 checkbox"
                      :class="isCourseSelectedAuto(course.code) ? 'checked' : ''"
                    >
                      <svg v-if="isCourseSelectedAuto(course.code)" class="w-2.5 h-2.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                      </svg>
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-1.5">
                        <span class="font-mono text-xs font-bold text-primary">{{ course.code }}</span>
                        <span class="text-[10px] px-1.5 py-0.5 rounded-full badge-section font-semibold">{{ course.sections.length }}</span>
                      </div>
                      <p class="text-[10px] text-secondary truncate">{{ getLocalizedName(course) }}</p>
                      <p v-if="getCourseInstructors(course)" class="text-[10px] text-indigo-400 truncate">👤 {{ getCourseInstructors(course) }}</p>
                    </div>
                  </div>

                  <!-- MANUAL Mode: Expand to select sections -->
                  <template v-else>
                    <div 
                      @click="toggleCourseExpand(course.code)"
                      class="px-3 py-2 cursor-pointer flex items-center gap-2 course-item transition-all duration-200"
                    >
                      <svg 
                        class="w-3 h-3 text-secondary transition-transform duration-200 flex-shrink-0" 
                        :class="expandedCourses.includes(course.code) ? 'rotate-90' : ''"
                        fill="none" stroke="currentColor" viewBox="0 0 24 24"
                      >
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                      </svg>
                      <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-1.5">
                          <span class="font-mono text-xs font-bold text-primary">{{ course.code }}</span>
                          <span class="text-[10px] px-1.5 py-0.5 rounded-full badge-section font-semibold">{{ course.sections.length }}</span>
                        </div>
                        <p class="text-[10px] text-secondary truncate">{{ getLocalizedName(course) }}</p>
                        <p v-if="getCourseInstructors(course)" class="text-[10px] text-indigo-400 truncate">👤 {{ getCourseInstructors(course) }}</p>
                      </div>
                    </div>
                    
                    <!-- Section'lar -->
                    <div v-if="expandedCourses.includes(course.code)" class="section-list border-t border-color animate-expand">
                      <div 
                        v-for="section in course.sections" 
                        :key="`${course.code}-${section.section}`"
                        @click="toggleSection(course.code, section.section)"
                        class="px-3 py-2 cursor-pointer flex items-center gap-2 section-item transition-all duration-200 border-b border-color last:border-b-0"
                        :class="isSelected(course.code, section.section) ? 'selected' : ''"
                      >
                        <div 
                          class="w-4 h-4 rounded border-2 flex items-center justify-center flex-shrink-0 transition-all duration-200 checkbox"
                          :class="isSelected(course.code, section.section) ? 'checked' : ''"
                        >
                          <svg v-if="isSelected(course.code, section.section)" class="w-2.5 h-2.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                          </svg>
                        </div>
                        <div class="flex-1 min-w-0">
                          <div class="flex items-center gap-2">
                            <span class="text-xs font-semibold text-primary">{{ $t('group') }} {{ section.section }}</span>
                            <span v-if="section.instructor" class="text-[10px] text-indigo-400">• {{ section.instructor }}</span>
                          </div>
                          <div class="text-[10px] text-secondary">
                            <span v-for="(detail, idx) in section.details" :key="idx">
                              {{ getDayShort(detail.day) }} {{ detail.startHour }}-{{ detail.endHour }}{{ idx < section.details.length - 1 ? ', ' : '' }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>

            <!-- Boş Durum / Yükleniyor -->
            <!-- SCHEDULE TAB: Loading/Empty State -->
            <div v-else-if="activeTab === 'schedule'" class="courses-list-card">
              <!-- Modern Loading State -->
              <div v-if="loadingCourses" class="courses-loading-container">
                <div class="courses-loading-content">
                  <!-- Animated Icon -->
                  <div class="courses-loading-icon">
                    <svg viewBox="0 0 24 24" fill="none" class="w-10 h-10">
                      <path d="M12 6.5V3M12 21v-3.5M6.5 12H3M21 12h-3.5M18.364 5.636l-2.475 2.475M8.111 15.889l-2.475 2.475M5.636 5.636l2.475 2.475M15.889 15.889l2.475 2.475" 
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" class="courses-loading-rays"/>
                      <circle cx="12" cy="12" r="4" fill="currentColor" class="courses-loading-center"/>
                    </svg>
                  </div>
                  
                  <!-- Text -->
                  <p class="courses-loading-text">{{ $t('loading') }}</p>
                  
                  <!-- Skeleton Cards -->
                  <div class="courses-skeleton-list">
                    <div class="courses-skeleton-item" v-for="i in 4" :key="i" :style="{ animationDelay: `${i * 0.1}s` }">
                      <div class="skeleton-line skeleton-title"></div>
                      <div class="skeleton-line skeleton-subtitle"></div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- Empty State -->
              <div v-else class="courses-empty-state">
                <svg class="w-12 h-12 text-gray-400 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
                <p class="text-secondary text-sm">{{ $t('noCourses') }}</p>
              </div>
            </div>

            <!-- QUOTA TAB: Course List (same as schedule tab) -->
            <div v-else-if="activeTab === 'quota' && availableCourses.length > 0" class="courses-list-card animate-fade-in">
              <!-- Header -->
              <div class="courses-list-header">
                <span class="courses-list-title">{{ $t('courses') }}</span>
                <span class="courses-list-count">{{ groupedCourses.length }}</span>
              </div>
              
              <!-- Search -->
              <input 
                v-model="quotaCourseSearch"
                type="text"
                :placeholder="$t('searchPlaceholder')"
                class="courses-search-input"
              />
              
              <!-- Scrollable Course List -->
              <div class="courses-list-content space-y-1">
                <div 
                  v-for="course in quotaFilteredCourses" 
                  :key="course.code"
                  class="rounded-xl overflow-hidden"
                >
                  <!-- Course Header - Click to expand -->
                  <div 
                    @click="toggleQuotaCourseExpand(course.code)"
                    class="px-3 py-2 cursor-pointer flex items-center gap-2 course-item transition-all duration-200"
                    :class="hasWatchedSections(course.code) ? 'selected' : ''"
                  >
                    <div 
                      class="w-4 h-4 rounded border-2 flex items-center justify-center flex-shrink-0 transition-all duration-200 checkbox"
                      :class="hasWatchedSections(course.code) ? 'checked' : ''"
                    >
                      <svg v-if="hasWatchedSections(course.code)" class="w-2.5 h-2.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                      </svg>
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-1.5">
                        <span class="font-mono text-xs font-bold text-primary">{{ course.code }}</span>
                        <span class="text-[10px] px-1.5 py-0.5 rounded-full badge-section font-semibold">{{ course.sections.length }}</span>
                      </div>
                      <p class="text-[10px] text-secondary truncate">{{ getLocalizedName(course) }}</p>
                      <p v-if="getCourseInstructors(course)" class="text-[10px] text-indigo-400 truncate">👤 {{ getCourseInstructors(course) }}</p>
                    </div>
                    <!-- Expand Arrow -->
                    <svg 
                      class="w-4 h-4 text-secondary transition-transform duration-200"
                      :class="quotaExpandedCourses.includes(course.code) ? 'rotate-180' : ''"
                      fill="none" stroke="currentColor" viewBox="0 0 24 24"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                    </svg>
                  </div>
                  
                  <!-- Sections - Expandable -->
                  <div v-if="quotaExpandedCourses.includes(course.code)" class="bg-opacity-50 section-list animate-expand">
                    <div 
                      v-for="section in course.sections" 
                      :key="section.section"
                      @click="toggleWatchSection(course, section)"
                      class="section-item cursor-pointer flex items-center gap-2 px-3 py-2 border-t border-color transition-all duration-200"
                      :class="isInWatchList(course.code, section.section) ? 'section-selected' : ''"
                    >
                      <div 
                        class="w-3.5 h-3.5 rounded border-2 flex items-center justify-center flex-shrink-0 transition-all duration-200 checkbox-small"
                        :class="isInWatchList(course.code, section.section) ? 'checked' : ''"
                      >
                        <svg v-if="isInWatchList(course.code, section.section)" class="w-2 h-2 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                        </svg>
                      </div>
                      <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-2">
                          <span class="text-xs font-semibold text-primary">{{ $t('group') }} {{ section.section }}</span>
                          <span v-if="section.instructor" class="text-[10px] text-indigo-400">• {{ section.instructor }}</span>
                        </div>
                      </div>
                      <!-- Quota badge if available -->
                      <div v-if="getWatchedQuota(course.code, section.section)?.remaining != null" class="quota-mini-badge" :class="getWatchedQuota(course.code, section.section)?.remaining > 0 ? 'quota-green' : 'quota-red'">
                        {{ getWatchedQuota(course.code, section.section)?.remaining }}/{{ getWatchedQuota(course.code, section.section)?.capacity }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- QUOTA TAB: Loading/Empty State -->
            <div v-else-if="activeTab === 'quota'" class="courses-list-card">
              <div v-if="loadingCourses" class="courses-loading-container">
                <div class="courses-loading-content">
                  <div class="courses-loading-icon">
                    <svg viewBox="0 0 24 24" fill="none" class="w-10 h-10">
                      <path d="M12 6.5V3M12 21v-3.5M6.5 12H3M21 12h-3.5M18.364 5.636l-2.475 2.475M8.111 15.889l-2.475 2.475M5.636 5.636l2.475 2.475M15.889 15.889l2.475 2.475" 
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" class="courses-loading-rays"/>
                      <circle cx="12" cy="12" r="4" fill="currentColor" class="courses-loading-center"/>
                    </svg>
                  </div>
                  <p class="courses-loading-text">{{ $t('loading') }}</p>
                </div>
              </div>
              <div v-else class="courses-empty-state">
                <svg class="w-12 h-12 text-gray-400 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
                <p class="text-secondary text-sm">{{ $t('noCourses') }}</p>
              </div>
            </div>

            <!-- GRADE TAB: Real Course List (expandable like quota) -->
            <div v-else-if="activeTab === 'grade' && availableCourses.length > 0" class="courses-list-card animate-fade-in">
              <div class="courses-list-header">
                <span class="courses-list-title">{{ $t('courses') }}</span>
                <span class="courses-list-count">{{ groupedCourses.length }}</span>
              </div>
              
              <input 
                v-model="gradeCourseSearch"
                type="text"
                :placeholder="$t('searchPlaceholder')"
                class="courses-search-input"
              />
              
              <div class="courses-list-content space-y-1">
                <div 
                  v-for="course in gradeFilteredCourses" 
                  :key="course.code"
                  class="rounded-xl overflow-hidden"
                >
                  <!-- Course Header - Click to select -->
                  <div 
                    @click="toggleGradeCourse(course)"
                    class="px-3 py-2 cursor-pointer flex items-center gap-2 course-item transition-all duration-200"
                    :class="isGradeCourseSelected(course.code) ? 'selected' : ''"
                  >
                    <div 
                      class="w-4 h-4 rounded border-2 flex items-center justify-center flex-shrink-0 transition-all duration-200 checkbox"
                      :class="isGradeCourseSelected(course.code) ? 'checked' : ''"
                    >
                      <svg v-if="isGradeCourseSelected(course.code)" class="w-2.5 h-2.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                      </svg>
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-1.5">
                        <span class="font-mono text-xs font-bold text-primary">{{ course.code }}</span>
                        <span class="text-[10px] px-1.5 py-0.5 rounded-full badge-section font-semibold">{{ course.sections.length }}</span>
                      </div>
                      <p class="text-[10px] text-secondary truncate">{{ getLocalizedName(course) }}</p>
                      <p v-if="getCourseInstructors(course)" class="text-[10px] text-indigo-400 truncate">👤 {{ getCourseInstructors(course) }}</p>
                    </div>
                    <!-- Grade badge if already calculated -->
                    <span v-if="isGradeCourseSelected(course.code) && getGradeCourseScore(course.code) !== null" 
                          class="text-xs font-black px-2 py-0.5 rounded-md flex-shrink-0"
                          :class="getLetterGradeColor(getLetterGrade(getGradeCourseScore(course.code)))"
                          style="background: rgba(255,255,255,0.05)">
                      {{ getLetterGrade(getGradeCourseScore(course.code)) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- GRADE TAB: Loading/Empty State -->
            <div v-else-if="activeTab === 'grade'" class="courses-list-card">
              <div v-if="loadingCourses" class="courses-loading-container">
                <div class="courses-loading-content">
                  <div class="courses-loading-icon">
                    <svg viewBox="0 0 24 24" fill="none" class="w-10 h-10">
                      <path d="M12 6.5V3M12 21v-3.5M6.5 12H3M21 12h-3.5M18.364 5.636l-2.475 2.475M8.111 15.889l-2.475 2.475M5.636 5.636l2.475 2.475M15.889 15.889l2.475 2.475" 
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" class="courses-loading-rays"/>
                      <circle cx="12" cy="12" r="4" fill="currentColor" class="courses-loading-center"/>
                    </svg>
                  </div>
                  <p class="courses-loading-text">{{ $t('loading') }}</p>
                </div>
              </div>
              <div v-else class="courses-empty-state">
                <svg class="w-12 h-12 text-gray-400 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
                <p class="text-secondary text-sm">{{ $t('noCourses') }}</p>
              </div>
            </div>
          </div>

          <!-- ===== BOTTOM FIXED SECTION ===== -->
          <div class="flex-1 p-3 border-t border-color flex flex-col gap-2 min-h-0 overflow-auto">
            
            <!-- ===== SCHEDULE TAB BOTTOM ===== -->
            <!-- Seçilen Dersler - AUTO Mode -->
            <div v-if="activeTab === 'schedule' && scheduleMode === 'AUTO' && selectedCoursesAuto.length > 0" class="animate-fade-in flex-1 min-h-0 flex flex-col">
              <div class="selected-courses-card">
                <!-- Header -->
                <div class="selected-courses-header">
                  <div class="flex items-center gap-2">
                    <span class="selected-courses-title">{{ $t('selected') }}</span>
                    <span class="selected-courses-count">{{ selectedCoursesAuto.length }}</span>
                  </div>
                  <button @click="clearAllCoursesAuto" class="clear-all-btn">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                    {{ $t('clearAll') }}
                  </button>
                </div>
                
                <!-- Course Tags -->
                <div class="selected-courses-content">
                  <span 
                    v-for="code in selectedCoursesAuto" 
                    :key="code"
                    class="inline-flex items-center gap-1 px-2.5 py-1.5 text-xs font-bold rounded-lg tag-selected"
                  >
                    {{ code }}
                    <button @click.stop="removeCourseAuto(code)" class="hover:opacity-70 transition-opacity">
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                      </svg>
                    </button>
                  </span>
                </div>
              </div>
            </div>

            <!-- Seçilen Dersler - MANUAL Mode -->
            <div v-if="activeTab === 'schedule' && scheduleMode === 'MANUAL' && selectedSections.length > 0" class="animate-fade-in flex-1 min-h-0 flex flex-col">
              <div class="selected-courses-card">
                <!-- Header -->
                <div class="selected-courses-header">
                  <div class="flex items-center gap-2">
                    <span class="selected-courses-title">{{ $t('selected') }}</span>
                    <span class="selected-courses-count">{{ selectedSections.length }}</span>
                  </div>
                  <button @click="clearAllSections" class="clear-all-btn">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                    {{ $t('clearAll') }}
                  </button>
                </div>
                
                <!-- Çakışma Uyarısı -->
                <div v-if="conflicts.length > 0" class="p-2 conflict-warning rounded-lg mb-2 text-xs">
                  ⚠️ {{ $t('conflictWarning') }}
                </div>

                <!-- Course Tags -->
                <div class="selected-courses-content">
                  <span 
                    v-for="sel in selectedSections" 
                    :key="`${sel.code}-${sel.section}`"
                    class="inline-flex items-center gap-1 px-2.5 py-1.5 text-xs font-bold rounded-lg"
                    :class="isInConflict(sel.code) ? 'tag-conflict' : 'tag-selected'"
                  >
                    {{ sel.code }}
                    <button @click.stop="removeSection(sel.code, sel.section)" class="hover:opacity-70 transition-opacity">
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                      </svg>
                    </button>
                  </span>
                </div>
              </div>
            </div>

            <!-- Generating Indicator -->
            <div 
              v-if="activeTab === 'schedule' && generating"
              class="w-full py-2 text-center text-xs font-semibold text-secondary flex items-center justify-center gap-2 flex-shrink-0"
            >
              <svg class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
              </svg>
              {{ $t('generating') }}
            </div>

            <!-- ===== QUOTA TAB BOTTOM ===== -->
            <!-- Watch List Display -->
            <div v-if="activeTab === 'quota' && quotaWatchList.length > 0" class="animate-fade-in flex-1 min-h-0 flex flex-col">
              <div class="selected-courses-card">
                <!-- Header -->
                <div class="selected-courses-header">
                  <div class="flex items-center gap-2">
                    <span class="selected-courses-title">{{ $t('watchList') }}</span>
                    <span class="selected-courses-count">{{ quotaWatchList.length }}</span>
                  </div>
                  <button @click="clearWatchList" class="clear-all-btn">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                    {{ $t('clearAll') }}
                  </button>
                </div>
                
                <!-- Watch List Tags with Quota -->
                <div class="selected-courses-content">
                  <span 
                    v-for="(item, index) in quotaWatchList" 
                    :key="`${item.code}-${item.section}`"
                    class="quota-tag"
                    :class="{ 'quota-tag-available': item.remaining > 0, 'quota-tag-full': item.remaining === 0 && item.remaining !== null }"
                  >
                    <span class="quota-tag-code">{{ item.code }}</span>
                    <span class="quota-tag-section">G{{ item.section }}</span>
                    <span v-if="item.remaining !== null" class="quota-tag-value">
                      {{ item.remaining }}/{{ item.capacity }}
                    </span>
                    <button @click.stop="removeFromWatchList(index)" class="hover:opacity-70 transition-opacity">
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                      </svg>
                    </button>
                  </span>
                </div>
              </div>
            </div>

            <!-- Refresh Quotas Button -->
            <button 
              v-if="activeTab === 'quota'"
              @click="refreshQuotas"
              :disabled="quotaLoading || quotaWatchList.length === 0"
              class="w-full py-2.5 btn-success text-white text-sm font-bold rounded-xl disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 flex items-center justify-center gap-2 hover:shadow-lg mt-auto flex-shrink-0"
            >
              <svg v-if="quotaLoading" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
              </svg>
              {{ quotaLoading ? $t('checking') : $t('refreshQuotas') }}
              <span v-if="lastQuotaCheck && !quotaLoading" class="text-[10px] opacity-70 ml-1">({{ formatLastCheck(lastQuotaCheck) }})</span>
            </button>

            <!-- ===== GRADE TAB BOTTOM ===== -->
            <div v-if="activeTab === 'grade' && gradeCourses.length > 0" class="animate-fade-in flex-1 min-h-0 flex flex-col">
              <div class="selected-courses-card">
                <div class="selected-courses-header">
                  <div class="flex items-center gap-2">
                    <span class="selected-courses-title">{{ $t('selected') }}</span>
                    <span class="selected-courses-count">{{ gradeCourses.length }}</span>
                  </div>
                  <button @click="clearAllGrades" class="clear-all-btn">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                    {{ $t('clearAll') }}
                  </button>
                </div>
                <div class="selected-courses-content">
                  <span 
                    v-for="course in gradeCourses" 
                    :key="course.id"
                    class="inline-flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-bold rounded-lg tag-selected"
                  >
                    {{ course.code }}
                    <span v-if="getCourseWeightedScore(course).filledWeight > 0" 
                          class="text-[10px] font-black px-1 rounded"
                          :class="getLetterGradeColor(getLetterGrade(getCourseWeightedScore(course).score))"
                          style="background: rgba(255,255,255,0.1)">
                      {{ getLetterGrade(getCourseWeightedScore(course).score) }}
                    </span>
                    <button @click.stop="removeGradeCourse(course.id)" class="hover:opacity-70 transition-opacity">
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                      </svg>
                    </button>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <!-- Mobile Overlay -->
        <div 
          v-if="mobileMenuOpen" 
          @click="mobileMenuOpen = false"
          class="fixed inset-0 bg-black/50 z-30 lg:hidden backdrop-blur-sm animate-fade-in"
        ></div>

        <!-- Main Content -->
        <main class="flex-1 min-w-0 overflow-hidden flex flex-col">
          
          <!-- Profile Page -->
          <Profile 
            v-if="showProfile"
            :tab-order="tabOrder"
            :hidden-tabs="hiddenTabs"
            :default-tab="defaultTab"
            @close="showProfile = false"
            @update-tab-config="handleTabConfigUpdate"
            @logout="handleLogout"
          />

          <!-- Hata -->
          <div v-if="!showProfile && error" class="mx-4 mt-4 p-4 error-box rounded-xl flex items-center gap-3 animate-shake">
            <svg class="w-6 h-6 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <p class="text-sm font-medium">{{ error }}</p>
          </div>

          <!-- ===== QUOTA TAB: Main Content ===== -->
          <div v-if="!showProfile && activeTab === 'quota'" class="flex-1 overflow-auto p-4 space-y-4">
            <!-- Quota Stats -->
            <div v-if="quotaWatchList.length > 0" class="stats-container animate-slide-down">
              <div class="stats-grid">
                <div class="stat-item stat-cyan">
                  <span class="stat-value">{{ quotaWatchList.length }}</span>
                  <span class="stat-label">{{ $t('watchedCourses') }}</span>
                </div>
                <div class="stat-item stat-green">
                  <span class="stat-value">{{ quotaWatchList.filter(c => c.remaining > 0).length }}</span>
                  <span class="stat-label">{{ $t('available') }}</span>
                </div>
                <div class="stat-item stat-red">
                  <span class="stat-value">{{ quotaWatchList.filter(c => c.remaining === 0).length }}</span>
                  <span class="stat-label">{{ $t('full') }}</span>
                </div>
                <div class="stat-item stat-purple">
                  <span class="stat-value">{{ quotaWatchList.filter(c => c.remaining === null).length }}</span>
                  <span class="stat-label">{{ $t('pending') }}</span>
                </div>
              </div>
              
              <div class="stats-footer">
                <span class="stats-season">{{ activeSeason?.year }} {{ activeSeason ? getLocalizedName(activeSeason) : '' }}</span>
                <span class="stats-date" v-if="lastQuotaCheck">{{ $t('lastChecked') }}: {{ formatLastCheck(lastQuotaCheck) }}</span>
              </div>
            </div>

            <!-- Quota Courses Cards -->
            <div v-if="quotaWatchList.length > 0" class="card-bg rounded-2xl p-5 animate-fade-in">
              <h2 class="text-xl font-bold text-primary mb-5 flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center shadow-lg">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                  </svg>
                </div>
                {{ $t('watchedCoursesDetail') }}
              </h2>
              
              <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
                <div 
                  v-for="(course, idx) in quotaWatchList" 
                  :key="`${course.code}-${course.section}`"
                  class="quota-detail-card rounded-2xl overflow-hidden animate-slide-up"
                  :class="{ 'quota-available': course.remaining > 0, 'quota-full': course.remaining === 0, 'quota-pending': course.remaining === null }"
                  :style="{ animationDelay: `${idx * 0.1}s` }"
                >
                  <!-- Course Header with gradient -->
                  <div class="quota-detail-header p-4">
                    <div class="flex items-start justify-between">
                      <div class="flex-1 min-w-0 pr-2">
                        <h3 class="font-black text-white text-lg tracking-wide">{{ course.code }}</h3>
                        <p class="text-sm text-white/80 mt-1 course-name-truncate" :title="locale === 'tr' ? course.nameTr : course.nameEn">
                          {{ locale === 'tr' ? course.nameTr : course.nameEn }}
                        </p>
                      </div>
                      <span class="px-3 py-1.5 text-xs font-black rounded-lg bg-white/20 text-white backdrop-blur-sm flex-shrink-0">
                        {{ $t('group') }} {{ course.section }}
                      </span>
                    </div>
                    
                    <!-- Quota Display in header -->
                    <div class="flex gap-3 mt-auto pt-3">
                      <span v-if="course.remaining !== null" class="px-4 py-1.5 rounded-lg text-white text-sm font-bold" 
                            :class="course.remaining > 0 ? 'bg-green-500/30' : 'bg-red-500/30'">
                        {{ $t('remaining') }}: {{ course.remaining }}
                      </span>
                      <span v-if="course.capacity !== null" class="px-4 py-1.5 rounded-lg bg-white/15 text-white text-sm font-semibold">
                        {{ $t('capacity') }}: {{ course.capacity }}
                      </span>
                      <span v-if="course.loading" class="px-4 py-1.5 rounded-lg bg-white/15 text-white text-sm font-semibold flex items-center gap-2">
                        <svg class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
                        </svg>
                        {{ $t('checking') }}
                      </span>
                    </div>
                  </div>
                  
                  <!-- Course Body -->
                  <div class="p-4 space-y-3">
                    <!-- Quota Progress Bar -->
                    <div v-if="course.capacity !== null && course.remaining !== null" class="quota-progress">
                      <div class="flex justify-between text-xs mb-1">
                        <span class="text-secondary">{{ $t('enrolled') }}</span>
                        <span class="font-bold" :class="course.remaining > 0 ? 'text-green-500' : 'text-red-500'">
                          {{ course.enrolled || (course.capacity - course.remaining) }}/{{ course.capacity }}
                        </span>
                      </div>
                      <div class="h-2 rounded-full bg-gray-700 overflow-hidden">
                        <div 
                          class="h-full rounded-full transition-all duration-500"
                          :class="course.remaining > 0 ? 'bg-gradient-to-r from-green-400 to-emerald-500' : 'bg-gradient-to-r from-red-400 to-rose-500'"
                          :style="{ width: `${course.capacity ? ((course.capacity - (course.remaining || 0)) / course.capacity) * 100 : 0}%` }"
                        ></div>
                      </div>
                    </div>

                    <!-- Instructor -->
                    <div v-if="course.instructor" class="detail-item">
                      <div class="detail-icon">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                        </svg>
                      </div>
                      <span class="detail-text">{{ course.instructor }}</span>
                    </div>

                    <!-- Remove Button -->
                    <button 
                      @click="removeFromWatchList(quotaWatchList.indexOf(course))"
                      class="w-full mt-2 py-2 text-xs font-semibold rounded-lg bg-red-500/20 text-red-400 hover:bg-red-500/30 transition-all"
                    >
                      {{ $t('removeFromWatch') }}
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Empty State for Quota Tab -->
            <div v-else class="min-h-[60vh] flex flex-col items-center justify-center text-center px-6 py-12">
              <div class="w-24 h-24 mb-6 rounded-3xl empty-icon flex items-center justify-center animate-float">
                <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-primary mb-2">{{ $t('noWatchedCourses') }}</h3>
              <p class="text-secondary text-base max-w-sm">
                {{ $t('noWatchedCoursesMessage') }}
              </p>
              <button 
                @click="mobileMenuOpen = true"
                class="mt-6 px-6 py-3 btn-primary text-white text-sm font-bold rounded-xl lg:hidden hover:shadow-lg transition-all duration-300"
              >
                {{ $t('addCourseToWatch') }}
              </button>
            </div>
          </div>

          <!-- ===== SCHEDULE TAB: Sonuç ===== -->
          <div v-else-if="!showProfile && activeTab === 'schedule' && scheduleResult && scheduleResult.success" class="flex-1 overflow-auto p-4 space-y-4">
            
            <!-- Uyarılar Grid -->
            <div v-if="scheduleResult.hasOverlap || (scheduleResult.excludedCourses && scheduleResult.excludedCourses.length > 0)" 
                 class="grid gap-3 animate-fade-in"
                 :class="scheduleResult.hasOverlap && scheduleResult.excludedCourses?.length > 0 ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'">
              
              <!-- Çakışma Uyarısı -->
              <div v-if="scheduleResult.hasOverlap" class="warning-card warning-yellow">
                <div class="warning-header">
                  <div class="warning-icon warning-icon-yellow">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                  </div>
                  <div>
                    <h3 class="warning-title text-yellow-400">{{ $t('overlapWarning') }}</h3>
                    <p class="warning-subtitle">{{ $t('overlapMessage', { minutes: scheduleResult.overlapMinutes }) }}</p>
                  </div>
                </div>
                
                <!-- Çakışma Detayları - Compact -->
                <div v-if="scheduleResult.overlapDetails && scheduleResult.overlapDetails.length > 0" class="warning-content">
                  <div v-for="(overlap, idx) in scheduleResult.overlapDetails" :key="idx" class="overlap-item">
                    <div class="overlap-courses">
                      <span class="overlap-code">{{ overlap.course1Code }}</span>
                      <svg class="w-3 h-3 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
                      </svg>
                      <span class="overlap-code">{{ overlap.course2Code }}</span>
                    </div>
                    <div class="overlap-details">
                      {{ overlap.dayTurkish }} • {{ overlap.overlapStart }} - {{ overlap.overlapEnd }}
                      <span class="overlap-duration">({{ overlap.overlapMinutes }} dk)</span>
                    </div>
                  </div>
                </div>
                
                <p class="warning-tip">💡 {{ $t('overlapTip') }}</p>
              </div>
              
              <!-- Kısmi Çözüm Uyarısı -->
              <div v-if="scheduleResult.excludedCourses && scheduleResult.excludedCourses.length > 0" class="warning-card warning-orange">
                <div class="warning-header">
                  <div class="warning-icon warning-icon-orange">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                    </svg>
                  </div>
                  <div>
                    <h3 class="warning-title text-amber-400">{{ $t('partialSchedule') }}</h3>
                    <p class="warning-subtitle">{{ $t('partialScheduleMessage') }}</p>
                  </div>
                </div>
                <div class="warning-content">
                  <div class="excluded-courses">
                    <span v-for="code in scheduleResult.excludedCourses" :key="code" class="excluded-badge">
                      {{ code }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- İstatistikler Grid -->
            <div class="stats-container animate-slide-down">
              <div class="stats-grid">
                <div class="stat-item stat-cyan">
                  <span class="stat-value">{{ scheduleResult.selectedCourses?.length || 0 }}</span>
                  <span class="stat-label">{{ $t('totalCourses') }}</span>
                </div>
                <div class="stat-item stat-blue">
                  <span class="stat-value">{{ scheduleResult.totalCredits }}</span>
                  <span class="stat-label">{{ $t('totalCredits') }}</span>
                </div>
                <div class="stat-item stat-purple">
                  <span class="stat-value">{{ scheduleResult.totalEcts }}</span>
                  <span class="stat-label">{{ $t('totalEcts') }}</span>
                </div>
                <div class="stat-item stat-green">
                  <span class="stat-value">{{ scheduleResult.metrics?.daysWithClasses }}</span>
                  <span class="stat-label">{{ $t('daysWithClasses') }}</span>
                </div>
                <div class="stat-item stat-orange">
                  <span class="stat-value">{{ scheduleResult.metrics?.totalGaps }}<small>{{ $t('minutes') }}</small></span>
                  <span class="stat-label">{{ $t('gaps') }}</span>
                </div>
                <div v-if="scheduleResult.hasOverlap" class="stat-item stat-red">
                  <span class="stat-value">{{ scheduleResult.overlapMinutes }}<small>{{ $t('minutes') }}</small></span>
                  <span class="stat-label">{{ $t('overlap') }}</span>
                </div>
              </div>
              
              <!-- Program Başlığı -->
              <div class="stats-footer">
                <span class="stats-season">{{ activeSeason?.year }} {{ activeSeason ? getLocalizedName(activeSeason) : '' }}</span>
                <span class="stats-date">{{ new Date().toLocaleDateString(locale === 'tr' ? 'tr-TR' : 'en-US') }}</span>
              </div>
            </div>

            <!-- Haftalık Program -->
            <div class="card-bg rounded-2xl p-4">
              <h2 class="text-lg font-bold text-primary mb-4 flex items-center gap-2">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                {{ $t('weeklySchedule') }}
              </h2>
              <WeeklySchedule :schedule="scheduleResult.weeklySchedule" :isDarkMode="isDarkMode" :locale="locale" />
            </div>
            
            <!-- Oluşturulan Programdaki Dersler -->
            <div class="card-bg rounded-2xl p-5 animate-fade-in">
              <h2 class="text-xl font-bold text-primary mb-5 flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-600 flex items-center justify-center shadow-lg">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
                  </svg>
                </div>
                {{ $t('scheduledCourses') }}
              </h2>
              
              <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
                <div 
                  v-for="(course, idx) in scheduleResult.selectedCourses" 
                  :key="course.code"
                  class="course-detail-card rounded-2xl overflow-hidden animate-slide-up"
                  :style="{ animationDelay: `${idx * 0.1}s` }"
                >
                  <!-- Course Header with gradient -->
                  <div class="course-detail-header p-4">
                    <div class="flex items-start justify-between">
                      <div class="flex-1 min-w-0 pr-2">
                        <h3 class="font-black text-white text-lg tracking-wide">{{ course.code }}</h3>
                        <p class="text-sm text-white/80 mt-1 course-name-truncate" :title="getLocalizedName(course)">{{ getLocalizedName(course) }}</p>
                      </div>
                      <span class="px-3 py-1.5 text-xs font-black rounded-lg bg-white/20 text-white backdrop-blur-sm flex-shrink-0">
                        {{ $t('group') }} {{ course.section }}
                      </span>
                    </div>
                    
                    <!-- Credits in header -->
                    <div class="flex gap-3 mt-auto pt-3">
                      <span class="px-3 py-1 rounded-lg bg-white/15 text-white text-sm font-semibold">
                        {{ course.credit }} {{ $t('credit') }}
                      </span>
                      <span class="px-3 py-1 rounded-lg bg-white/15 text-white text-sm font-semibold">
                        {{ course.ects }} AKTS
                      </span>
                    </div>
                  </div>
                  
                  <!-- Quota Info Section -->
                  <div v-if="course.quota != null" class="px-4 pt-4 pb-1">
                    <div class="quota-progress">
                      <div class="flex items-center justify-between mb-2">
                        <span class="text-xs font-bold flex items-center gap-1.5" :class="(course.quota - (course.fullQuota || 0)) > 0 ? 'text-green-500' : 'text-red-500'">
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
                          </svg>
                          {{ $t('quota') }}: {{ course.quota - (course.fullQuota || 0) }}/{{ course.quota }}
                        </span>
                        <span class="text-[10px] font-semibold text-secondary">
                          {{ $t('enrolled') }}: {{ course.fullQuota || 0 }}
                        </span>
                      </div>
                      <div class="h-2 rounded-full bg-gray-700 overflow-hidden">
                        <div 
                          class="h-full rounded-full transition-all duration-500"
                          :class="(course.quota - (course.fullQuota || 0)) > 0 ? 'bg-gradient-to-r from-green-400 to-emerald-500' : 'bg-gradient-to-r from-red-400 to-rose-500'"
                          :style="{ width: `${course.quota ? ((course.fullQuota || 0) / course.quota) * 100 : 0}%` }"
                        ></div>
                      </div>
                    </div>
                  </div>

                  <!-- Course Details (Lectures, Labs, etc.) -->
                  <div class="p-4 space-y-3">
                    <div 
                      v-for="(detail, didx) in course.details" 
                      :key="didx"
                      class="detail-row rounded-xl p-3"
                      :class="getDetailTypeClass(detail.typeShort)"
                    >
                      <div class="flex items-center justify-between mb-2">
                        <span class="font-bold text-sm flex items-center gap-2">
                          <span class="text-lg">{{ getTypeIcon(detail.typeShort) }}</span>
                          {{ getTypeTurkishFull(detail.typeShort) }}
                        </span>
                        <span class="text-xs font-bold px-2 py-1 rounded-md bg-black/10">
                          {{ getDayTurkish(detail.day) }}
                        </span>
                      </div>
                      <div class="flex items-center justify-between text-sm">
                        <span class="flex items-center gap-2 font-semibold">
                          <svg class="w-4 h-4 opacity-70" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                          </svg>
                          {{ detail.startHour }} - {{ detail.endHour }}
                        </span>
                        <span v-if="detail.fullName" class="text-xs flex items-center gap-1.5 font-medium opacity-80 truncate max-w-[180px]" :title="detail.fullName">
                          <svg class="w-3.5 h-3.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                          </svg>
                          {{ detail.fullName }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Oluşturuluyor Durumu (Schedule Tab Only) -->
          <div v-else-if="activeTab === 'schedule' && generating" class="flex-1 flex flex-col items-center justify-center text-center px-6">
            <div class="generating-animation mb-8">
              <!-- Animated Calendar Icon -->
              <div class="calendar-container">
                <div class="calendar-icon">
                  <svg class="w-20 h-20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                  </svg>
                </div>
                <!-- Floating Elements -->
                <div class="floating-element el-1">📚</div>
                <div class="floating-element el-2">🔬</div>
                <div class="floating-element el-3">✏️</div>
                <div class="floating-element el-4">📅</div>
              </div>
              
              <!-- Progress Bar -->
              <div class="progress-container mt-8">
                <div class="progress-bar">
                  <div class="progress-fill"></div>
                </div>
              </div>
            </div>
            
            <h3 class="text-2xl font-bold text-primary mb-2 animate-pulse">{{ $t('generatingSchedule') }}</h3>
            <p class="text-secondary text-base max-w-sm">
              {{ $t('calculatingSchedule') }}
            </p>
            
            <!-- Loading dots -->
            <div class="flex gap-2 mt-6">
              <span class="loading-dot"></span>
              <span class="loading-dot" style="animation-delay: 0.2s"></span>
              <span class="loading-dot" style="animation-delay: 0.4s"></span>
            </div>
          </div>

          <!-- ===== GRADE TAB: Main Content ===== -->
          <div v-else-if="!showProfile && activeTab === 'grade'" class="flex-1 overflow-auto p-4 space-y-4">
            <!-- Grade Stats -->
            <div v-if="gradeCourses.length > 0" class="stats-container animate-slide-down">
              <div class="stats-grid">
                <div class="stat-item stat-cyan">
                  <span class="stat-value">{{ gradeStats.total }}</span>
                  <span class="stat-label">{{ $t('courseCount') }}</span>
                </div>
                <div class="stat-item stat-green">
                  <span class="stat-value">{{ gradeStats.calculated }}</span>
                  <span class="stat-label">{{ $t('calculated') }}</span>
                </div>
                <div class="stat-item stat-red">
                  <span class="stat-value">{{ gradeStats.notCalculated }}</span>
                  <span class="stat-label">{{ $t('notCalculated') }}</span>
                </div>
                <div class="stat-item stat-purple">
                  <span class="stat-value">{{ gradeStats.average }}</span>
                  <span class="stat-label">{{ $t('averageGrade') }}</span>
                </div>
              </div>
            </div>

            <!-- Grade Course Cards -->
            <div v-if="gradeCourses.length > 0" class="space-y-5">
              <div 
                v-for="(course, idx) in gradeCourses" 
                :key="course.id"
                class="grade-card card-bg rounded-2xl overflow-hidden animate-slide-up"
                :style="{ animationDelay: `${idx * 0.08}s` }"
              >
                <!-- Card Header -->
                <div class="grade-card-header p-4">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3 min-w-0">
                      <div class="w-10 h-10 rounded-xl bg-white/15 flex items-center justify-center flex-shrink-0">
                        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z"/>
                        </svg>
                      </div>
                      <div class="min-w-0">
                        <h3 class="font-black text-white text-lg tracking-wide">{{ course.code }}</h3>
                        <p class="text-sm text-white/70 truncate">{{ locale === 'tr' ? course.name : (course.nameEn || course.name) }}</p>
                      </div>
                    </div>
                    <div class="flex items-center gap-2 flex-shrink-0">
                      <!-- Weighted Score -->
                      <div v-if="getCourseWeightedScore(course).filledWeight > 0" class="text-right">
                        <div class="text-2xl font-black text-white leading-none">{{ getCourseWeightedScore(course).score.toFixed(1) }}</div>
                        <div class="text-xs font-bold mt-0.5" :class="getLetterGradeColor(getLetterGrade(getCourseWeightedScore(course).score))">
                          {{ getLetterGrade(getCourseWeightedScore(course).score) }}
                        </div>
                      </div>
                      <div v-else class="text-right">
                        <div class="text-xl font-bold text-white/40">--</div>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Weight Status Bar -->
                  <div class="mt-3">
                    <div class="flex items-center justify-between text-xs mb-1">
                      <span class="text-white/70">{{ $t('totalWeight') }}</span>
                      <span class="font-bold" :class="getCourseWeightTotal(course) === 100 ? 'text-green-300' : 'text-yellow-300'">
                        %{{ getCourseWeightTotal(course) }}
                      </span>
                    </div>
                    <div class="h-1.5 rounded-full bg-white/10 overflow-hidden">
                      <div 
                        class="h-full rounded-full transition-all duration-500"
                        :class="getCourseWeightTotal(course) === 100 ? 'bg-green-400' : getCourseWeightTotal(course) > 100 ? 'bg-red-400' : 'bg-yellow-400'"
                        :style="{ width: `${Math.min(getCourseWeightTotal(course), 100)}%` }"
                      ></div>
                    </div>
                    <div v-if="getCourseWeightTotal(course) !== 100" class="text-[10px] mt-1 text-yellow-300/70">
                      ⚠️ {{ $t('weightWarning') }}
                    </div>
                    <div v-else class="text-[10px] mt-1 text-green-300/70">
                      ✓ {{ $t('weightPerfect') }}
                    </div>
                  </div>
                </div>
                
                <!-- Components Table -->
                <div class="p-4 space-y-2">
                  <!-- Table Header -->
                  <div class="grid grid-cols-12 gap-2 px-2 mb-1">
                    <div class="col-span-5 text-[10px] font-bold text-secondary uppercase tracking-wider">{{ $t('componentName') }}</div>
                    <div class="col-span-3 text-[10px] font-bold text-secondary uppercase tracking-wider text-center">{{ $t('weight') }}</div>
                    <div class="col-span-3 text-[10px] font-bold text-secondary uppercase tracking-wider text-center">{{ $t('score') }}</div>
                    <div class="col-span-1"></div>
                  </div>
                  
                  <!-- Component Rows -->
                  <div 
                    v-for="comp in course.components" 
                    :key="comp.id"
                    class="grade-component-row grid grid-cols-12 gap-2 items-center p-2 rounded-xl"
                  >
                    <div class="col-span-5">
                      <input 
                        v-model="comp.name" 
                        @change="saveGradeCourses"
                        class="w-full px-2 py-1.5 input-bg rounded-lg text-primary text-xs font-semibold outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                        :placeholder="$t('componentName')"
                      />
                    </div>
                    <div class="col-span-3">
                      <div class="relative">
                        <input 
                          v-model.number="comp.weight" 
                          @change="saveGradeCourses"
                          type="number" 
                          min="0" 
                          max="100"
                          class="w-full px-2 py-1.5 input-bg rounded-lg text-primary text-xs font-bold text-center outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                        />
                        <span class="absolute right-1.5 top-1/2 -translate-y-1/2 text-[10px] text-secondary font-bold pointer-events-none">%</span>
                      </div>
                    </div>
                    <div class="col-span-3">
                      <input 
                        v-model.number="comp.score" 
                        @change="saveGradeCourses"
                        type="number" 
                        min="0" 
                        max="100"
                        class="w-full px-2 py-1.5 input-bg rounded-lg text-primary text-xs font-bold text-center outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                        :placeholder="'-'"
                      />
                    </div>
                    <div class="col-span-1 flex justify-center">
                      <button 
                        @click="removeGradeComponent(course.id, comp.id)" 
                        class="p-1 rounded-md hover:bg-red-500/20 text-secondary hover:text-red-400 transition-all"
                        :title="$t('removeComponent')"
                      >
                        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                        </svg>
                      </button>
                    </div>
                  </div>
                  
                  <!-- Add Component Button -->
                  <button 
                    @click="addGradeComponent(course.id)" 
                    class="w-full py-2 mt-2 text-xs font-semibold rounded-xl border-2 border-dashed border-color text-secondary hover:text-indigo-400 hover:border-indigo-500/50 transition-all flex items-center justify-center gap-1.5"
                  >
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                    </svg>
                    {{ $t('addComponent') }}
                  </button>
                  
                  <!-- Course Result Summary -->
                  <div v-if="getCourseWeightedScore(course).filledWeight > 0" class="mt-3 p-3 rounded-xl grade-result-summary">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center gap-2">
                        <span class="text-xs font-bold text-secondary">{{ $t('weightedTotal') }}:</span>
                        <span class="text-lg font-black" :class="getLetterGradeColor(getLetterGrade(getCourseWeightedScore(course).score))">
                          {{ getCourseWeightedScore(course).score.toFixed(2) }}
                        </span>
                      </div>
                      <div class="flex items-center gap-2">
                        <span class="text-xs font-bold text-secondary">{{ $t('letterGrade') }}:</span>
                        <span class="text-xl font-black px-3 py-1 rounded-lg" 
                              :class="getLetterGradeColor(getLetterGrade(getCourseWeightedScore(course).score))"
                              style="background: rgba(255,255,255,0.05)">
                          {{ getLetterGrade(getCourseWeightedScore(course).score) }}
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Remove Course Button -->
                  <button 
                    @click="removeGradeCourse(course.id)" 
                    class="w-full mt-2 py-2 text-xs font-semibold rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500/20 transition-all"
                  >
                    {{ $t('removeCourse') }}
                  </button>
                </div>
              </div>
            </div>

            <!-- Empty State -->
            <div v-else class="min-h-[60vh] flex flex-col items-center justify-center text-center px-6 py-12">
              <div class="w-24 h-24 mb-6 rounded-3xl empty-icon flex items-center justify-center animate-float">
                <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z"/>
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-primary mb-2">{{ $t('noGradeCourses') }}</h3>
              <p class="text-secondary text-base max-w-sm">
                {{ $t('noGradeCoursesMessage') }}
              </p>
              <button 
                @click="mobileMenuOpen = true"
                class="mt-6 px-6 py-3 btn-primary text-white text-sm font-bold rounded-xl lg:hidden hover:shadow-lg transition-all duration-300"
              >
                {{ $t('addCourse') }}
              </button>
            </div>
          </div>

          <!-- Boş Durum (Schedule Tab Only) -->
          <div v-else-if="!showProfile && activeTab === 'schedule'" class="flex-1 min-h-[60vh] flex flex-col items-center justify-center text-center px-6">
            <div class="w-24 h-24 mb-6 rounded-3xl empty-icon flex items-center justify-center animate-float">
              <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-primary mb-2">{{ $t('noScheduleYet') }}</h3>
            <p class="text-secondary text-base max-w-sm">
              {{ $t('noScheduleMessage') }}
            </p>
            <button 
              @click="mobileMenuOpen = true"
              class="mt-6 px-6 py-3 btn-primary text-white text-sm font-bold rounded-xl lg:hidden hover:shadow-lg transition-all duration-300"
            >
              {{ $t('selectCourse') }}
            </button>
          </div>
        </main>
      </div>
    </div>

    <!-- Saving Indicator (Fixed Bottom Right) -->
    <transition name="fade">
      <div v-if="prefsSaving" class="fixed bottom-4 right-4 z-50 flex items-center gap-2 bg-blue-500 text-white px-4 py-2 rounded-lg shadow-lg">
        <svg class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span class="text-sm font-medium">{{ $t('saving') || 'Kaydediliyor...' }}</span>
      </div>
    </transition>

    <!-- Loading Indicator (Full Screen) -->
    <transition name="fade">
      <div v-if="prefsLoading" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
        <div class="card-bg rounded-lg p-6 shadow-xl text-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500 mx-auto"></div>
          <p class="mt-4 text-secondary">{{ $t('loading') || 'Verileriniz yükleniyor...' }}</p>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { courseApi } from './api/courseApi'
import { authAPI, preferencesAPI } from './api/api'
import { useUserPreferences } from './composables/useUserPreferences.js'
import WeeklySchedule from './components/WeeklySchedule.vue'
import Profile from './components/Profile.vue'

// Props & Emits (for AppWrapper integration)
const emit = defineEmits(['logout'])

// Current user
const currentUser = ref(null)

// i18n
const { t, locale } = useI18n()

// Logout handler
const handleLogout = () => {
  if (confirm('Are you sure you want to logout?')) {
    emit('logout')
  }
}

// Tab config update handler (from Profile component)
const handleTabConfigUpdate = (config) => {
  tabOrder.value = config.tabOrder
  hiddenTabs.value = config.hiddenTabs
  defaultTab.value = config.defaultTab
  
  // Save all tab config in a single PATCH request to avoid debounce conflicts
  preferencesAPI.patchPreferences({
    tabOrder: JSON.stringify(config.tabOrder),
    hiddenTabs: JSON.stringify(config.hiddenTabs),
    defaultTab: config.defaultTab,
  }).catch(err => console.error('Failed to save tab config:', err))
  
  // If current active tab got hidden, switch to default
  if (config.hiddenTabs.includes(activeTab.value)) {
    activeTab.value = config.defaultTab
  }
}

// Load current user
onMounted(() => {
  currentUser.value = authAPI.getCurrentUser()
})

// User Preferences (replaces localStorage)
const {
  preferences,
  isLoading: prefsLoading,
  isSaving: prefsSaving,
  loadPreferences,
  updateDepartment,
  updateScheduleMode,
  updateSelectedCourses,
  updateSelectedSections,
  updateScheduleResult,
  updateQuotaWatchList,
  updateActiveTab,
  updateGradeCourses,
  updateTheme,
  updateLanguage,
  updateTabOrder,
  updateHiddenTabs,
  updateDefaultTab,
} = useUserPreferences()

// DEPRECATED: LocalStorage keys (kept for reference, will be removed)
// const STORAGE_KEYS = {
//   DEPARTMENT: 'cs_department',
//   SCHEDULE_MODE: 'cs_scheduleMode',
//   SELECTED_COURSES_AUTO: 'cs_selectedCoursesAuto',
//   SELECTED_SECTIONS: 'cs_selectedSections',
//   SCHEDULE_RESULT: 'cs_scheduleResult',
//   QUOTA_WATCH_LIST: 'cs_quotaWatchList',
//   ACTIVE_TAB: 'cs_activeTab',
//   GRADE_COURSES: 'cs_gradeCourses'
// }

const toggleLanguage = () => {
  locale.value = locale.value === 'tr' ? 'en' : 'tr'
  updateLanguage(locale.value) // Database'e kaydet
}

// Helper function to get localized name
const getLocalizedName = (item) => {
  if (!item) return ''
  if (locale.value === 'en' && item.nameEn) {
    return item.nameEn
  }
  return item.name || item.nameEn || ''
}

// Helper function to get course instructors
const getCourseInstructors = (course) => {
  if (!course || !course.sections) return ''
  const instructors = new Set()
  course.sections.forEach(section => {
    if (section.instructor) {
      instructors.add(section.instructor)
    }
  })
  const arr = [...instructors]
  if (arr.length === 0) return ''
  if (arr.length <= 2) return arr.join(', ')
  return arr.slice(0, 2).join(', ') + ` +${arr.length - 2}`
}

// Theme & Mobile
const isDarkMode = ref(true)
const mobileMenuOpen = ref(false)

// Active Users WebSocket
const activeUserCount = ref(0)
let websocket = null
let wsPingInterval = null
let wsReconnectTimeout = null
let wsReconnectDelay = 3000
const WS_MAX_RECONNECT_DELAY = 30000

const connectWebSocket = () => {
  // Clean up previous connection
  if (websocket) {
    websocket.onclose = null
    websocket.onerror = null
    websocket.close()
  }
  if (wsPingInterval) clearInterval(wsPingInterval)
  if (wsReconnectTimeout) clearTimeout(wsReconnectTimeout)

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${window.location.host}/ws/active-users`
  
  try {
    websocket = new WebSocket(wsUrl)
  } catch (e) {
    scheduleReconnect()
    return
  }
  
  websocket.onopen = () => {
    wsReconnectDelay = 3000 // Reset delay on success
    wsPingInterval = setInterval(() => {
      if (websocket && websocket.readyState === WebSocket.OPEN) {
        websocket.send('ping')
      }
    }, 30000)
  }
  
  websocket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'activeUsers') {
        activeUserCount.value = data.count
      }
    } catch (e) {
      // Ignore pong messages
    }
  }
  
  websocket.onclose = () => {
    if (wsPingInterval) clearInterval(wsPingInterval)
    scheduleReconnect()
  }
  
  websocket.onerror = () => {
    // onclose will fire after onerror, reconnect handled there
  }
}

const scheduleReconnect = () => {
  wsReconnectTimeout = setTimeout(() => {
    connectWebSocket()
  }, wsReconnectDelay)
  wsReconnectDelay = Math.min(wsReconnectDelay * 1.5, WS_MAX_RECONNECT_DELAY)
}

const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  updateTheme(isDarkMode.value ? 'dark' : 'light') // Database'e kaydet
}

// State
const seasons = ref([])
const selectedSeason = ref('')
const departments = ref([])
const departmentId = ref('all')
const departmentSearch = ref('')
const availableCourses = ref([])
const selectedSections = ref([]) // For MANUAL mode: [{code, section}]
const selectedCoursesAuto = ref([]) // For AUTO mode: [courseCode, ...]
const expandedCourses = ref([])
const courseSearch = ref('')
const scheduleResult = ref(null)
const error = ref('')

// Schedule Mode: 'AUTO' or 'MANUAL'
const scheduleMode = ref('AUTO')

// Active Tab: 'schedule' or 'quota'
const activeTab = ref('schedule')

// Tab customization
const tabOrder = ref(['schedule', 'quota', 'grade'])
const hiddenTabs = ref([])
const defaultTab = ref('schedule')

// Profile view
const showProfile = ref(false)

// All tab definitions for dynamic rendering
const allTabDefs = {
  schedule: { id: 'schedule', labelKey: 'scheduleTab', icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z' },
  quota: { id: 'quota', labelKey: 'quotaTab', icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' },
  grade: { id: 'grade', labelKey: 'gradeTab', icon: 'M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z' },
}

// Computed: visible tabs in user-defined order
const visibleTabs = computed(() => {
  return tabOrder.value
    .filter(id => !hiddenTabs.value.includes(id))
    .map(id => allTabDefs[id])
    .filter(Boolean)
})

// Quota Watch List State
const quotaWatchList = ref([])
const quotaLoading = ref(false)
const lastQuotaCheck = ref(null)
const quotaCourseSearch = ref('')
const quotaExpandedCourses = ref([])

// Grade Calculator State
const gradeCourses = ref([])
const gradeCourseSearch = ref('')
const gradeExpandedCourses = ref([])

// Loading states
const loadingSeasons = ref(false)
const loadingDepartments = ref(false)
const loadingCourses = ref(false)
const generating = ref(false)

// Save state to localStorage
// DEPRECATED: saveState - now handled by auto-save in useUserPreferences
// const saveState = () => {
//   // Auto-saved via watch handlers
// }

// DEPRECATED: loadSavedState - now handled by loadPreferences in onMounted
// const loadSavedState = () => {
//   // Loaded from database in onMounted
// }

// Computed
const filteredDepartments = computed(() => {
  if (!departmentSearch.value) return departments.value
  const search = departmentSearch.value.toLowerCase()
  return departments.value.filter(d => 
    d.name.toLowerCase().includes(search) || 
    d.nameEn?.toLowerCase().includes(search)
  )
})

const activeSeason = computed(() => {
  return seasons.value.find(s => s.active === 1)
})

const groupedCourses = computed(() => {
  const courseMap = new Map()
  
  availableCourses.value.forEach(course => {
    if (!courseMap.has(course.code)) {
      courseMap.set(course.code, {
        code: course.code,
        name: course.name || course.nameEn,
        nameEn: course.nameEn,
        credit: course.credit,
        ects: course.ects,
        departmentId: course.departmentId,
        sections: []
      })
    }
    
    const instructor = course.details && course.details.length > 0 ? course.details[0].fullName : null
    
    courseMap.get(course.code).sections.push({
      section: course.section,
      instructor: instructor,
      details: course.details || [],
      fullQuota: course.fullQuota,
      quota: course.quota,
      departmentId: course.departmentId
    })
  })
  
  courseMap.forEach(course => {
    course.sections.sort((a, b) => a.section - b.section)
  })
  
  return Array.from(courseMap.values()).sort((a, b) => a.code.localeCompare(b.code))
})

const filteredCourses = computed(() => {
  if (!courseSearch.value) return groupedCourses.value
  const search = courseSearch.value.toLowerCase().replace(/\s+/g, '')
  const searchLower = courseSearch.value.toLowerCase()
  
  return groupedCourses.value.filter(c => {
    const codeNoSpace = c.code.toLowerCase().replace(/\s+/g, '')
    
    // Ders kodu veya adıyla eşleşme
    const matchesCode = codeNoSpace.includes(search) || 
                        c.code.toLowerCase().includes(searchLower)
    const matchesName = c.name?.toLowerCase().includes(searchLower)
    
    // Hoca adıyla eşleşme - tüm section'lardaki hocaları kontrol et
    const matchesInstructor = c.sections.some(section => 
      section.instructor?.toLowerCase().includes(searchLower)
    )
    
    return matchesCode || matchesName || matchesInstructor
  })
})

// Quota tab filtered courses
const quotaFilteredCourses = computed(() => {
  if (!quotaCourseSearch.value) return groupedCourses.value
  const search = quotaCourseSearch.value.toLowerCase().replace(/\s+/g, '')
  const searchLower = quotaCourseSearch.value.toLowerCase()
  
  return groupedCourses.value.filter(c => {
    const codeNoSpace = c.code.toLowerCase().replace(/\s+/g, '')
    const matchesCode = codeNoSpace.includes(search) || c.code.toLowerCase().includes(searchLower)
    const matchesName = c.name?.toLowerCase().includes(searchLower) || c.nameEn?.toLowerCase().includes(searchLower)
    const matchesInstructor = c.sections.some(section => 
      section.instructor?.toLowerCase().includes(searchLower)
    )
    return matchesCode || matchesName || matchesInstructor
  })
})

// Check if course section is already in watch list
const isInWatchList = (code, section) => {
  return quotaWatchList.value.some(item => item.code === code && item.section === section)
}

// Check if course has any watched sections
const hasWatchedSections = (code) => {
  return quotaWatchList.value.some(item => item.code === code)
}

// Get quota info for a watched section
const getWatchedQuota = (code, section) => {
  return quotaWatchList.value.find(item => item.code === code && item.section === section)
}

// Toggle course expansion in quota tab
const toggleQuotaCourseExpand = (code) => {
  const idx = quotaExpandedCourses.value.indexOf(code)
  if (idx === -1) {
    quotaExpandedCourses.value.push(code)
  } else {
    quotaExpandedCourses.value.splice(idx, 1)
  }
}

// Toggle section in watch list
const toggleWatchSection = (course, section) => {
  const idx = quotaWatchList.value.findIndex(
    item => item.code === course.code && item.section === section.section
  )
  
  if (idx === -1) {
    // Get the departmentId - prefer from section, then from course, then from current selection
    const deptId = section.departmentId || course.departmentId || (departmentId.value !== 'all' ? departmentId.value : null)
    
    // Use quota data from section if available (API: quota = total capacity, fullQuota = enrolled)
    const cap = section.quota != null ? section.quota : null
    const enr = section.fullQuota != null ? section.fullQuota : null
    const rem = (cap != null && enr != null) ? cap - enr : null
    
    // Add to watch list
    quotaWatchList.value.push({
      code: course.code,
      section: section.section,
      nameEn: course.nameEn,
      nameTr: course.name,
      departmentId: deptId,
      instructor: section.instructor,
      capacity: cap,
      enrolled: enr,
      remaining: rem,
      loading: false
    })
  } else {
    // Remove from watch list
    quotaWatchList.value.splice(idx, 1)
  }
  saveQuotaWatchList()
}

// Clear all from watch list
const clearWatchList = () => {
  quotaWatchList.value = []
  saveQuotaWatchList()
}

const canGenerate = computed(() => {
  if (!selectedSeason.value || !departmentId.value) return false
  if (scheduleMode.value === 'AUTO') {
    return selectedCoursesAuto.value.length > 0
  } else {
    return selectedSections.value.length > 0
  }
})

// Helpers
const parseTime = (timeStr) => {
  if (!timeStr) return 0
  const parts = timeStr.split(':')
  return parseInt(parts[0]) * 60 + parseInt(parts[1] || 0)
}

const getDayTurkish = (day) => t(`days.${day}`) || day
const getDayShort = (day) => t(`daysShort.${day}`) || day

// Type helpers for detailed view - flexible matching
const getTypeTurkishFull = (type) => {
  const typeStr = (type || '').toUpperCase()
  if (typeStr.includes('LEC') || typeStr.includes('LECTURE') || typeStr.includes('TEO') || typeStr.includes('THEORY')) return t('types.lecture')
  if (typeStr.includes('LAB') || typeStr.includes('LABOR')) return t('types.lab')
  if (typeStr.includes('PS') || typeStr.includes('PROBLEM') || typeStr.includes('RECIT')) return t('types.ps')
  return type || t('types.other')
}

const getTypeIcon = (type) => {
  const typeStr = (type || '').toUpperCase()
  if (typeStr.includes('LEC') || typeStr.includes('LECTURE') || typeStr.includes('TEO') || typeStr.includes('THEORY')) return '📚'
  if (typeStr.includes('LAB') || typeStr.includes('LABOR')) return '🔬'
  if (typeStr.includes('PS') || typeStr.includes('PROBLEM') || typeStr.includes('RECIT')) return '✏️'
  return '📋'
}

const getDetailTypeClass = (type) => {
  const typeStr = (type || '').toUpperCase()
  if (typeStr.includes('LEC') || typeStr.includes('LECTURE') || typeStr.includes('TEO') || typeStr.includes('THEORY')) return 'type-lecture'
  if (typeStr.includes('LAB') || typeStr.includes('LABOR')) return 'type-lab'
  if (typeStr.includes('PS') || typeStr.includes('PROBLEM') || typeStr.includes('RECIT')) return 'type-ps'
  return 'type-other'
}

const formatInstructorShort = (name) => {
  if (!name) return ''
  const parts = name.split(' ')
  if (parts.length > 2) {
    return parts[0].charAt(0) + '. ' + parts[parts.length - 1]
  }
  if (parts.length === 2) {
    return parts[0].charAt(0) + '. ' + parts[1]
  }
  return name
}

// Conflicts
const conflicts = computed(() => {
  const result = []
  const selectedDetails = []
  
  selectedSections.value.forEach(sel => {
    const course = groupedCourses.value.find(c => c.code === sel.code)
    if (course) {
      const section = course.sections.find(s => s.section === sel.section)
      if (section && section.details) {
        section.details.forEach(detail => {
          selectedDetails.push({
            code: sel.code,
            section: sel.section,
            day: detail.day,
            startHour: detail.startHour,
            endHour: detail.endHour
          })
        })
      }
    }
  })
  
  for (let i = 0; i < selectedDetails.length; i++) {
    for (let j = i + 1; j < selectedDetails.length; j++) {
      const d1 = selectedDetails[i]
      const d2 = selectedDetails[j]
      
      if (d1.code !== d2.code && d1.day === d2.day) {
        const start1 = parseTime(d1.startHour)
        const end1 = parseTime(d1.endHour)
        const start2 = parseTime(d2.startHour)
        const end2 = parseTime(d2.endHour)
        
        if (start1 < end2 && start2 < end1) {
          const conflictKey = [d1.code, d2.code].sort().join('-') + '-' + d1.day
          if (!result.find(r => r.key === conflictKey)) {
            result.push({
              key: conflictKey,
              course1: `${d1.code}`,
              course2: `${d2.code}`,
              day: dayNames[d1.day] || d1.day
            })
          }
        }
      }
    }
  }
  
  return result
})

const isInConflict = (code) => {
  return conflicts.value.some(c => c.course1 === code || c.course2 === code)
}

// Methods
const loadSeasons = async () => {
  loadingSeasons.value = true
  try {
    seasons.value = await courseApi.getSeasons()
    const active = seasons.value.find(s => s.active === 1)
    if (active) selectedSeason.value = active.id
  } catch (e) {
    error.value = t('errorLoadingSeasons', { message: e.message })
  } finally {
    loadingSeasons.value = false
  }
}

const loadDepartments = async () => {
  loadingDepartments.value = true
  try {
    departments.value = await courseApi.getDepartments()
    departments.value.sort((a, b) => a.name.localeCompare(b.name, 'tr'))
  } catch (e) {
    error.value = t('errorLoadingDepartments', { message: e.message })
  } finally {
    loadingDepartments.value = false
  }
}

const onDepartmentChange = async () => {
  selectedSections.value = []
  selectedCoursesAuto.value = []
  expandedCourses.value = []
  scheduleResult.value = null
  // Automatically load courses when department changes
  await loadCourses()
}

const loadCourses = async () => {
  if (!selectedSeason.value || !departmentId.value) return
  loadingCourses.value = true
  error.value = ''
  try {
    availableCourses.value = await courseApi.getCourses(selectedSeason.value, departmentId.value)
  } catch (e) {
    error.value = t('errorLoadingCourses', { message: e.message })
  } finally {
    loadingCourses.value = false
  }
}

const toggleCourseExpand = (code) => {
  const idx = expandedCourses.value.indexOf(code)
  if (idx === -1) expandedCourses.value.push(code)
  else expandedCourses.value.splice(idx, 1)
}

const isSelected = (code, section) => {
  return selectedSections.value.some(s => s.code === code && s.section === section)
}

const toggleSection = (code, section) => {
  const idx = selectedSections.value.findIndex(s => s.code === code && s.section === section)
  if (idx === -1) {
    selectedSections.value = selectedSections.value.filter(s => s.code !== code)
    selectedSections.value.push({ code, section })
  } else {
    selectedSections.value.splice(idx, 1)
  }
  autoGenerateSchedule()
}

const removeSection = (code, section) => {
  const idx = selectedSections.value.findIndex(s => s.code === code && s.section === section)
  if (idx !== -1) selectedSections.value.splice(idx, 1)
  autoGenerateSchedule()
}

const clearAllSections = () => {
  selectedSections.value = []
  scheduleResult.value = null
}

const clearAllCoursesAuto = () => {
  selectedCoursesAuto.value = []
  scheduleResult.value = null
}

// AUTO mode functions
const isCourseSelectedAuto = (code) => {
  return selectedCoursesAuto.value.includes(code)
}

const toggleCourseAuto = (code) => {
  const idx = selectedCoursesAuto.value.indexOf(code)
  if (idx === -1) {
    selectedCoursesAuto.value.push(code)
  } else {
    selectedCoursesAuto.value.splice(idx, 1)
  }
  autoGenerateSchedule()
}

const removeCourseAuto = (code) => {
  const idx = selectedCoursesAuto.value.indexOf(code)
  if (idx !== -1) selectedCoursesAuto.value.splice(idx, 1)
  autoGenerateSchedule()
}

const generateSchedule = async () => {
  if (!canGenerate.value) return
  generating.value = true
  error.value = ''
  
  try {
    const deptId = departmentId.value === 'all' ? 0 : Number(departmentId.value)
    
    let requestData = {
      seasonId: selectedSeason.value,
      departmentId: deptId,
      mode: scheduleMode.value
    }
    
    if (scheduleMode.value === 'AUTO') {
      // Auto mode: send course codes, backend finds best sections
      requestData.courseCodes = [...selectedCoursesAuto.value]
    } else {
      // Manual mode: send specific sections
      requestData.courseCodes = [...new Set(selectedSections.value.map(s => s.code))]
      requestData.selectedSections = selectedSections.value.map(s => ({
        code: s.code,
        section: String(s.section)
      }))
    }
    
    const result = await courseApi.generateScheduleAdvanced(requestData)
    
    if (result.success) scheduleResult.value = result
    else error.value = result.message
  } catch (e) {
    error.value = t('errorGenerating', { message: e.message })
  } finally {
    generating.value = false
  }
}

// Auto-generate schedule when courses change (debounced)
let autoGenerateTimer = null
const autoGenerateSchedule = () => {
  if (autoGenerateTimer) clearTimeout(autoGenerateTimer)
  autoGenerateTimer = setTimeout(() => {
    if (canGenerate.value && activeTab.value === 'schedule') {
      generateSchedule()
    }
  }, 800)
}

// ===== QUOTA WATCH LIST FUNCTIONS =====

// Save quota watch list to localStorage
const saveQuotaWatchList = () => {
  // No-op: auto-saved via watch handler
}

// DEPRECATED: loadQuotaWatchList - now handled by loadPreferences
// const loadQuotaWatchList = () => {
//   // Loaded from database in onMounted
// }

// Remove from watch list
const removeFromWatchList = (index) => {
  quotaWatchList.value.splice(index, 1)
  // saveQuotaWatchList() - auto-saved via watch
}

// Refresh quotas from API
const refreshQuotas = async () => {
  if (quotaWatchList.value.length === 0) return
  
  quotaLoading.value = true
  
  // Mark all items as loading
  quotaWatchList.value.forEach(item => {
    item.loading = true
  })
  
  try {
    const coursesToCheck = quotaWatchList.value
      .filter(item => item.departmentId && item.departmentId !== 'all')
      .map(item => ({
        code: item.code,
        section: item.section,
        departmentId: parseInt(item.departmentId)
      }))
    
    // If no courses to check (all have invalid departmentId), skip API call
    if (coursesToCheck.length === 0) {
      console.warn('No courses with valid departmentId to check')
      quotaWatchList.value.forEach(item => {
        item.loading = false
      })
      quotaLoading.value = false
      return
    }
    
    const result = await courseApi.checkQuotas(selectedSeason.value, coursesToCheck)
    
    if (result.success && result.quotas) {
      // Update watch list with fresh data
      result.quotas.forEach(quota => {
        const item = quotaWatchList.value.find(
          i => i.code === quota.code && i.section === quota.section
        )
        if (item) {
          item.capacity = quota.capacity
          item.enrolled = quota.enrolled
          item.remaining = quota.remaining
          item.instructor = quota.instructor || item.instructor
          item.loading = false
        }
      })
    }
    
    lastQuotaCheck.value = Date.now()
    saveQuotaWatchList()
  } catch (e) {
    console.error('Failed to refresh quotas:', e)
    error.value = t('errorCheckingQuotas', { message: e.message })
  } finally {
    quotaLoading.value = false
    quotaWatchList.value.forEach(item => {
      item.loading = false
    })
  }
}

// Format last check time
const formatLastCheck = (timestamp) => {
  if (!timestamp) return ''
  const diff = Math.floor((Date.now() - timestamp) / 1000)
  if (diff < 60) return t('justNow')
  if (diff < 3600) return t('minutesAgo', { count: Math.floor(diff / 60) })
  return new Date(timestamp).toLocaleTimeString(locale.value === 'tr' ? 'tr-TR' : 'en-US', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ===== GRADE CALCULATOR FUNCTIONS =====

let gradeIdCounter = 1

const saveGradeCourses = () => {
  // No-op: auto-saved via watch handler
}

// DEPRECATED: loadGradeCourses - now handled by loadPreferences
const loadGradeCourses = () => {
  // This function is kept for gradeIdCounter initialization
  // Actual data is loaded from database in onMounted
  if (gradeCourses.value && gradeCourses.value.length > 0) {
    gradeCourses.value.forEach(c => {
      if (c.id >= gradeIdCounter) gradeIdCounter = c.id + 1
      if (c.components) {
        c.components.forEach(comp => {
          if (comp.id >= gradeIdCounter) gradeIdCounter = comp.id + 1
        })
      }
    })
  }
}

// Grade tab filtered courses
const gradeFilteredCourses = computed(() => {
  if (!gradeCourseSearch.value) return groupedCourses.value
  const search = gradeCourseSearch.value.toLowerCase().replace(/\s+/g, '')
  const searchLower = gradeCourseSearch.value.toLowerCase()
  
  return groupedCourses.value.filter(c => {
    const codeNoSpace = c.code.toLowerCase().replace(/\s+/g, '')
    const matchesCode = codeNoSpace.includes(search) || c.code.toLowerCase().includes(searchLower)
    const matchesName = c.name?.toLowerCase().includes(searchLower) || c.nameEn?.toLowerCase().includes(searchLower)
    return matchesCode || matchesName
  })
})

const isGradeCourseSelected = (code) => {
  return gradeCourses.value.some(c => c.code === code)
}

const getGradeCourseScore = (code) => {
  const course = gradeCourses.value.find(c => c.code === code)
  if (!course) return null
  const { score, filledWeight } = getCourseWeightedScore(course)
  return filledWeight > 0 ? score : null
}

const toggleGradeCourseExpand = (code) => {
  const idx = gradeExpandedCourses.value.indexOf(code)
  if (idx === -1) {
    gradeExpandedCourses.value.push(code)
  } else {
    gradeExpandedCourses.value.splice(idx, 1)
  }
}

const toggleGradeCourse = (course) => {
  const idx = gradeCourses.value.findIndex(c => c.code === course.code)
  if (idx === -1) {
    // Add course with default components
    gradeCourses.value.push({
      id: gradeIdCounter++,
      code: course.code,
      name: course.name,
      nameEn: course.nameEn,
      credit: course.credit,
      ects: course.ects,
      components: [
        { id: gradeIdCounter++, name: t('defaultComponents.midterm'), weight: 30, score: null },
        { id: gradeIdCounter++, name: t('defaultComponents.final'), weight: 40, score: null },
        { id: gradeIdCounter++, name: t('defaultComponents.homework'), weight: 30, score: null }
      ]
    })
  } else {
    gradeCourses.value.splice(idx, 1)
  }
  saveGradeCourses()
}

const removeGradeCourse = (courseId) => {
  gradeCourses.value = gradeCourses.value.filter(c => c.id !== courseId)
  saveGradeCourses()
}

const addGradeComponent = (courseId) => {
  const course = gradeCourses.value.find(c => c.id === courseId)
  if (course) {
    course.components.push({
      id: gradeIdCounter++,
      name: '',
      weight: 0,
      score: null
    })
    saveGradeCourses()
  }
}

const removeGradeComponent = (courseId, componentId) => {
  const course = gradeCourses.value.find(c => c.id === courseId)
  if (course) {
    course.components = course.components.filter(c => c.id !== componentId)
    saveGradeCourses()
  }
}

const clearAllGrades = () => {
  gradeCourses.value = []
  saveGradeCourses()
}

const getCourseWeightTotal = (course) => {
  return course.components.reduce((sum, c) => sum + (parseFloat(c.weight) || 0), 0)
}

const getCourseWeightedScore = (course) => {
  let totalWeightedScore = 0
  let totalWeight = 0
  
  course.components.forEach(comp => {
    const w = parseFloat(comp.weight) || 0
    const s = parseFloat(comp.score)
    if (!isNaN(s) && s !== null && comp.score !== null && comp.score !== '') {
      totalWeightedScore += (s * w) / 100
      totalWeight += w
    }
  })
  
  return { score: totalWeightedScore, filledWeight: totalWeight }
}

const getLetterGrade = (score) => {
  if (score === null || score === undefined || isNaN(score)) return '-'
  if (score >= 90) return 'AA'
  if (score >= 85) return 'BA'
  if (score >= 80) return 'BB'
  if (score >= 75) return 'CB'
  if (score >= 70) return 'CC'
  if (score >= 65) return 'DC'
  if (score >= 60) return 'DD'
  if (score >= 50) return 'FD'
  return 'FF'
}

const getLetterGradeColor = (letter) => {
  if (letter === 'AA' || letter === 'BA') return 'text-green-400'
  if (letter === 'BB' || letter === 'CB') return 'text-emerald-400'
  if (letter === 'CC' || letter === 'DC') return 'text-yellow-400'
  if (letter === 'DD' || letter === 'FD') return 'text-orange-400'
  if (letter === 'FF') return 'text-red-400'
  return 'text-secondary'
}

const gradeStats = computed(() => {
  const total = gradeCourses.value.length
  let calculatedCount = 0
  let totalScore = 0
  
  gradeCourses.value.forEach(course => {
    const { score, filledWeight } = getCourseWeightedScore(course)
    if (filledWeight > 0) {
      calculatedCount++
      totalScore += score
    }
  })
  
  return {
    total,
    calculated: calculatedCount,
    notCalculated: total - calculatedCount,
    average: calculatedCount > 0 ? (totalScore / calculatedCount).toFixed(1) : '-'
  }
})

// Watchers for auto-save
// Watch handlers - auto-save to database
watch(departmentId, (newValue) => updateDepartment(newValue))
watch(scheduleMode, (newValue) => updateScheduleMode(newValue))
watch(selectedCoursesAuto, (newValue) => updateSelectedCourses(newValue), { deep: true })
watch(selectedSections, (newValue) => updateSelectedSections(newValue), { deep: true })
watch(scheduleResult, (newValue) => updateScheduleResult(newValue), { deep: true })
watch(activeTab, (newValue) => updateActiveTab(newValue))
watch(quotaWatchList, (newValue) => updateQuotaWatchList(newValue), { deep: true })
watch(gradeCourses, (newValue) => updateGradeCourses(newValue), { deep: true })

// Lifecycle
onMounted(async () => {
  // Load user preferences from database
  const prefs = await loadPreferences()
  
  // Apply loaded preferences
  if (prefs) {
    // departmentId always defaults to 'all' so all courses load automatically
    if (prefs.scheduleMode) scheduleMode.value = prefs.scheduleMode
    if (prefs.selectedCoursesAuto) selectedCoursesAuto.value = prefs.selectedCoursesAuto
    if (prefs.selectedSections) selectedSections.value = prefs.selectedSections
    if (prefs.scheduleResult) scheduleResult.value = prefs.scheduleResult
    if (prefs.quotaWatchList) quotaWatchList.value = prefs.quotaWatchList
    if (prefs.activeTab) activeTab.value = prefs.activeTab
    if (prefs.gradeCourses) {
      gradeCourses.value = prefs.gradeCourses
      loadGradeCourses() // Initialize gradeIdCounter
    }
    if (prefs.theme) isDarkMode.value = (prefs.theme === 'dark')
    if (prefs.language) locale.value = prefs.language
    
    // Tab customization
    if (prefs.tabOrder && Array.isArray(prefs.tabOrder) && prefs.tabOrder.length > 0) {
      tabOrder.value = prefs.tabOrder
    }
    if (prefs.hiddenTabs && Array.isArray(prefs.hiddenTabs)) {
      hiddenTabs.value = prefs.hiddenTabs
    }
    if (prefs.defaultTab) {
      defaultTab.value = prefs.defaultTab
      // Always open the default tab on page load
      activeTab.value = prefs.defaultTab
    }
  }
  
  await loadSeasons()
  await loadDepartments()
  
  // Load courses for the saved or default department
  await loadCourses()
  
  connectWebSocket()
  
  // Auto-generate schedule if courses are selected but no result exists
  const hasSelectedCourses = (selectedCoursesAuto.value && selectedCoursesAuto.value.length > 0) || 
                             (selectedSections.value && selectedSections.value.length > 0)
  if (hasSelectedCourses && !scheduleResult.value) {
    generateSchedule()
  }
  
  if (hasSelectedCourses) {
    console.log('✅ Previous session restored from database')
  }
  
  // Auto-check quotas if watch list has items
  if (quotaWatchList.value && quotaWatchList.value.length > 0) {
    refreshQuotas()
  }
})

onUnmounted(() => {
  if (autoGenerateTimer) clearTimeout(autoGenerateTimer)
  if (wsPingInterval) clearInterval(wsPingInterval)
  if (wsReconnectTimeout) clearTimeout(wsReconnectTimeout)
  if (websocket) {
    websocket.onclose = null
    websocket.close()
  }
})
</script>

<style scoped>
/* ===== DARK THEME ===== */
.dark-theme {
  --bg-primary: #0c1222;
  --bg-secondary: #151d30;
  --bg-tertiary: #1e293b;
  --bg-elevated: #243044;
  --text-primary: #f1f5f9;
  --text-secondary: #94a3b8;
  --text-tertiary: #64748b;
  --border-color: rgba(148, 163, 184, 0.12);
  --border-subtle: rgba(148, 163, 184, 0.06);

  --accent: #818cf8;
  --accent-muted: rgba(99, 102, 241, 0.15);

  --glass-bg: rgba(21, 29, 48, 0.8);
  --glass-border: rgba(148, 163, 184, 0.1);
  --glass-blur: blur(16px);

  --glow-primary: radial-gradient(600px circle at var(--mouse-x, 50%) var(--mouse-y, 30%), rgba(99, 102, 241, 0.06), transparent 40%);

  --season-bg: linear-gradient(135deg, #6366f1, #8b5cf6);
  --season-text: #ffffff;
  --badge-bg: rgba(99, 102, 241, 0.2);
  --badge-text: #a5b4fc;
  
  --btn-primary: linear-gradient(135deg, #6366f1, #8b5cf6);
  --btn-primary-hover: linear-gradient(135deg, #5558e6, #7c4fea);
  --btn-success: linear-gradient(135deg, #10b981, #14b8a6);
  
  --stat-cyan: linear-gradient(135deg, #06b6d4, #0891b2);
  --stat-blue: linear-gradient(135deg, #3b82f6, #2563eb);
  --stat-purple: linear-gradient(135deg, #8b5cf6, #7c3aed);
  --stat-green: linear-gradient(135deg, #10b981, #059669);
  --stat-orange: linear-gradient(135deg, #f97316, #ea580c);
  --stat-red: linear-gradient(135deg, #f43f5e, #e11d48);
  
  --tag-selected: linear-gradient(135deg, #6366f1, #8b5cf6);
  --tag-conflict: linear-gradient(135deg, #f43f5e, #e11d48);
  --conflict-bg: rgba(244, 63, 94, 0.12);
  --conflict-border: rgba(244, 63, 94, 0.3);
  --conflict-text: #fda4af;
  
  --error-bg: rgba(244, 63, 94, 0.12);
  --error-border: rgba(244, 63, 94, 0.3);
  --error-text: #fda4af;

  --success-bg: rgba(16, 185, 129, 0.12);
  --success-border: rgba(16, 185, 129, 0.3);
  --success-text: #6ee7b7;
  
  --empty-icon-bg: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(139, 92, 246, 0.15));
  --empty-icon-color: #818cf8;
  
  --course-item-hover: rgba(99, 102, 241, 0.08);
  --section-selected: rgba(99, 102, 241, 0.12);
  --checkbox-border: #475569;
  --checkbox-checked: linear-gradient(135deg, #6366f1, #8b5cf6);

  --shadow-sm: 0 1px 3px rgba(0,0,0,0.3), 0 1px 2px rgba(0,0,0,0.2);
  --shadow-md: 0 4px 16px rgba(0,0,0,0.35);
  --shadow-lg: 0 12px 32px rgba(0,0,0,0.4);
  --shadow-glow: 0 0 20px rgba(99, 102, 241, 0.15);
  --shadow-card: 0 2px 8px rgba(0,0,0,0.2), 0 0 1px rgba(148,163,184,0.1);
}

/* ===== LIGHT THEME ===== */
.light-theme {
  --bg-primary: #f8fafc;
  --bg-secondary: #ffffff;
  --bg-tertiary: #f1f5f9;
  --bg-elevated: #ffffff;
  --text-primary: #0f172a;
  --text-secondary: #64748b;
  --text-tertiary: #94a3b8;
  --border-color: rgba(15, 23, 42, 0.08);
  --border-subtle: rgba(15, 23, 42, 0.04);

  --accent: #6366f1;
  --accent-muted: rgba(99, 102, 241, 0.08);

  --glass-bg: rgba(255, 255, 255, 0.75);
  --glass-border: rgba(15, 23, 42, 0.06);
  --glass-blur: blur(16px);

  --glow-primary: radial-gradient(600px circle at var(--mouse-x, 50%) var(--mouse-y, 30%), rgba(99, 102, 241, 0.04), transparent 40%);

  --season-bg: linear-gradient(135deg, #6366f1, #8b5cf6);
  --season-text: #ffffff;
  --badge-bg: rgba(99, 102, 241, 0.1);
  --badge-text: #6366f1;
  
  --btn-primary: linear-gradient(135deg, #6366f1, #8b5cf6);
  --btn-primary-hover: linear-gradient(135deg, #5558e6, #7c4fea);
  --btn-success: linear-gradient(135deg, #10b981, #14b8a6);
  
  --stat-cyan: linear-gradient(135deg, #06b6d4, #0e7490);
  --stat-blue: linear-gradient(135deg, #3b82f6, #1d4ed8);
  --stat-purple: linear-gradient(135deg, #8b5cf6, #6d28d9);
  --stat-green: linear-gradient(135deg, #10b981, #047857);
  --stat-orange: linear-gradient(135deg, #f97316, #c2410c);
  --stat-red: linear-gradient(135deg, #f43f5e, #e11d48);
  
  --tag-selected: linear-gradient(135deg, #6366f1, #8b5cf6);
  --tag-conflict: linear-gradient(135deg, #f43f5e, #e11d48);
  --conflict-bg: #fff1f2;
  --conflict-border: #fecdd3;
  --conflict-text: #be123c;
  
  --error-bg: #fff1f2;
  --error-border: #fecdd3;
  --error-text: #be123c;

  --success-bg: #ecfdf5;
  --success-border: #a7f3d0;
  --success-text: #047857;
  
  --empty-icon-bg: linear-gradient(135deg, #e0e7ff, #ede9fe);
  --empty-icon-color: #6366f1;
  
  --course-item-hover: rgba(99, 102, 241, 0.06);
  --section-selected: rgba(99, 102, 241, 0.08);
  --checkbox-border: #cbd5e1;
  --checkbox-checked: linear-gradient(135deg, #6366f1, #8b5cf6);

  --shadow-sm: 0 1px 3px rgba(15,23,42,0.04), 0 1px 2px rgba(15,23,42,0.06);
  --shadow-md: 0 4px 16px rgba(15,23,42,0.06), 0 1px 3px rgba(15,23,42,0.04);
  --shadow-lg: 0 12px 32px rgba(15,23,42,0.08), 0 4px 8px rgba(15,23,42,0.04);
  --shadow-glow: 0 0 20px rgba(99, 102, 241, 0.08);
  --shadow-card: 0 1px 3px rgba(15,23,42,0.04), 0 0 0 1px rgba(15,23,42,0.04);
}

/* ===== BASE STYLES ===== */
.app-bg { 
  background-color: var(--bg-primary);
  background-image: var(--glow-primary);
  min-height: 100vh;
}
.sidebar-bg { 
  background-color: var(--bg-secondary); 
  border-right: 1px solid var(--border-color);
}
.header-bg { 
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border-bottom: 1px solid var(--glass-border);
}
.sticky-header { background-color: var(--bg-secondary); }
.card-bg { 
  background-color: var(--bg-secondary); 
  border: 1px solid var(--border-color); 
  box-shadow: var(--shadow-card);
  border-radius: 16px;
}
.input-bg { 
  background-color: var(--bg-tertiary); 
  border: 1px solid var(--border-color); 
  border-radius: 12px;
  transition: all var(--duration-normal, 200ms) var(--ease-out, ease);
}
.input-bg:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-muted);
}
.text-primary { color: var(--text-primary); }
.text-secondary { color: var(--text-secondary); }
.text-tertiary { color: var(--text-tertiary); }
.border-color { border-color: var(--border-color); }
.placeholder-secondary::placeholder { color: var(--text-tertiary); }

.toggle-btn { 
  background-color: var(--bg-tertiary); 
  border: 1px solid var(--border-color);
  transition: all var(--duration-normal, 200ms) var(--ease-out, ease);
}
.toggle-btn:hover {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
  transform: translateY(-1px);
}

.user-info-pill {
  border-color: var(--border-color);
  background: var(--bg-tertiary);
  transition: all var(--duration-normal, 200ms) var(--ease-out, ease);
}
.user-info-pill:hover {
  border-color: var(--accent);
  background: var(--accent-muted);
}

.logout-btn {
  color: #f43f5e;
  transition: all var(--duration-fast, 120ms) var(--ease-out, ease);
}
.logout-btn:hover {
  background: rgba(244, 63, 94, 0.1);
}

/* Active Users Badge */
.active-users-badge {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
  border: 1px solid rgba(34, 197, 94, 0.3);
}

.dark-theme .active-users-badge {
  background: rgba(34, 197, 94, 0.15);
  color: #4ade80;
  border: 1px solid rgba(34, 197, 94, 0.25);
}

.light-theme .active-users-badge {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border: 1px solid rgba(34, 197, 94, 0.3);
}

/* Season Badge */
.season-badge {
  background: var(--season-bg);
  color: var(--season-text);
}

.season-card {
  background: var(--season-bg);
  color: var(--season-text);
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
  border-radius: 12px;
}

/* Buttons */
.btn-primary {
  background: var(--btn-primary);
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.35);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 10px;
}
.btn-primary:hover {
  background: var(--btn-primary-hover);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.4);
}
.btn-primary:active {
  transform: scale(0.98);
}

.btn-success {
  background: var(--btn-success);
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.35);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 10px;
}
.btn-success:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4);
}
.btn-success:active {
  transform: scale(0.98);
}

/* Warning Cards */
.warning-card {
  padding: 14px;
  border-radius: 14px;
  border: 1px solid;
}

.warning-yellow {
  background: linear-gradient(135deg, rgba(234, 179, 8, 0.1), rgba(245, 158, 11, 0.1));
  border-color: rgba(234, 179, 8, 0.3);
}

.warning-orange {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(249, 115, 22, 0.1));
  border-color: rgba(245, 158, 11, 0.3);
}

.warning-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.warning-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: white;
}

.warning-icon-yellow {
  background: linear-gradient(135deg, #eab308, #f59e0b);
}

.warning-icon-orange {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.warning-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 2px;
}

.warning-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
}

.warning-content {
  margin-top: 12px;
  margin-left: 44px;
}

.warning-tip {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.8;
  margin-top: 10px;
  margin-left: 44px;
}

/* Overlap Items */
.overlap-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 12px;
  background: rgba(234, 179, 8, 0.1);
  border-radius: 8px;
  margin-bottom: 6px;
}

.overlap-item:last-child {
  margin-bottom: 0;
}

.overlap-courses {
  display: flex;
  align-items: center;
  gap: 6px;
}

.overlap-code {
  font-size: 12px;
  font-weight: 700;
  color: #fbbf24;
}

.overlap-details {
  font-size: 11px;
  color: var(--text-secondary);
}

.overlap-duration {
  color: #fbbf24;
  font-weight: 600;
}

/* Excluded Courses */
.excluded-courses {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.excluded-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 700;
  color: #fca5a5;
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 6px;
}

/* Stats Container */
.stats-container {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 16px;
  box-shadow: var(--shadow-card);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(90px, 1fr));
  gap: 10px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 14px 12px;
  border-radius: 12px;
  color: white;
  text-align: center;
}

.stat-value {
  font-size: 22px;
  font-weight: 800;
  line-height: 1.1;
}

.stat-value small {
  font-size: 11px;
  font-weight: 600;
  opacity: 0.85;
  margin-left: 2px;
}

.stat-label {
  font-size: 10px;
  font-weight: 600;
  opacity: 0.9;
  margin-top: 4px;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.stat-cyan { background: var(--stat-cyan); }
.stat-blue { background: var(--stat-blue); }
.stat-purple { background: var(--stat-purple); }
.stat-green { background: var(--stat-green); }
.stat-orange { background: var(--stat-orange); }
.stat-red { background: linear-gradient(135deg, #ef4444, #dc2626); }

.stats-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.stats-season {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
}

.stats-date {
  font-size: 12px;
  color: var(--text-secondary);
}

/* Course Items */
.course-item {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
}
.course-item:hover {
  background-color: var(--course-item-hover);
  border-color: var(--accent);
  transform: translateX(2px);
}

.section-list {
  background-color: var(--bg-tertiary);
}

.section-item:hover {
  background-color: var(--course-item-hover);
}
.section-item.selected {
  background-color: var(--section-selected);
}

.checkbox {
  border-color: var(--checkbox-border);
}
.checkbox.checked {
  background: var(--checkbox-checked);
  border-color: transparent;
}

/* Mode Toggle */
.mode-toggle-container {
  position: relative;
  display: flex;
  background: var(--accent-muted);
  border-radius: 12px;
  padding: 4px;
  border: 1px solid var(--border-color);
}

.mode-toggle-slider {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  border-radius: 8px;
  transition: all 280ms cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
}

.mode-toggle-slider.slide-left {
  transform: translateX(0);
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.35);
}

.mode-toggle-slider.slide-right {
  transform: translateX(100%);
  background: linear-gradient(135deg, #10b981, #14b8a6);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.35);
}

.mode-toggle-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  z-index: 1;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
}

.mode-toggle-btn:hover:not(.active) {
  color: var(--text-primary);
}

.mode-toggle-btn.active {
  color: white;
}

.mode-icon {
  font-size: 14px;
  transition: transform 0.3s ease;
}

.mode-description {
  text-align: center;
  font-size: 11px;
  color: var(--text-secondary);
  margin-top: 8px;
  padding: 0 8px;
  line-height: 1.4;
}

.mode-toggle-btn.active .mode-icon {
  animation: bounce-icon 0.4s ease;
}

@keyframes bounce-icon {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

/* Badges & Tags */
.badge-count {
  background: var(--badge-bg);
  color: var(--badge-text);
  font-weight: 700;
}

.badge-section {
  background: var(--badge-bg);
  color: var(--badge-text);
}

.tag-selected {
  background: var(--tag-selected);
  color: white;
}

/* Selected Courses Card */
.selected-courses-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  box-shadow: var(--shadow-card);
  padding: 12px;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.selected-courses-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.selected-courses-title {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--text-secondary);
}

.selected-courses-count {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--badge-bg);
  color: var(--badge-text);
}

.clear-all-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  font-size: 10px;
  font-weight: 600;
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.clear-all-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
}

.selected-courses-content {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  overflow-y: auto;
  flex: 1;
  align-content: flex-start;
  justify-content: center;
  mask-image: linear-gradient(to bottom, black calc(100% - 16px), transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, black calc(100% - 16px), transparent 100%);
}

.selected-courses-content::-webkit-scrollbar {
  width: 5px;
}

.selected-courses-content::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: 3px;
}

.selected-courses-content::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
}

.selected-courses-content::-webkit-scrollbar-thumb:hover {
  background: #6366f1;
}

/* Courses List Card */
.courses-list-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  box-shadow: var(--shadow-card);
  display: grid;
  grid-template-rows: auto auto 1fr;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.courses-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid var(--border-color);
}

.courses-list-title {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--text-secondary);
}

.courses-list-count {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--badge-bg);
  color: var(--badge-text);
}

.courses-search-input {
  margin: 10px 12px;
  padding: 10px 14px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 13px;
  outline: none;
  transition: all 0.2s ease;
}

.courses-search-input::placeholder {
  color: var(--text-secondary);
}

.courses-search-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.courses-list-content {
  flex: 1 1 0;
  overflow-y: auto;
  padding: 0 12px 12px;
  display: block;
  min-height: 0;
  mask-image: linear-gradient(to bottom, black calc(100% - 20px), transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, black calc(100% - 20px), transparent 100%);
}

.courses-list-content::-webkit-scrollbar {
  width: 6px;
}

.courses-list-content::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: 3px;
}

.courses-list-content::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
}

.courses-list-content::-webkit-scrollbar-thumb:hover {
  background: #6366f1;
}

/* Courses Loading Animation */
.courses-loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  grid-row: 1 / -1;
  grid-column: 1 / -1;
  min-height: 300px;
  padding: 20px;
}

.courses-loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 280px;
}

.courses-loading-icon {
  color: #6366f1;
  animation: pulse-glow 2s ease-in-out infinite;
}

.courses-loading-rays {
  animation: rotate-rays 3s linear infinite;
  transform-origin: center;
}

.courses-loading-center {
  animation: pulse-center 1.5s ease-in-out infinite;
}

@keyframes rotate-rays {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse-center {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(0.9); }
}

@keyframes pulse-glow {
  0%, 100% { 
    filter: drop-shadow(0 0 8px rgba(99, 102, 241, 0.4));
  }
  50% { 
    filter: drop-shadow(0 0 16px rgba(99, 102, 241, 0.7));
  }
}

.courses-loading-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.5px;
}

.courses-skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.courses-skeleton-item {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 12px;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-line {
  border-radius: 4px;
  background: linear-gradient(
    90deg,
    var(--border-color) 0%,
    var(--bg-tertiary) 50%,
    var(--border-color) 100%
  );
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.5s ease-in-out infinite;
}

.skeleton-title {
  height: 14px;
  width: 70%;
  margin-bottom: 8px;
}

.skeleton-subtitle {
  height: 10px;
  width: 50%;
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

@keyframes skeleton-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Empty State */
.courses-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  grid-row: 1 / -1;
  grid-column: 1 / -1;
  min-height: 300px;
  padding: 30px;
  text-align: center;
}

.tag-conflict {
  background: var(--tag-conflict);
  color: white;
}

.course-tag {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

/* Warnings & Errors */
.conflict-warning {
  background: var(--conflict-bg);
  border: 1px solid var(--conflict-border);
  color: var(--conflict-text);
}

.error-box {
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  color: var(--error-text);
}

/* Empty State */
.empty-icon {
  background: var(--empty-icon-bg);
  color: var(--empty-icon-color);
}

/* Generating Animation */
.generating-animation {
  position: relative;
}

.calendar-container {
  position: relative;
  width: 160px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.calendar-icon {
  width: 100px;
  height: 100px;
  border-radius: 24px;
  background: linear-gradient(135deg, var(--accent-purple), var(--accent-blue));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  animation: calendar-pulse 2s ease-in-out infinite;
  box-shadow: 0 10px 40px rgba(99, 102, 241, 0.4);
}

@keyframes calendar-pulse {
  0%, 100% { 
    transform: scale(1) rotate(0deg); 
    box-shadow: 0 10px 40px rgba(99, 102, 241, 0.4);
  }
  50% { 
    transform: scale(1.1) rotate(3deg); 
    box-shadow: 0 20px 60px rgba(99, 102, 241, 0.6);
  }
}

.floating-element {
  position: absolute;
  font-size: 24px;
  animation: float-around 3s ease-in-out infinite;
}

.el-1 { top: 0; left: 20%; animation-delay: 0s; }
.el-2 { top: 20%; right: 0; animation-delay: 0.5s; }
.el-3 { bottom: 0; left: 30%; animation-delay: 1s; }
.el-4 { top: 40%; left: 0; animation-delay: 1.5s; }

@keyframes float-around {
  0%, 100% { 
    transform: translateY(0) scale(1); 
    opacity: 0.7;
  }
  50% { 
    transform: translateY(-15px) scale(1.2); 
    opacity: 1;
  }
}

.progress-container {
  width: 200px;
}

.progress-bar {
  height: 6px;
  background: var(--accent-muted);
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #8b5cf6, #6366f1);
  background-size: 200% 100%;
  animation: progress-move 1.5s ease-in-out infinite;
  border-radius: 6px;
  box-shadow: 0 0 8px rgba(99, 102, 241, 0.3);
}

@keyframes progress-move {
  0% { 
    width: 0%; 
    background-position: 0% 0%;
  }
  50% { 
    width: 70%; 
    background-position: 100% 0%;
  }
  100% { 
    width: 100%; 
    background-position: 0% 0%;
  }
}

.loading-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-purple), var(--accent-blue));
  animation: dot-bounce 1.4s ease-in-out infinite both;
}

@keyframes dot-bounce {
  0%, 80%, 100% { 
    transform: scale(0.6);
    opacity: 0.5;
  }
  40% { 
    transform: scale(1);
    opacity: 1;
  }
}

/* Course Detail Cards */
.course-detail-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-card);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px;
}
.course-detail-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
  border-color: var(--accent);
}

.course-detail-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0,0,0,0.15);
}

.course-detail-header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a855f7 100%);
  height: 130px;
  display: flex;
  flex-direction: column;
}

.course-name-truncate {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
  max-height: 2.6em;
}

.course-detail-card:nth-child(3n+2) .course-detail-header {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 50%, #1d4ed8 100%);
}

.course-detail-card:nth-child(3n+3) .course-detail-header {
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%);
}

.credit-badge {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.detail-row {
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.detail-row:hover {
  transform: scale(1.02);
}

.detail-row.type-lecture {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(29, 78, 216, 0.15));
  border-color: rgba(59, 130, 246, 0.5);
  color: var(--text-primary);
}

.detail-row.type-lab {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.2), rgba(4, 120, 87, 0.15));
  border-color: rgba(16, 185, 129, 0.5);
  color: var(--text-primary);
}

.detail-row.type-ps {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(109, 40, 217, 0.15));
  border-color: rgba(139, 92, 246, 0.5);
  color: var(--text-primary);
}

.detail-row.type-other {
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.2), rgba(194, 65, 12, 0.15));
  border-color: rgba(249, 115, 22, 0.5);
  color: var(--text-primary);
}

/* ===== ANIMATIONS ===== */
@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slide-up {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slide-down {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes expand {
  from { opacity: 0; max-height: 0; }
  to { opacity: 1; max-height: 500px; }
}

@keyframes pop {
  0% { transform: scale(0.8); opacity: 0; }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-4px); }
  40%, 80% { transform: translateX(4px); }
}

@keyframes check {
  from { transform: scale(0); }
  to { transform: scale(1); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.animate-fade-in { animation: fade-in 0.3s ease-out forwards; }
.animate-slide-up { animation: slide-up 0.4s ease-out forwards; opacity: 0; }
.animate-slide-down { animation: slide-down 0.4s ease-out forwards; }
.animate-expand { animation: expand 0.3s ease-out forwards; }
.animate-pop { animation: pop 0.3s ease-out forwards; }
.animate-shake { animation: shake 0.4s ease-out; }
.animate-check { animation: check 0.2s ease-out forwards; }
.animate-float { animation: float 3s ease-in-out infinite; }

/* ===== SCROLLBAR ===== */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: var(--border-color); border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: var(--text-secondary); }

/* Hide scrollbar utility */
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}

/* Mobile improvements */
@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
    gap: 8px;
  }
  
  .stat-item {
    padding: 10px 8px;
  }
  
  .stat-value {
    font-size: 18px;
  }
  
  .stat-label {
    font-size: 9px;
  }
  
  .warning-content {
    margin-left: 0;
    margin-top: 10px;
  }
  
  .warning-tip {
    margin-left: 0;
  }
}

/* ===== TOP BAR TABS ===== */
.topbar-tabs {
  display: flex;
  justify-content: center;
  gap: 4px;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  max-width: 100%;
  padding: 4px;
  background: var(--accent-muted);
  border-radius: 12px;
}

.topbar-tabs::-webkit-scrollbar {
  display: none;
}

.topbar-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 18px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  border-radius: 8px;
  white-space: nowrap;
  flex-shrink: 0;
  cursor: pointer;
  background: transparent;
}

.topbar-tab:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.topbar-tab.active {
  color: white;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
}

.topbar-tab-badge {
  background: rgba(255,255,255,0.25);
  color: white;
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 700;
  min-width: 18px;
  text-align: center;
}

.topbar-tab:not(.active) .topbar-tab-badge {
  background: var(--badge-bg);
  color: var(--badge-text);
}

@media (max-width: 640px) {
  .topbar-tab {
    padding: 6px 12px;
    font-size: 12px;
    gap: 4px;
  }
  
  .topbar-tab svg {
    width: 14px;
    height: 14px;
  }
}

/* ===== OLD TAB NAVIGATION (kept for sidebar mobile) ===== */
.tab-navigation {
  display: flex;
  gap: 4px;
  background: var(--bg-secondary);
  padding: 4px;
  border-radius: 12px;
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.tab-btn:hover {
  color: var(--text-primary);
  background: var(--bg-tertiary);
}

.tab-btn.active {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
}

.tab-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 10px;
  font-weight: 700;
}

/* ===== QUOTA WATCH LIST ===== */
.quota-info-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  padding: 14px;
  box-shadow: var(--shadow-card);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
}
.quota-info-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--accent);
}

.quota-watch-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.quota-watch-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid var(--border-color);
}

.quota-watch-title {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--text-secondary);
}

.quota-watch-count {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--badge-bg);
  color: var(--badge-text);
}

.quota-watch-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quota-watch-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  transition: all 0.2s ease;
}

.quota-watch-item:hover {
  border-color: #6366f1;
}

.quota-watch-item.quota-available {
  border-left: 3px solid #10b981;
}

.quota-watch-item.quota-full {
  border-left: 3px solid #ef4444;
}

.quota-watch-item.quota-loading {
  opacity: 0.7;
}

.quota-display {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  min-width: 60px;
}

.quota-numbers {
  display: flex;
  align-items: baseline;
  gap: 2px;
  font-weight: 700;
}

.quota-remaining {
  font-size: 16px;
}

.quota-separator {
  font-size: 12px;
  color: var(--text-secondary);
}

.quota-total {
  font-size: 12px;
  color: var(--text-secondary);
}

.quota-bar {
  width: 56px;
  height: 5px;
  background: var(--accent-muted);
  border-radius: 6px;
  overflow: hidden;
}

.quota-bar-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 280ms cubic-bezier(0.4, 0, 0.2, 1);
}

.quota-remove-btn {
  padding: 4px;
  border-radius: 6px;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.quota-remove-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.quota-loading-spinner {
  min-width: 60px;
  display: flex;
  justify-content: center;
}

.quota-empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.section-add-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.section-add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-color: transparent;
  color: white;
}

.section-add-btn.added {
  background: rgba(16, 185, 129, 0.2);
  border-color: #10b981;
  color: #10b981;
  cursor: default;
}

.section-add-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Quota Mini Badge (in course list) */
.quota-mini-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  background: var(--bg-tertiary);
}

.quota-mini-badge.quota-green {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.quota-mini-badge.quota-red {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

/* Quota Tags (in bottom section) */
.quota-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  transition: all 0.2s ease;
}

.quota-tag-available {
  background: linear-gradient(135deg, #10b981, #059669);
}

.quota-tag-full {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.quota-tag-code {
  font-family: monospace;
  font-weight: 700;
}

.quota-tag-section {
  opacity: 0.8;
  font-size: 10px;
}

.quota-tag-value {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.25);
  color: white;
}

/* ===== QUOTA DETAIL CARDS ===== */
.quota-detail-card {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.quota-detail-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.quota-detail-header {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.quota-detail-card.quota-available .quota-detail-header {
  background: linear-gradient(135deg, #10b981, #059669);
}

.quota-detail-card.quota-full .quota-detail-header {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.quota-detail-card.quota-pending .quota-detail-header {
  background: linear-gradient(135deg, #6b7280, #4b5563);
}

.quota-progress {
  background: var(--bg-secondary);
  padding: 12px;
  border-radius: 10px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: var(--bg-secondary);
  border-radius: 10px;
}

.detail-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  flex-shrink: 0;
}

.detail-text {
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
}

/* ===== GRADE CALCULATOR ===== */
.grade-card {
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.grade-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.grade-card-header {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  min-height: 100px;
}

.grade-card:nth-child(2n) .grade-card-header {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
}

.grade-card:nth-child(3n) .grade-card-header {
  background: linear-gradient(135deg, #10b981, #059669);
}

.grade-card:nth-child(4n) .grade-card-header {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.grade-card:nth-child(5n) .grade-card-header {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.grade-component-row {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  transition: all 0.2s ease;
}

.grade-component-row:hover {
  border-color: rgba(99, 102, 241, 0.3);
}

.grade-result-summary {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
}

/* Number input arrows removal */
.grade-component-row input[type="number"]::-webkit-inner-spin-button,
.grade-component-row input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.grade-component-row input[type="number"] {
  -moz-appearance: textfield;
  appearance: textfield;
}

/* ===== TRANSITIONS ===== */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
