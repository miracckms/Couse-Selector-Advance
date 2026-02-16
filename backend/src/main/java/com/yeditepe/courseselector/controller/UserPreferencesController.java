package com.yeditepe.courseselector.controller;

import com.yeditepe.courseselector.entity.User;
import com.yeditepe.courseselector.entity.UserPreferences;
import com.yeditepe.courseselector.service.UserPreferencesService;
import com.yeditepe.courseselector.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;
    private final UserService userService;

    public UserPreferencesController(UserPreferencesService preferencesService,
                                    UserService userService) {
        this.preferencesService = preferencesService;
        this.userService = userService;
    }

    /**
     * Get current user's preferences
     */
    @GetMapping
    public ResponseEntity<UserPreferences> getPreferences(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Long userId = getUserIdFromAuth(authentication);
        UserPreferences prefs = preferencesService.getOrCreatePreferences(userId);
        return ResponseEntity.ok(prefs);
    }

    /**
     * Update current user's preferences
     */
    @PutMapping
    public ResponseEntity<UserPreferences> updatePreferences(
            @RequestBody UserPreferences updates,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Long userId = getUserIdFromAuth(authentication);
        UserPreferences updated = preferencesService.updatePreferences(userId, updates);
        return ResponseEntity.ok(updated);
    }

    /**
     * Partial update - only update specific fields
     */
    @PatchMapping
    public ResponseEntity<UserPreferences> partialUpdate(
            @RequestBody UserPreferences updates,
            Authentication authentication) {
        
        return updatePreferences(updates, authentication);
    }

    /**
     * Helper method to extract user ID from authentication
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId();
    }
}
