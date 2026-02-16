export default {
  // Header
  appTitle: 'Ders Planlayıcı',
  university: 'Yeditepe Üniversitesi',
  activeUsers: '{count} aktif',

  // Sidebar
  semester: 'Dönem',
  allDepartments: 'Tüm Bölümler',
  loadCourses: 'Dersleri Getir',
  loading: 'Yükleniyor...',
  auto: 'Otomatik',
  manual: 'Manuel',
  autoModeDesc: 'Dersleri seç, sistem en uygun grupları otomatik belirlesin.',
  manualModeDesc: 'Her dersin grubunu kendin seç, tam kontrol sende.',
  courses: 'Dersler',
  searchPlaceholder: 'Ders kodu, adı veya hoca ara...',
  group: 'Grup',
  selected: 'Seçilen',
  clearAll: 'Temizle',
  conflictWarning: 'Çakışma var!',
  generateSchedule: 'Program Oluştur',
  generating: 'Oluşturuluyor...',
  selectDepartmentFirst: 'Önce bölüm seçip dersleri getirin',
  noCourses: 'Bu bölümde ders bulunamadı',
  noCoursesThisDay: 'Bu gün ders yok',
  scrollHint: 'Kaydırın',

  // Days
  days: {
    MON: 'Pazartesi',
    TUE: 'Salı',
    WED: 'Çarşamba',
    THU: 'Perşembe',
    FRI: 'Cuma',
    SAT: 'Cumartesi',
    SUN: 'Pazar'
  },
  daysShort: {
    MON: 'Pzt',
    TUE: 'Sal',
    WED: 'Çar',
    THU: 'Per',
    FRI: 'Cum',
    SAT: 'Cmt',
    SUN: 'Paz'
  },

  // Course types
  types: {
    lecture: 'Teorik Ders',
    lab: 'Laboratuvar',
    ps: 'Problem Çözümü',
    other: 'Diğer'
  },
  typesShort: {
    lecture: 'Teorik',
    lab: 'Lab',
    ps: 'PS',
    other: 'Diğer'
  },

  // Schedule results
  totalCourses: 'Ders Sayısı',
  totalCredits: 'Toplam Kredi',
  totalEcts: 'Toplam AKTS',
  daysWithClasses: 'Ders Günü',
  gaps: 'Boşluk',
  overlap: 'Çakışma',
  minutes: 'dk',

  // Warnings
  overlapWarning: 'Çakışma Uyarısı',
  overlapMessage: 'Programda {minutes} dakikalık çakışma var.',
  overlapTip: 'İpucu: Çakışan derslerin bir kısmına katılamayabilirsiniz.',
  partialSchedule: 'Kısmi Program Oluşturuldu',
  partialScheduleMessage: 'Tüm dersler sığmadı. Çakışma nedeniyle şu dersler çıkarıldı:',

  // Schedule
  weeklySchedule: 'Haftalık Program',
  clearSchedule: 'Programı Sil',
  clearScheduleConfirm: 'Oluşturulan programı silmek istediğinize emin misiniz?',
  scheduledCourses: 'Oluşturulan Programdaki Dersler',
  credit: 'Kredi',
  quota: 'Kontenjan',

  // Empty state
  noScheduleYet: 'Henüz Program Yok',
  noScheduleMessage: 'Sol panelden bölüm seçin, dersleri yükleyin ve program oluşturun',
  selectCourse: 'Ders Seç',
  generatingSchedule: 'Program Oluşturuluyor...',
  calculatingSchedule: 'En uygun ders programı hesaplanıyor, lütfen bekleyin',

  // Errors
  errorLoadingSeasons: 'Dönemler yüklenirken hata: {message}',
  errorLoadingDepartments: 'Bölümler yüklenirken hata: {message}',
  errorLoadingCourses: 'Dersler yüklenirken hata: {message}',
  errorGenerating: 'Program oluşturulurken hata: {message}',

  // Legend
  legend: {
    lecture: 'Teorik',
    lab: 'Lab',
    ps: 'PS',
    other: 'Diğer',
    conflict: 'Çakışma'
  },

  // Tabs
  scheduleTab: 'Program',
  quotaTab: 'Kontenjan',

  // Quota
  quotaCheck: 'Kontenjan Sorgula',
  quotaDescription: 'Dersleri seç, kontenjanlarını anlık olarak takip et.',
  watchList: 'İzlenenler',
  emptyWatchList: 'İzleme listeniz boş.',
  addToWatchList: 'Ders Ekle',
  watchedCourses: 'İzlenen Ders',
  watchedCoursesDetail: 'İzlenen Dersler',
  available: 'Kontenjan Var',
  full: 'Dolu',
  pending: 'Bekliyor',
  remaining: 'Kalan',
  capacity: 'Kapasite',
  enrolled: 'Kayıtlı',
  noWatchedCourses: 'İzlenen Ders Yok',
  noWatchedCoursesMessage: 'Sol menüden ders seçerek kontenjanlarını takip edebilirsiniz.',
  addCourseToWatch: 'Ders Ekle',
  removeFromWatch: 'İzlemeyi Kaldır',
  refreshQuotas: 'Kontenjanlari Güncelle',
  checking: 'Kontrol ediliyor...',
  lastChecked: 'Son kontrol',
  justNow: 'Az önce',
  minutesAgo: '{count} dk önce',
  errorCheckingQuotas: 'Kontenjan kontrolünde hata: {message}',

  // Grade Calculator
  gradeTab: 'Not Hesapla',
  gradeCalculator: 'Not Hesaplayıcı',
  gradeDescription: 'Derslerini ekle, puan kalemlerini gir, notunu hesapla.',
  addCourse: 'Ders Ekle',
  courseName: 'Ders Adı',
  addComponent: 'Puan Kalemi Ekle',
  componentName: 'Puan Kalemi',
  weight: 'Ağırlık (%)',
  score: 'Puan',
  totalWeight: 'Toplam Ağırlık',
  weightedTotal: 'Ağırlıklı Toplam',
  letterGrade: 'Harf Notu',
  removeCourse: 'Dersi Sil',
  removeComponent: 'Kalemi Sil',
  noGradeCourses: 'Henüz Ders Eklenmedi',
  noGradeCoursesMessage: 'Sol menüden ders ekleyerek not hesaplaması yapabilirsiniz.',
  gradeCoursesList: 'Derslerim',
  gradeCoursesDetail: 'Not Hesaplama Detayları',
  enterCourseName: 'Ders adı girin...',
  defaultComponents: {
    midterm: 'Vize',
    final: 'Final',
    quiz: 'Quiz',
    homework: 'Ödev',
    project: 'Proje',
    lab: 'Laboratuvar',
    attendance: 'Devam'
  },
  weightWarning: 'Ağırlık toplamı %100 olmalıdır',
  weightPerfect: 'Ağırlık toplamı %100',
  clearAllGrades: 'Tümünü Temizle',
  averageGrade: 'Ortalama',
  courseCount: 'Ders Sayısı',
  calculated: 'Hesaplanan',
  notCalculated: 'Eksik',
  
  // Data sync indicators
  saving: 'Kaydediliyor...',
  loading: 'Yükleniyor...',
  saved: 'Kaydedildi',
  loadingData: 'Verileriniz yükleniyor...',

  // Profile
  profileTitle: 'Profil Ayarları',
  personalInfo: 'Kişisel Bilgiler',
  profileFirstName: 'Ad',
  profileLastName: 'Soyad',
  profileEmail: 'E-posta',
  profileStudentId: 'Öğrenci No',
  profileDepartment: 'Bölüm',
  saveProfile: 'Bilgileri Kaydet',
  profileSaved: 'Profil başarıyla güncellendi',

  // Password Change
  changePassword: 'Şifre Değiştir',
  currentPassword: 'Mevcut Şifre',
  newPassword: 'Yeni Şifre',
  confirmPassword: 'Yeni Şifre (Tekrar)',
  currentPasswordPlaceholder: 'Mevcut şifrenizi girin',
  newPasswordPlaceholder: 'Yeni şifrenizi girin',
  confirmPasswordPlaceholder: 'Yeni şifrenizi tekrar girin',
  changePasswordBtn: 'Şifreyi Değiştir',
  passwordChanged: 'Şifre başarıyla değiştirildi',
  passwordMismatch: 'Şifreler eşleşmiyor',
  pwWeak: 'Zayıf',
  pwFair: 'Orta',
  pwGood: 'İyi',
  pwStrong: 'Güçlü',

  // Tab Customization
  tabCustomization: 'Menü Ayarları',
  tabCustomizationDesc: 'Menüleri sürükleyerek sıralayın, gizleyin veya varsayılan sayfayı seçin.',
  defaultTabLabel: 'Varsayılan',
  setDefault: 'Varsayılan yap',
  showTab: 'Göster',
  hideTab: 'Gizle',
  minOneTab: 'En az bir menü görünür olmalıdır.'
}
