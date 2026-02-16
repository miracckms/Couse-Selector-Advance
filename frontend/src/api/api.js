import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || '/api';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.token) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Callback for auth state changes (set by AppWrapper)
let onAuthExpired = null;

export const setOnAuthExpired = (callback) => {
  onAuthExpired = callback;
};

// Track if a refresh is already in progress to avoid duplicate calls
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Response interceptor with automatic token refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Skip refresh logic for auth endpoints themselves
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url.includes('/auth/login') &&
      !originalRequest.url.includes('/auth/refresh-token')
    ) {
      if (isRefreshing) {
        // Queue the request while refresh is in progress
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const user = JSON.parse(localStorage.getItem('user') || '{}');

      if (user && user.refreshToken) {
        try {
          const response = await axios.post(`${API_URL}/auth/refresh-token`, {
            refreshToken: user.refreshToken,
          });

          const newData = response.data;
          // Update localStorage with new tokens
          localStorage.setItem('user', JSON.stringify(newData));

          // Update the Authorization header
          apiClient.defaults.headers.Authorization = `Bearer ${newData.token}`;
          originalRequest.headers.Authorization = `Bearer ${newData.token}`;

          processQueue(null, newData.token);

          return apiClient(originalRequest);
        } catch (refreshError) {
          processQueue(refreshError, null);
          localStorage.removeItem('user');
          if (onAuthExpired) {
            onAuthExpired();
          }
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      } else {
        // No refresh token available
        localStorage.removeItem('user');
        if (onAuthExpired) {
          onAuthExpired();
        }
      }
    }

    return Promise.reject(error);
  }
);

// ============ Authentication API ============

export const authAPI = {
  login: async (username, password) => {
    const response = await apiClient.post('/auth/login', { username, password });
    if (response.data.token) {
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  register: async (userData) => {
    const response = await apiClient.post('/auth/signup', userData);
    return response.data;
  },

  logout: async () => {
    const user = authAPI.getCurrentUser();
    if (user && user.refreshToken) {
      try {
        await apiClient.post('/auth/logout', { refreshToken: user.refreshToken });
      } catch {
        // Ignore errors during logout
      }
    }
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  isAuthenticated: () => {
    const user = authAPI.getCurrentUser();
    return !!(user && user.token);
  },

  validateToken: async () => {
    const user = authAPI.getCurrentUser();
    if (!user || !user.token) {
      return false;
    }
    try {
      // This will automatically try refresh via interceptor if 401
      await apiClient.get('/auth/me');
      return true;
    } catch {
      return false;
    }
  },

  getProfile: async () => {
    const response = await apiClient.get('/auth/me');
    return response.data;
  },

  updateProfile: async (profileData) => {
    const response = await apiClient.put('/auth/profile', profileData);
    return response.data;
  },

  changePassword: async (currentPassword, newPassword) => {
    const response = await apiClient.put('/auth/change-password', { currentPassword, newPassword });
    return response.data;
  },
};

// ============ Schedule API ============

export const scheduleAPI = {
  // Save a schedule
  saveSchedule: async (scheduleData) => {
    const response = await apiClient.post('/schedules/save', scheduleData);
    return response.data;
  },

  // Get all user's schedules
  getSchedules: async () => {
    const response = await apiClient.get('/schedules');
    return response.data;
  },

  // Get schedule by ID
  getScheduleById: async (id) => {
    const response = await apiClient.get(`/schedules/${id}`);
    return response.data;
  },

  // Get schedules for a specific season
  getSchedulesBySeason: async (seasonId) => {
    const response = await apiClient.get(`/schedules/season/${seasonId}`);
    return response.data;
  },

  // Get favorite schedules
  getFavorites: async () => {
    const response = await apiClient.get('/schedules/favorites');
    return response.data;
  },

  // Toggle favorite status
  toggleFavorite: async (id) => {
    const response = await apiClient.put(`/schedules/${id}/favorite`);
    return response.data;
  },

  // Update schedule
  updateSchedule: async (id, name, description) => {
    const response = await apiClient.put(`/schedules/${id}`, { name, description });
    return response.data;
  },

  // Delete schedule
  deleteSchedule: async (id) => {
    const response = await apiClient.delete(`/schedules/${id}`);
    return response.data;
  },
};

// ============ User Preferences API ============

export const preferencesAPI = {
  // Get user preferences
  getPreferences: async () => {
    const response = await apiClient.get('/preferences');
    return response.data;
  },

  // Update all preferences
  updatePreferences: async (preferences) => {
    const response = await apiClient.put('/preferences', preferences);
    return response.data;
  },

  // Partial update (only changed fields)
  patchPreferences: async (updates) => {
    const response = await apiClient.patch('/preferences', updates);
    return response.data;
  },

  // Helper methods for specific preference updates
  updateDepartment: async (departmentId) => {
    return preferencesAPI.patchPreferences({ departmentId });
  },

  updateScheduleMode: async (scheduleMode) => {
    return preferencesAPI.patchPreferences({ scheduleMode });
  },

  updateSelectedCourses: async (selectedCoursesAuto) => {
    return preferencesAPI.patchPreferences({ 
      selectedCoursesAuto: JSON.stringify(selectedCoursesAuto) 
    });
  },

  updateSelectedSections: async (selectedSections) => {
    return preferencesAPI.patchPreferences({ 
      selectedSections: JSON.stringify(selectedSections) 
    });
  },

  updateScheduleResult: async (scheduleResult) => {
    return preferencesAPI.patchPreferences({ 
      scheduleResult: JSON.stringify(scheduleResult) 
    });
  },

  updateQuotaWatchList: async (quotaWatchList) => {
    return preferencesAPI.patchPreferences({ 
      quotaWatchList: JSON.stringify(quotaWatchList) 
    });
  },

  updateActiveTab: async (activeTab) => {
    return preferencesAPI.patchPreferences({ activeTab });
  },

  updateGradeCourses: async (gradeCourses) => {
    return preferencesAPI.patchPreferences({ 
      gradeCourses: JSON.stringify(gradeCourses) 
    });
  },

  updateTheme: async (theme) => {
    return preferencesAPI.patchPreferences({ theme });
  },

  updateLanguage: async (language) => {
    return preferencesAPI.patchPreferences({ language });
  },
};

// ============ Course API (existing - enhanced) ============

export default {
  // Get all academic seasons
  async getSeasons() {
    const response = await apiClient.get('/seasons');
    return response.data;
  },

  // Get all departments
  async getDepartments() {
    const response = await apiClient.get('/departments');
    return response.data;
  },

  // Get calendar events
  async getCalendar() {
    const response = await apiClient.get('/calendar');
    return response.data;
  },

  // Get all courses for a season
  async getAllCourses(seasonId) {
    const response = await apiClient.get(`/courses/${seasonId}/all`);
    return response.data;
  },

  // Get courses for specific department and season
  async getCourses(seasonId, departmentId) {
    const response = await apiClient.get(`/courses/${seasonId}/${departmentId}`);
    return response.data;
  },

  // Generate schedule
  async generateSchedule(scheduleRequest) {
    const response = await apiClient.post('/schedule/generate', scheduleRequest);
    return response.data;
  },

  // Check course quotas
  async checkQuotas(quotaRequest) {
    const response = await apiClient.post('/quota/check', quotaRequest);
    return response.data;
  },

  // Get cache status
  async getCacheStatus() {
    const response = await apiClient.get('/cache/status');
    return response.data;
  },

  // Refresh cache
  async refreshCache() {
    const response = await apiClient.post('/cache/refresh');
    return response.data;
  },

  // Check if cache is ready
  async isCacheReady() {
    const response = await apiClient.get('/cache/ready');
    return response.data;
  },
};
