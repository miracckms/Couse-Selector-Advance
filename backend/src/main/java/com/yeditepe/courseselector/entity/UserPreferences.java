package com.yeditepe.courseselector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * User preferences and session data
 * Stores user's current selections, watch lists, and other preferences
 */
@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Current selections
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "schedule_mode", length = 20)
    private String scheduleMode; // 'auto' or 'manual'

    @Column(name = "selected_courses_auto", columnDefinition = "TEXT")
    private String selectedCoursesAuto; // JSON array of course codes

    @Column(name = "selected_sections", columnDefinition = "TEXT")
    private String selectedSections; // JSON object mapping course codes to sections

    @Column(name = "schedule_result", columnDefinition = "TEXT")
    private String scheduleResult; // JSON of current schedule result

    // Quota watch list
    @Column(name = "quota_watch_list", columnDefinition = "TEXT")
    private String quotaWatchList; // JSON array of watched courses

    @Column(name = "active_tab", length = 50)
    private String activeTab;

    // Grade calculator courses
    @Column(name = "grade_courses", columnDefinition = "TEXT")
    private String gradeCourses; // JSON array of courses for grade calculation

    // Tab customization
    @Column(name = "tab_order", columnDefinition = "TEXT")
    private String tabOrder; // JSON array: ["schedule","quota","grade"]

    @Column(name = "hidden_tabs", columnDefinition = "TEXT")
    private String hiddenTabs; // JSON array: ["grade"]

    @Column(name = "default_tab", length = 50)
    private String defaultTab; // Which tab to show on login

    // Theme and language
    @Column(name = "theme", length = 20)
    private String theme; // 'light' or 'dark'

    @Column(name = "language", length = 10)
    private String language; // 'tr' or 'en'

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
