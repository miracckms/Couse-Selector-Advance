import { createApp } from 'vue'
import AppWrapper from './AppWrapper.vue'
import i18n from './i18n.js'
import './style.css'

createApp(AppWrapper).use(i18n).mount('#app')
