import { createI18n } from 'vue-i18n'
import tr from './locales/tr.js'
import en from './locales/en.js'

// Get saved language from localStorage or default to Turkish
const savedLanguage = localStorage.getItem('language') || 'tr'

const i18n = createI18n({
  legacy: false, // Use Composition API
  locale: savedLanguage,
  fallbackLocale: 'tr',
  messages: {
    tr,
    en
  }
})

export default i18n
