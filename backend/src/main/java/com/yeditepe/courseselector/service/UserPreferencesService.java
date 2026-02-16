package com.yeditepe.courseselector.service;

import com.yeditepe.courseselector.entity.User;
import com.yeditepe.courseselector.entity.UserPreferences;
import com.yeditepe.courseselector.repository.UserPreferencesRepository;
import com.yeditepe.courseselector.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    public UserPreferencesService(UserPreferencesRepository preferencesRepository,
                                 UserRepository userRepository) {
        this.preferencesRepository = preferencesRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get or create preferences for a user
     */
    @Transactional
    public UserPreferences getOrCreatePreferences(Long userId) {
        return preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    
                    UserPreferences prefs = new UserPreferences();
                    prefs.setUser(user);
                    prefs.setTheme("light");
                    prefs.setLanguage("tr");
                    prefs.setScheduleMode("auto");
                    prefs.setActiveTab("schedule");
                    
                    return preferencesRepository.save(prefs);
                });
    }

    /**
     * Update user preferences
     */
    @Transactional
    public UserPreferences updatePreferences(Long userId, UserPreferences updates) {
        UserPreferences existing = getOrCreatePreferences(userId);
        
        // Update fields if provided
        if (updates.getDepartmentId() != null) {
            existing.setDepartmentId(updates.getDepartmentId());
        }
        if (updates.getScheduleMode() != null) {
            existing.setScheduleMode(updates.getScheduleMode());
        }
        if (updates.getSelectedCoursesAuto() != null) {
            existing.setSelectedCoursesAuto(updates.getSelectedCoursesAuto());
        }
        if (updates.getSelectedSections() != null) {
            existing.setSelectedSections(updates.getSelectedSections());
        }
        if (updates.getScheduleResult() != null) {
            existing.setScheduleResult(updates.getScheduleResult());
        }
        if (updates.getQuotaWatchList() != null) {
            existing.setQuotaWatchList(updates.getQuotaWatchList());
        }
        if (updates.getActiveTab() != null) {
            existing.setActiveTab(updates.getActiveTab());
        }
        if (updates.getGradeCourses() != null) {
            existing.setGradeCourses(updates.getGradeCourses());
        }
        if (updates.getTabOrder() != null) {
            existing.setTabOrder(updates.getTabOrder());
        }
        if (updates.getHiddenTabs() != null) {
            existing.setHiddenTabs(updates.getHiddenTabs());
        }
        if (updates.getDefaultTab() != null) {
            existing.setDefaultTab(updates.getDefaultTab());
        }
        if (updates.getTheme() != null) {
            existing.setTheme(updates.getTheme());
        }
        if (updates.getLanguage() != null) {
            existing.setLanguage(updates.getLanguage());
        }
        
        return preferencesRepository.save(existing);
    }

    /**
     * Get preferences for a user
     */
    public Optional<UserPreferences> getPreferences(Long userId) {
        return preferencesRepository.findByUserId(userId);
    }

    /**
     * Delete preferences for a user
     */
    @Transactional
    public void deletePreferences(Long userId) {
        preferencesRepository.deleteByUserId(userId);
    }
}
