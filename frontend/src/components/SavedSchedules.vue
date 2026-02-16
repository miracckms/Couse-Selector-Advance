<template>
  <div class="p-4 sm:p-6 h-full overflow-y-auto">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-white">My Schedules</h2>
        <p class="text-sm text-gray-600 dark:text-gray-400 mt-1">
          {{ schedules.length }} saved schedule{{ schedules.length !== 1 ? 's' : '' }}
        </p>
      </div>
      <button
        v-if="currentSchedule"
        @click="showSaveDialog = true"
        class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors flex items-center gap-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        Save Current
      </button>
    </div>

    <!-- Filters -->
    <div class="flex gap-3 mb-6">
      <select
        v-model="filterSeason"
        class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500"
      >
        <option value="">All Semesters</option>
        <option v-for="season in seasons" :key="season.id" :value="season.id">
          {{ season.name }}
        </option>
      </select>
      <select
        v-model="filterType"
        class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500"
      >
        <option value="all">All</option>
        <option value="favorites">Favorites Only</option>
      </select>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <svg class="animate-spin h-8 w-8 text-indigo-600" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredSchedules.length === 0" class="text-center py-12">
      <svg class="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
      </svg>
      <p class="text-gray-600 dark:text-gray-400">No schedules found</p>
      <p class="text-sm text-gray-500 dark:text-gray-500 mt-2">Create your first schedule to get started!</p>
    </div>

    <!-- Schedules List -->
    <div v-else class="space-y-4">
      <div
        v-for="schedule in filteredSchedules"
        :key="schedule.id"
        class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-4 hover:shadow-lg transition-shadow"
      >
        <div class="flex items-start justify-between mb-3">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-1">
              <button
                @click="toggleFavorite(schedule.id)"
                class="text-2xl hover:scale-110 transition-transform"
              >
                {{ schedule.isFavorite ? '⭐' : '☆' }}
              </button>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
                {{ schedule.name }}
              </h3>
            </div>
            <p v-if="schedule.description" class="text-sm text-gray-600 dark:text-gray-400 mb-2">
              {{ schedule.description }}
            </p>
            <div class="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
              <span class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                {{ schedule.seasonName }}
              </span>
              <span class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                </svg>
                {{ schedule.totalCredits }} Credits
              </span>
              <span>{{ schedule.totalEcts }} ECTS</span>
            </div>
            <p class="text-xs text-gray-500 dark:text-gray-500 mt-2">
              Updated: {{ formatDate(schedule.updatedAt) }}
            </p>
          </div>
          <div class="flex flex-col gap-2 ml-4">
            <button
              @click="viewSchedule(schedule)"
              class="px-3 py-1.5 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors text-sm"
            >
              View
            </button>
            <button
              @click="editSchedule(schedule)"
              class="px-3 py-1.5 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors text-sm"
            >
              Edit
            </button>
            <button
              @click="deleteSchedule(schedule.id)"
              class="px-3 py-1.5 border border-red-300 dark:border-red-600 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors text-sm"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Save Dialog -->
    <div v-if="showSaveDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl max-w-md w-full p-6">
        <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-4">Save Schedule</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Schedule Name *</label>
            <input
              v-model="saveForm.name"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white"
              placeholder="e.g., Spring 2026 - Option A"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
            <textarea
              v-model="saveForm.description"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white"
              placeholder="Optional description..."
            ></textarea>
          </div>
          <div class="flex gap-3">
            <button
              @click="showSaveDialog = false"
              class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              Cancel
            </button>
            <button
              @click="saveSchedule"
              :disabled="saving"
              class="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors disabled:opacity-50"
            >
              {{ saving ? 'Saving...' : 'Save' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Dialog -->
    <div v-if="showEditDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl max-w-md w-full p-6">
        <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-4">Edit Schedule</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Schedule Name *</label>
            <input
              v-model="editForm.name"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
            <textarea
              v-model="editForm.description"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white"
            ></textarea>
          </div>
          <div class="flex gap-3">
            <button
              @click="showEditDialog = false"
              class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              Cancel
            </button>
            <button
              @click="updateSchedule"
              :disabled="saving"
              class="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors disabled:opacity-50"
            >
              {{ saving ? 'Updating...' : 'Update' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { scheduleAPI } from '../api/api.js';

export default {
  name: 'SavedSchedules',
  props: {
    currentSchedule: Object,
    seasons: Array,
    selectedSeason: Number,
  },
  data() {
    return {
      schedules: [],
      loading: false,
      saving: false,
      showSaveDialog: false,
      showEditDialog: false,
      filterSeason: '',
      filterType: 'all',
      saveForm: {
        name: '',
        description: '',
      },
      editForm: {
        id: null,
        name: '',
        description: '',
      },
    };
  },
  computed: {
    filteredSchedules() {
      let filtered = this.schedules;

      // Filter by season
      if (this.filterSeason) {
        filtered = filtered.filter(s => s.seasonId == this.filterSeason);
      }

      // Filter by type
      if (this.filterType === 'favorites') {
        filtered = filtered.filter(s => s.isFavorite);
      }

      return filtered;
    },
  },
  mounted() {
    this.loadSchedules();
  },
  methods: {
    async loadSchedules() {
      this.loading = true;
      try {
        this.schedules = await scheduleAPI.getSchedules();
      } catch (error) {
        console.error('Error loading schedules:', error);
      } finally {
        this.loading = false;
      }
    },

    async saveSchedule() {
      if (!this.saveForm.name) {
        alert('Please enter a schedule name');
        return;
      }

      this.saving = true;
      try {
        const scheduleData = {
          name: this.saveForm.name,
          description: this.saveForm.description,
          seasonId: this.selectedSeason,
          seasonName: this.seasons.find(s => s.id === this.selectedSeason)?.name || '',
          courses: this.currentSchedule.selectedCourses,
        };

        await scheduleAPI.saveSchedule(scheduleData);
        await this.loadSchedules();
        
        this.showSaveDialog = false;
        this.saveForm = { name: '', description: '' };
      } catch (error) {
        console.error('Error saving schedule:', error);
        alert('Failed to save schedule');
      } finally {
        this.saving = false;
      }
    },

    editSchedule(schedule) {
      this.editForm = {
        id: schedule.id,
        name: schedule.name,
        description: schedule.description || '',
      };
      this.showEditDialog = true;
    },

    async updateSchedule() {
      this.saving = true;
      try {
        await scheduleAPI.updateSchedule(
          this.editForm.id,
          this.editForm.name,
          this.editForm.description
        );
        await this.loadSchedules();
        this.showEditDialog = false;
      } catch (error) {
        console.error('Error updating schedule:', error);
        alert('Failed to update schedule');
      } finally {
        this.saving = false;
      }
    },

    async toggleFavorite(id) {
      try {
        await scheduleAPI.toggleFavorite(id);
        await this.loadSchedules();
      } catch (error) {
        console.error('Error toggling favorite:', error);
      }
    },

    async deleteSchedule(id) {
      if (!confirm('Are you sure you want to delete this schedule?')) {
        return;
      }

      try {
        await scheduleAPI.deleteSchedule(id);
        await this.loadSchedules();
      } catch (error) {
        console.error('Error deleting schedule:', error);
        alert('Failed to delete schedule');
      }
    },

    async viewSchedule(schedule) {
      try {
        const response = await scheduleAPI.getScheduleById(schedule.id);
        this.$emit('load-schedule', response);
      } catch (error) {
        console.error('Error loading schedule:', error);
        alert('Failed to load schedule');
      }
    },

    formatDate(dateString) {
      const date = new Date(dateString);
      const now = new Date();
      const diffMs = now - date;
      const diffMins = Math.floor(diffMs / 60000);
      const diffHours = Math.floor(diffMs / 3600000);
      const diffDays = Math.floor(diffMs / 86400000);

      if (diffMins < 1) return 'Just now';
      if (diffMins < 60) return `${diffMins} min ago`;
      if (diffHours < 24) return `${diffHours}h ago`;
      if (diffDays === 0) return 'Today';
      if (diffDays === 1) return 'Yesterday';
      if (diffDays < 7) return `${diffDays} days ago`;
      
      return date.toLocaleDateString();
    },
  },
};
</script>
