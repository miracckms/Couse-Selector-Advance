export default {
  // Header
  appTitle: 'Course Planner',
  university: 'Yeditepe University',
  activeUsers: '{count} active',

  // Sidebar
  semester: 'Semester',
  allDepartments: 'All Departments',
  loadCourses: 'Load Courses',
  loading: 'Loading...',
  auto: 'Auto',
  manual: 'Manual',
  autoModeDesc: 'Select courses, system finds the best sections automatically.',
  manualModeDesc: 'Choose each section yourself, full control is yours.',
  courses: 'Courses',
  searchPlaceholder: 'Search course code, name or instructor...',
  group: 'Section',
  selected: 'Selected',
  clearAll: 'Clear All',
  conflictWarning: 'Conflict detected!',
  generateSchedule: 'Generate Schedule',
  generating: 'Generating...',
  selectDepartmentFirst: 'Select a department and load courses first',
  noCourses: 'No courses found for this department',
  noCoursesThisDay: 'No courses this day',
  scrollHint: 'Scroll',

  // Days
  days: {
    MON: 'Monday',
    TUE: 'Tuesday',
    WED: 'Wednesday',
    THU: 'Thursday',
    FRI: 'Friday',
    SAT: 'Saturday',
    SUN: 'Sunday'
  },
  daysShort: {
    MON: 'Mon',
    TUE: 'Tue',
    WED: 'Wed',
    THU: 'Thu',
    FRI: 'Fri',
    SAT: 'Sat',
    SUN: 'Sun'
  },

  // Course types
  types: {
    lecture: 'Lecture',
    lab: 'Laboratory',
    ps: 'Problem Session',
    other: 'Other'
  },
  typesShort: {
    lecture: 'Lecture',
    lab: 'Lab',
    ps: 'PS',
    other: 'Other'
  },

  // Schedule results
  totalCourses: 'Courses',
  totalCredits: 'Total Credits',
  totalEcts: 'Total ECTS',
  daysWithClasses: 'Class Days',
  gaps: 'Gaps',
  overlap: 'Overlap',
  minutes: 'min',

  // Warnings
  overlapWarning: 'Overlap Warning',
  overlapMessage: 'Schedule has {minutes} minutes of overlap.',
  overlapTip: 'Tip: You may not be able to attend parts of overlapping courses.',
  partialSchedule: 'Partial Schedule Created',
  partialScheduleMessage: 'Not all courses fit. The following courses were excluded due to conflicts:',

  // Schedule
  weeklySchedule: 'Weekly Schedule',
  clearSchedule: 'Clear Schedule',
  clearScheduleConfirm: 'Are you sure you want to clear the generated schedule?',
  scheduledCourses: 'Courses in Generated Schedule',
  credit: 'Credit',
  quota: 'Quota',

  // Empty state
  noScheduleYet: 'No Schedule Yet',
  noScheduleMessage: 'Select a department from the side panel, load courses and generate a schedule',
  selectCourse: 'Select Course',
  generatingSchedule: 'Generating Schedule...',
  calculatingSchedule: 'Calculating the optimal course schedule, please wait',

  // Errors
  errorLoadingSeasons: 'Error loading semesters: {message}',
  errorLoadingDepartments: 'Error loading departments: {message}',
  errorLoadingCourses: 'Error loading courses: {message}',
  errorGenerating: 'Error generating schedule: {message}',

  // Legend
  legend: {
    lecture: 'Lecture',
    lab: 'Lab',
    ps: 'PS',
    other: 'Other',
    conflict: 'Conflict'
  },

  // Tabs
  scheduleTab: 'Schedule',
  quotaTab: 'Quota',

  // Quota
  quotaCheck: 'Check Quota',
  quotaDescription: 'Select courses to track their quotas in real-time.',
  watchList: 'Watching',
  emptyWatchList: 'Your watch list is empty.',
  addToWatchList: 'Add Course',
  watchedCourses: 'Watched',
  watchedCoursesDetail: 'Watched Courses',
  available: 'Available',
  full: 'Full',
  pending: 'Pending',
  remaining: 'Remaining',
  capacity: 'Capacity',
  enrolled: 'Enrolled',
  noWatchedCourses: 'No Watched Courses',
  noWatchedCoursesMessage: 'Select courses from the left menu to track their quotas.',
  addCourseToWatch: 'Add Course',
  removeFromWatch: 'Remove from Watch',
  refreshQuotas: 'Refresh Quotas',
  checking: 'Checking...',
  lastChecked: 'Last checked',
  justNow: 'Just now',
  minutesAgo: '{count} min ago',
  errorCheckingQuotas: 'Error checking quotas: {message}',

  // Grade Calculator
  gradeTab: 'Grades',
  gradeCalculator: 'Grade Calculator',
  gradeDescription: 'Add courses, enter grade components, calculate your grade.',
  addCourse: 'Add Course',
  courseName: 'Course Name',
  addComponent: 'Add Component',
  componentName: 'Component',
  weight: 'Weight (%)',
  score: 'Score',
  totalWeight: 'Total Weight',
  weightedTotal: 'Weighted Total',
  letterGrade: 'Letter Grade',
  removeCourse: 'Remove Course',
  removeComponent: 'Remove',
  noGradeCourses: 'No Courses Added',
  noGradeCoursesMessage: 'Add courses from the left menu to start calculating grades.',
  gradeCoursesList: 'My Courses',
  gradeCoursesDetail: 'Grade Calculation Details',
  enterCourseName: 'Enter course name...',
  defaultComponents: {
    midterm: 'Midterm',
    final: 'Final',
    quiz: 'Quiz',
    homework: 'Homework',
    project: 'Project',
    lab: 'Laboratory',
    attendance: 'Attendance'
  },
  weightWarning: 'Total weight must be 100%',
  weightPerfect: 'Total weight is 100%',
  clearAllGrades: 'Clear All',
  averageGrade: 'Average',
  courseCount: 'Courses',
  calculated: 'Calculated',
  notCalculated: 'Incomplete',
  
  // Data sync indicators
  saving: 'Saving...',
  loading: 'Loading...',
  saved: 'Saved',
  loadingData: 'Loading your data...',

  // Profile
  profileTitle: 'Profile Settings',
  personalInfo: 'Personal Information',
  profileFirstName: 'First Name',
  profileLastName: 'Last Name',
  profileEmail: 'Email',
  profileStudentId: 'Student ID',
  profileDepartment: 'Department',
  saveProfile: 'Save Profile',
  profileSaved: 'Profile updated successfully',

  // Password Change
  changePassword: 'Change Password',
  currentPassword: 'Current Password',
  newPassword: 'New Password',
  confirmPassword: 'Confirm New Password',
  currentPasswordPlaceholder: 'Enter your current password',
  newPasswordPlaceholder: 'Enter your new password',
  confirmPasswordPlaceholder: 'Confirm your new password',
  changePasswordBtn: 'Change Password',
  passwordChanged: 'Password changed successfully',
  passwordMismatch: 'Passwords do not match',
  pwWeak: 'Weak',
  pwFair: 'Fair',
  pwGood: 'Good',
  pwStrong: 'Strong',

  // Tab Customization
  tabCustomization: 'Menu Settings',
  tabCustomizationDesc: 'Drag to reorder menus, toggle visibility, or set the default page.',
  defaultTabLabel: 'Default',
  setDefault: 'Set default',
  showTab: 'Show',
  hideTab: 'Hide',
  minOneTab: 'At least one menu must be visible.'
}
