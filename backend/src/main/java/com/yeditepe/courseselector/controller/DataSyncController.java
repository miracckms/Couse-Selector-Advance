package com.yeditepe.courseselector.controller;

import com.yeditepe.courseselector.service.DataSyncService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

/**
 * Controller for manual data synchronization
 * Protected - only admins can trigger manual sync
 */
@RestController
@RequestMapping("/api/sync")
public class DataSyncController {

    private final DataSyncService dataSyncService;

    public DataSyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    /**
     * Get sync status
     */
    @GetMapping("/status")
    public ResponseEntity<DataSyncService.SyncStatus> getSyncStatus() {
        return ResponseEntity.ok(dataSyncService.getSyncStatus());
    }

    /**
     * Trigger manual full sync
     * Admin only
     */
    @PostMapping("/full")
    public ResponseEntity<String> triggerFullSync() {
        try {
            dataSyncService.syncAllData();
            return ResponseEntity.ok("Full sync completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Sync failed: " + e.getMessage());
        }
    }

    /**
     * Trigger seasons sync only
     * Admin only
     */
    @PostMapping("/seasons")
    public ResponseEntity<String> triggerSeasonsSync() {
        try {
            dataSyncService.syncSeasons();
            return ResponseEntity.ok("Seasons synced successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Seasons sync failed: " + e.getMessage());
        }
    }

    /**
     * Trigger departments sync only
     * Admin only
     */
    @PostMapping("/departments")
    public ResponseEntity<String> triggerDepartmentsSync() {
        try {
            dataSyncService.syncDepartments();
            return ResponseEntity.ok("Departments synced successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Departments sync failed: " + e.getMessage());
        }
    }

    /**
     * Trigger courses sync only
     * Admin only
     */
    @PostMapping("/courses")
    public ResponseEntity<String> triggerCoursesSync() {
        try {
            dataSyncService.syncCourses();
            return ResponseEntity.ok("Courses synced successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Courses sync failed: " + e.getMessage());
        }
    }
}
