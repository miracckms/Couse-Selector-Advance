import { ref, watch } from 'vue'
import { preferencesAPI } from '../api/api.js'

// Merging debounce: accumulates updates and sends them all in one API call
function createMergingDebounce(fn, delay) {
  let timeoutId = null
  let pendingUpdates = {}
  return function (updates) {
    Object.assign(pendingUpdates, updates)
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => {
      const merged = { ...pendingUpdates }
      pendingUpdates = {}
      fn(merged)
    }, delay)
  }
}

/**
 * Composable for managing user preferences with auto-save to backend
 * Replaces localStorage with API calls
 */
export function useUserPreferences() {
  // State
  const preferences = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  const isSaving = ref(false)

  // Load preferences from backend
  const loadPreferences = async () => {
    isLoading.value = true
    error.value = null
    
    try {
      const data = await preferencesAPI.getPreferences()
      preferences.value = data
      
      // Parse JSON fields
      if (data.selectedCoursesAuto) {
        data.selectedCoursesAuto = JSON.parse(data.selectedCoursesAuto)
      }
      if (data.selectedSections) {
        data.selectedSections = JSON.parse(data.selectedSections)
      }
      if (data.scheduleResult) {
        data.scheduleResult = JSON.parse(data.scheduleResult)
      }
      if (data.quotaWatchList) {
        data.quotaWatchList = JSON.parse(data.quotaWatchList)
      }
      if (data.gradeCourses) {
        data.gradeCourses = JSON.parse(data.gradeCourses)
      }
      if (data.tabOrder) {
        data.tabOrder = JSON.parse(data.tabOrder)
      }
      if (data.hiddenTabs) {
        data.hiddenTabs = JSON.parse(data.hiddenTabs)
      }
      
      return data
    } catch (err) {
      console.error('Failed to load preferences:', err)
      error.value = err.message
      // Return empty preferences on error
      return {
        departmentId: null,
        scheduleMode: 'auto',
        selectedCoursesAuto: [],
        selectedSections: {},
        scheduleResult: null,
        quotaWatchList: [],
        activeTab: 'schedule',
        gradeCourses: [],
        theme: 'light',
        language: 'tr'
      }
    } finally {
      isLoading.value = false
    }
  }

  // Save with merging debounce - accumulates all pending updates
  const debouncedSave = createMergingDebounce(async (updates) => {
    isSaving.value = true
    try {
      await preferencesAPI.patchPreferences(updates)
    } catch (err) {
      console.error('Failed to save preferences:', err)
      error.value = err.message
    } finally {
      isSaving.value = false
    }
  }, 1000) // 1 second debounce

  // Update methods with auto-save
  const updateDepartment = (departmentId) => {
    if (preferences.value) {
      preferences.value.departmentId = departmentId
    }
    debouncedSave({ departmentId })
  }

  const updateScheduleMode = (scheduleMode) => {
    if (preferences.value) {
      preferences.value.scheduleMode = scheduleMode
    }
    debouncedSave({ scheduleMode })
  }

  const updateSelectedCourses = (selectedCoursesAuto) => {
    if (preferences.value) {
      preferences.value.selectedCoursesAuto = selectedCoursesAuto
    }
    debouncedSave({ 
      selectedCoursesAuto: JSON.stringify(selectedCoursesAuto) 
    })
  }

  const updateSelectedSections = (selectedSections) => {
    if (preferences.value) {
      preferences.value.selectedSections = selectedSections
    }
    debouncedSave({ 
      selectedSections: JSON.stringify(selectedSections) 
    })
  }

  const updateScheduleResult = (scheduleResult) => {
    if (preferences.value) {
      preferences.value.scheduleResult = scheduleResult
    }
    debouncedSave({ 
      scheduleResult: JSON.stringify(scheduleResult) 
    })
  }

  const updateQuotaWatchList = (quotaWatchList) => {
    if (preferences.value) {
      preferences.value.quotaWatchList = quotaWatchList
    }
    debouncedSave({ 
      quotaWatchList: JSON.stringify(quotaWatchList) 
    })
  }

  const updateActiveTab = (activeTab) => {
    if (preferences.value) {
      preferences.value.activeTab = activeTab
    }
    debouncedSave({ activeTab })
  }

  const updateGradeCourses = (gradeCourses) => {
    if (preferences.value) {
      preferences.value.gradeCourses = gradeCourses
    }
    debouncedSave({ 
      gradeCourses: JSON.stringify(gradeCourses) 
    })
  }

  const updateTheme = (theme) => {
    if (preferences.value) {
      preferences.value.theme = theme
    }
    debouncedSave({ theme })
  }

  const updateLanguage = (language) => {
    if (preferences.value) {
      preferences.value.language = language
    }
    debouncedSave({ language })
  }

  const updateTabOrder = (tabOrder) => {
    if (preferences.value) {
      preferences.value.tabOrder = tabOrder
    }
    debouncedSave({ tabOrder: JSON.stringify(tabOrder) })
  }

  const updateHiddenTabs = (hiddenTabs) => {
    if (preferences.value) {
      preferences.value.hiddenTabs = hiddenTabs
    }
    debouncedSave({ hiddenTabs: JSON.stringify(hiddenTabs) })
  }

  const updateDefaultTab = (defaultTab) => {
    if (preferences.value) {
      preferences.value.defaultTab = defaultTab
    }
    debouncedSave({ defaultTab })
  }

  return {
    // State
    preferences,
    isLoading,
    isSaving,
    error,
    
    // Methods
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
  }
}
