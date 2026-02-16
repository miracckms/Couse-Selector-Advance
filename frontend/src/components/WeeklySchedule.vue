<template>
  <div class="weekly-schedule" :class="isDarkMode ? 'dark' : 'light'">
    
    <!-- ===== MOBILE VIEW: Daily ===== -->
    <div class="mobile-view sm:hidden">
      <!-- Day Selector Tabs -->
      <div class="day-tabs">
        <button 
          v-for="(day, idx) in days" 
          :key="day"
          @click="selectedDay = idx"
          class="day-tab"
          :class="{ active: selectedDay === idx, 'has-courses': getDayCoursesCount(idx) > 0 }"
        >
          <span class="day-name">{{ day }}</span>
          <span v-if="getDayCoursesCount(idx) > 0" class="day-count">{{ getDayCoursesCount(idx) }}</span>
        </button>
      </div>
      
      <!-- Daily Schedule -->
      <div class="daily-schedule">
        <div v-if="getDayCoursesCount(selectedDay) === 0" class="no-courses-day">
          <span class="text-4xl mb-2">📅</span>
          <p>{{ $t('noCoursesThisDay') || 'Bu gün ders yok' }}</p>
        </div>
        
        <div v-else class="daily-courses">
          <div 
            v-for="course in getDayCourses(selectedDay)" 
            :key="`${course.courseCode}-${course.startHour}`"
            class="daily-course-card"
            :class="getCourseColor(course)"
          >
            <div class="daily-course-time">
              <span class="time-start">{{ course.startHour }}</span>
              <span class="time-separator">-</span>
              <span class="time-end">{{ course.endHour }}</span>
            </div>
            <div class="daily-course-info">
              <div class="daily-course-code">{{ course.courseCode }}</div>
              <div class="daily-course-type">{{ getTypeTurkish(course.type) }}</div>
              <div v-if="course.instructor" class="daily-course-instructor">
                {{ course.instructor }}
              </div>
            </div>
            <div v-if="hasConflict(selectedDay, course)" class="daily-conflict-badge">!</div>
          </div>
        </div>
      </div>
      
      <!-- Mobile Legend -->
      <div class="legend mobile-legend">
        <div class="legend-item">
          <span class="legend-dot course-blue"></span>
          <span>{{ $t('legend.lecture') }}</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot course-emerald"></span>
          <span>{{ $t('legend.lab') }}</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot course-purple"></span>
          <span>{{ $t('legend.ps') }}</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot course-orange"></span>
          <span>{{ $t('legend.other') }}</span>
        </div>
        <div class="legend-item conflict">
          <span class="legend-dot bg-red-500"></span>
          <span>{{ $t('legend.conflict') }}</span>
        </div>
      </div>
    </div>

    <!-- ===== DESKTOP VIEW: Weekly Grid ===== -->
    <div class="desktop-view hidden sm:block">
      <div class="schedule-container">
        <div class="weekly-grid" :style="{ gridTemplateColumns: `60px repeat(${days.length}, 1fr)` }">
          <!-- Header -->
          <div class="schedule-header-corner"></div>
          <div 
            v-for="day in days" 
            :key="day" 
            class="schedule-header-cell"
          >
            {{ day }}
          </div>

          <!-- Time Rows -->
          <!-- eslint-disable-next-line vue/no-v-for-template-key -->
          <template v-for="hour in hours" :key="hour">
            <div class="schedule-time-cell">
              {{ formatHour(hour) }}
            </div>
            <div 
              v-for="(day, dayIdx) in days" 
              :key="`${hour}-${day}`"
              class="schedule-cell"
            >
              <div 
                v-for="course in getCoursesAt(dayIdx, hour)" 
                :key="`${course.courseCode}-${course.startHour}-${course.type}`"
                class="course-card"
                :class="[getCourseColor(course), { 'has-conflict': hasConflict(dayIdx, course) }]"
                :style="{ height: `${getHeight(course)}px` }"
              >
                <div class="course-content">
                  <div class="course-code">{{ course.courseCode }}</div>
                  <div class="course-type-badge">
                    {{ getTypeTurkish(course.type) }}
                  </div>
                  <div class="course-time">
                    {{ course.startHour }} - {{ course.endHour }}
                  </div>
                  <div class="course-instructor" v-if="course.instructor && getHeight(course) > 70">
                    {{ course.instructor }}
                  </div>
                </div>
                <div v-if="hasConflict(dayIdx, course)" class="conflict-badge" :title="$t('conflictWarning')">
                  <span>!</span>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>

      <!-- Desktop Legend -->
      <div class="legend">
        <div class="legend-item">
          <span class="legend-dot course-blue"></span>
          <span>{{ $t('legend.lecture') }}</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot course-emerald"></span>
          <span>{{ $t('legend.lab') }}</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot course-purple"></span>
          <span>{{ $t('legend.ps') }}</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot course-orange"></span>
          <span>{{ $t('legend.other') }}</span>
        </div>
        <div class="legend-item conflict">
          <span class="legend-dot bg-red-500"></span>
          <span>{{ $t('legend.conflict') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  schedule: {
    type: Object,
    required: true
  },
  isDarkMode: {
    type: Boolean,
    default: false
  },
  locale: {
    type: String,
    default: 'tr'
  }
})

// ===== SHARED STATE =====
const selectedDay = ref(0) // Selected day for mobile view (0 = Monday)

const days = computed(() => [
  t('daysShort.MON'),
  t('daysShort.TUE'),
  t('daysShort.WED'),
  t('daysShort.THU'),
  t('daysShort.FRI')
])

const dayMap = { 'MON': 0, 'TUE': 1, 'WED': 2, 'THU': 3, 'FRI': 4, 'SAT': 5, 'SUN': 6 }

// ===== DESKTOP WEEKLY VIEW =====
const hours = computed(() => {
  let min = 24, max = 0
  Object.values(props.schedule || {}).forEach(dayCourses => {
    dayCourses.forEach(course => {
      const start = parseInt(course.startHour?.split(':')[0] || 9)
      const end = parseInt(course.endHour?.split(':')[0] || 17)
      if (start < min) min = start
      if (end > max) max = end
    })
  })
  const startHour = Math.max(8, min)
  const endHour = Math.min(22, max + 1)
  return Array.from({ length: endHour - startHour }, (_, i) => startHour + i)
})

const formatHour = (hour) => {
  return `${hour.toString().padStart(2, '0')}:00`
}

const getCoursesAt = (dayIdx, hour) => {
  const dayKey = Object.keys(dayMap).find(k => dayMap[k] === dayIdx)
  const courses = props.schedule?.[dayKey] || []
  return courses.filter(c => {
    const startH = parseInt(c.startHour?.split(':')[0] || 0)
    return startH === hour
  })
}

const getHeight = (course) => {
  const start = parseInt(course.startHour?.split(':')[0] || 0) * 60 + parseInt(course.startHour?.split(':')[1] || 0)
  const end = parseInt(course.endHour?.split(':')[0] || 0) * 60 + parseInt(course.endHour?.split(':')[1] || 0)
  const duration = end - start
  return Math.max(70, (duration / 60) * 70 - 4)
}

// ===== SHARED FUNCTIONS =====
const getAllCoursesForDay = (dayIdx) => {
  const dayKey = Object.keys(dayMap).find(k => dayMap[k] === dayIdx)
  return props.schedule?.[dayKey] || []
}

// Get all courses for a day sorted by start time (for mobile view)
const getDayCourses = (dayIdx) => {
  const courses = getAllCoursesForDay(dayIdx)
  return [...courses].sort((a, b) => {
    const timeA = parseInt(a.startHour?.replace(':', '') || 0)
    const timeB = parseInt(b.startHour?.replace(':', '') || 0)
    return timeA - timeB
  })
}

// Get course count for a day (for mobile tabs)
const getDayCoursesCount = (dayIdx) => {
  return getAllCoursesForDay(dayIdx).length
}

const parseTime = (timeStr) => {
  if (!timeStr) return 0
  const parts = timeStr.split(':')
  return parseInt(parts[0]) * 60 + parseInt(parts[1] || 0)
}

const hasConflict = (dayIdx, course) => {
  const allCourses = getAllCoursesForDay(dayIdx)
  const courseStart = parseTime(course.startHour)
  const courseEnd = parseTime(course.endHour)
  
  for (const other of allCourses) {
    if (other.courseCode === course.courseCode && other.startHour === course.startHour && other.type === course.type) continue
    
    const otherStart = parseTime(other.startHour)
    const otherEnd = parseTime(other.endHour)
    
    if (courseStart < otherEnd && otherStart < courseEnd) {
      return true
    }
  }
  return false
}

const getCourseColor = (course) => {
  const type = (course.type || '').toUpperCase()
  if (type.includes('LEC') || type.includes('LECTURE') || type.includes('TEO')) return 'course-blue'
  if (type.includes('LAB') || type.includes('LABOR')) return 'course-emerald'
  if (type.includes('PS') || type.includes('PROBLEM')) return 'course-purple'
  return 'course-orange'
}

const getTypeTurkish = (type) => {
  const typeStr = (type || '').toUpperCase()
  if (typeStr.includes('LEC') || typeStr.includes('LECTURE') || typeStr.includes('TEO')) return t('typesShort.lecture')
  if (typeStr.includes('LAB') || typeStr.includes('LABOR')) return t('typesShort.lab')
  if (typeStr.includes('PS') || typeStr.includes('PROBLEM')) return t('typesShort.ps')
  return type || t('typesShort.other')
}
</script>

<style scoped>
.weekly-schedule.dark {
  --bg-secondary: #1e293b;
  --bg-tertiary: #334155;
  --text-primary: #f8fafc;
  --text-secondary: #94a3b8;
  --border-color: #475569;
  --header-bg: #334155;
  --cell-hover: rgba(99, 102, 241, 0.1);
  --tab-active: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.weekly-schedule.light {
  --bg-secondary: #ffffff;
  --bg-tertiary: #f8fafc;
  --tab-active: linear-gradient(135deg, #6366f1, #8b5cf6);
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --border-color: #e2e8f0;
  --header-bg: #f1f5f9;
  --cell-hover: rgba(99, 102, 241, 0.05);
}

/* ===== MOBILE VIEW STYLES ===== */
.mobile-view {
  flex-direction: column;
  gap: 12px;
}

@media (min-width: 640px) {
  .mobile-view {
    display: none !important;
  }
}

@media (max-width: 639px) {
  .mobile-view {
    display: flex;
  }
  .desktop-view {
    display: none !important;
  }
}

/* Day Tabs */
.day-tabs {
  display: flex;
  gap: 6px;
  padding: 4px;
  background: var(--bg-tertiary);
  border-radius: 12px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.day-tab {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 10px 8px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.day-tab .day-name {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.day-tab .day-count {
  font-size: 9px;
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(99, 102, 241, 0.15);
  color: #6366f1;
}

.day-tab.active {
  background: var(--tab-active);
  color: white;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.day-tab.active .day-count {
  background: rgba(255, 255, 255, 0.25);
  color: white;
}

.day-tab.has-courses:not(.active) {
  color: var(--text-primary);
}

/* Daily Schedule */
.daily-schedule {
  min-height: 200px;
}

.no-courses-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--text-secondary);
  text-align: center;
  font-size: 14px;
}

.daily-courses {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.daily-course-card {
  display: flex;
  align-items: stretch;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  position: relative;
}

.daily-course-time {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 14px;
  min-width: 70px;
  background: rgba(0,0,0,0.1);
  font-weight: 700;
}

.daily-course-time .time-start {
  font-size: 14px;
  color: inherit;
}

.daily-course-time .time-separator {
  font-size: 10px;
  opacity: 0.6;
  margin: 2px 0;
}

.daily-course-time .time-end {
  font-size: 12px;
  opacity: 0.8;
}

.daily-course-info {
  flex: 1;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.daily-course-code {
  font-weight: 700;
  font-size: 14px;
  color: inherit;
}

.daily-course-type {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  opacity: 0.8;
}

.daily-course-instructor {
  font-size: 11px;
  opacity: 0.75;
  margin-top: 2px;
}

.daily-conflict-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 20px;
  height: 20px;
  background: #ef4444;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 12px;
  box-shadow: 0 2px 6px rgba(239, 68, 68, 0.4);
}

/* Mobile Legend */
.mobile-legend {
  padding: 10px 0;
  border-top: 1px solid var(--border-color);
  margin-top: 8px;
}

/* ===== DESKTOP VIEW STYLES - WEEKLY GRID ===== */
/* Responsive display handled by media queries above */

/* Schedule Container */
.schedule-container {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}

.weekly-grid {
  display: grid;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-md, 0 4px 16px rgba(0,0,0,0.1));
}

/* Header */
.schedule-header-corner {
  background: var(--header-bg);
  border-bottom: 2px solid var(--border-color);
}

.schedule-header-cell {
  padding: 16px 8px;
  text-align: center;
  font-weight: 800;
  font-size: 13px;
  color: var(--text-primary);
  background: var(--header-bg);
  border-bottom: 2px solid var(--border-color);
  border-left: 1px solid var(--border-color);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Time Column */
.schedule-time-cell {
  padding: 12px 8px;
  text-align: center;
  font-size: 12px;
  font-weight: 700;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-color);
  height: 70px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

/* Cells */
.schedule-cell {
  position: relative;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  border-left: 1px solid var(--border-color);
  min-height: 70px;
  padding: 3px;
  transition: background 0.2s;
}

.schedule-cell:hover {
  background: var(--cell-hover);
}

/* Course Cards */
.course-card {
  position: absolute;
  top: 3px;
  left: 3px;
  right: 3px;
  padding: 8px 10px;
  border-radius: 12px;
  color: white;
  overflow: hidden;
  z-index: 10;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  transition: all 0.2s ease;
}

.course-card:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
  z-index: 20;
}

.course-card.has-conflict {
  animation: pulse-conflict 2s ease-in-out infinite;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.6), 0 4px 12px rgba(0,0,0,0.2);
}

@keyframes pulse-conflict {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.85; }
}

.course-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.course-code {
  font-weight: 900;
  font-size: 13px;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
  margin-bottom: 2px;
}

.course-type-badge {
  display: inline-block;
  font-size: 9px;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(255,255,255,0.25);
  width: fit-content;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.course-time {
  font-size: 11px;
  font-weight: 600;
  opacity: 0.95;
}

.course-instructor {
  font-size: 10px;
  opacity: 0.9;
  margin-top: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Conflict Badge */
.conflict-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 20px;
  height: 20px;
  background: #ef4444;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.5);
  animation: bounce-badge 1s ease-in-out infinite;
}

@keyframes bounce-badge {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

/* Course Colors - Vibrant Gradients */
.course-blue { 
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}
.course-emerald { 
  background: linear-gradient(135deg, #10b981 0%, #047857 100%);
}
.course-purple { 
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
}
.course-orange { 
  background: linear-gradient(135deg, #f97316 0%, #c2410c 100%);
}

/* Legend */
.legend {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
  padding: 8px 12px;
  background: var(--bg-tertiary);
  border-radius: 8px;
}

@media (min-width: 640px) {
  .legend {
    gap: 20px;
    margin-top: 20px;
    padding: 12px 20px;
    border-radius: 12px;
  }
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 10px;
  font-weight: 600;
  color: var(--text-secondary);
}

@media (min-width: 640px) {
  .legend-item {
    gap: 8px;
    font-size: 13px;
  }
}

.legend-item.conflict {
  color: #ef4444;
  padding-left: 8px;
  border-left: 2px solid var(--border-color);
}

.legend-dot {
  width: 16px;
  height: 16px;
  border-radius: 6px;
}

.bg-red-500 {
  background: #ef4444;
}
</style>
