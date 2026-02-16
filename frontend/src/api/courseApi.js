import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const courseApi = {
  async getSeasons() {
    const response = await api.get('/seasons')
    return response.data
  },

  async getDepartments() {
    const response = await api.get('/departments')
    return response.data
  },

  async getCalendar() {
    const response = await api.get('/calendar')
    return response.data
  },

  async getCourses(seasonId, departmentId) {
    if (departmentId === 'all') {
      const response = await api.get(`/courses/${seasonId}/all`)
      return response.data
    }
    const response = await api.get(`/courses/${seasonId}/${departmentId}`)
    return response.data
  },

  async generateSchedule(seasonId, departmentId, courseCodes) {
    const response = await api.post('/schedule/generate', {
      seasonId,
      departmentId,
      courseCodes
    })
    return response.data
  },

  async generateScheduleAdvanced(requestData) {
    const response = await api.post('/schedule/generate', requestData)
    return response.data
  },

  async checkQuotas(seasonId, courses) {
    const response = await api.post('/quota/check', {
      seasonId,
      courses
    })
    return response.data
  }
}
