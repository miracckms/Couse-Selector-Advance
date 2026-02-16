package com.yeditepe.courseselector.controller;

import com.yeditepe.courseselector.dto.*;
import com.yeditepe.courseselector.service.CourseCacheService;
import com.yeditepe.courseselector.service.ScheduleService;
import com.yeditepe.courseselector.service.YeditepeApiService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseCacheService cacheService;
    private final ScheduleService scheduleService;
    private final YeditepeApiService apiService;

    public CourseController(CourseCacheService cacheService, 
                           ScheduleService scheduleService,
                           YeditepeApiService apiService) {
        this.cacheService = cacheService;
        this.scheduleService = scheduleService;
        this.apiService = apiService;
    }

    @GetMapping("/seasons")
    public ResponseEntity<List<AcademicSeason>> getSeasons() {
        return ResponseEntity.ok(cacheService.getSeasons());
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(cacheService.getDepartments());
    }

    // Calendar endpoint removed - not needed for course selection

    @GetMapping("/courses/{seasonId}/all")
    public ResponseEntity<List<Course>> getAllCourses(@PathVariable Long seasonId) {
        return ResponseEntity.ok(cacheService.getAllCoursesForSeason(seasonId));
    }

    @GetMapping("/courses/{seasonId}/{departmentId}")
    public ResponseEntity<List<Course>> getCourses(
            @PathVariable Long seasonId,
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(cacheService.getCourses(seasonId, departmentId));
    }

    @PostMapping("/schedule/generate")
    public ResponseEntity<ScheduleResult> generateSchedule(@Valid @RequestBody ScheduleRequest request) {
        List<Course> allCourses;
        
        // Check if "all departments" mode
        if (request.getDepartmentId() == null || request.getDepartmentId() == 0) {
            // Get from cache - all departments
            allCourses = new java.util.ArrayList<>(
                cacheService.getAllCoursesForSeason(request.getSeasonId())
            );
        } else {
            // Get courses from primary department (from cache)
            allCourses = new java.util.ArrayList<>(
                cacheService.getCourses(request.getSeasonId(), request.getDepartmentId())
            );
            
            // Get courses from additional departments if specified
            if (request.getAdditionalDepartmentIds() != null && !request.getAdditionalDepartmentIds().isEmpty()) {
                for (Long deptId : request.getAdditionalDepartmentIds()) {
                    if (!deptId.equals(request.getDepartmentId())) { // Avoid duplicate
                        List<Course> deptCourses = cacheService.getCourses(request.getSeasonId(), deptId);
                        allCourses.addAll(deptCourses);
                    }
                }
            }
        }
        
        // Use the new method that handles both AUTO and MANUAL modes
        ScheduleResult result = scheduleService.generateSchedule(allCourses, request);
        return ResponseEntity.ok(result);
    }

    // ============ QUOTA CHECK ENDPOINT ============

    /**
     * Check quotas for specific courses - fetches FRESH data from API
     */
    @PostMapping("/quota/check")
    public ResponseEntity<Map<String, Object>> checkQuotas(@RequestBody QuotaRequest request) {
        if (request.getCourses() == null || request.getCourses().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "En az bir ders seçilmelidir"
            ));
        }

        // Group courses by department to minimize API calls
        Map<Long, List<QuotaRequest.CourseIdentifier>> coursesByDept = request.getCourses().stream()
            .filter(c -> c.getDepartmentId() != null)
            .collect(Collectors.groupingBy(QuotaRequest.CourseIdentifier::getDepartmentId));

        List<QuotaInfo> results = new ArrayList<>();
        
        // Fetch fresh data for each department
        for (Map.Entry<Long, List<QuotaRequest.CourseIdentifier>> entry : coursesByDept.entrySet()) {
            Long deptId = entry.getKey();
            List<QuotaRequest.CourseIdentifier> deptCourses = entry.getValue();
            
            try {
                // Fetch FRESH course data from API (not from cache)
                List<Course> freshCourses = apiService.getCourses(request.getSeasonId(), deptId);
                
                // Match requested courses with fresh data
                for (QuotaRequest.CourseIdentifier requested : deptCourses) {
                    Course found = freshCourses.stream()
                        .filter(c -> c.getCode().equals(requested.getCode()) && 
                                    c.getSection().equals(requested.getSection()))
                        .findFirst()
                        .orElse(null);
                    
                    if (found != null) {
                        results.add(new QuotaInfo(found));
                    } else {
                        results.add(QuotaInfo.notFound(requested.getCode(), requested.getSection()));
                    }
                }
            } catch (Exception e) {
                // If API call fails, mark all courses from this department as error
                for (QuotaRequest.CourseIdentifier requested : deptCourses) {
                    QuotaInfo errorInfo = new QuotaInfo();
                    errorInfo.setCode(requested.getCode());
                    errorInfo.setSection(requested.getSection());
                    errorInfo.setFound(false);
                    errorInfo.setError("API hatası: " + e.getMessage());
                    results.add(errorInfo);
                }
            }
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "quotas", results,
            "checkedAt", System.currentTimeMillis()
        ));
    }

    // ============ CACHE MANAGEMENT ENDPOINTS ============

    /**
     * Get cache status information
     */
    @GetMapping("/cache/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        return ResponseEntity.ok(cacheService.getCacheStats());
    }

    /**
     * Manually trigger data sync (DEPRECATED - use /api/sync/full instead)
     */
    @PostMapping("/cache/refresh")
    public ResponseEntity<Map<String, String>> refreshCache() {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is deprecated",
            "note", "Use /api/sync/full for manual data sync (admin only)"
        ));
    }

    /**
     * Check if cache is ready (data exists in database)
     */
    @GetMapping("/cache/ready")
    public ResponseEntity<Map<String, Object>> isCacheReady() {
        Map<String, Object> stats = cacheService.getCacheStats();
        return ResponseEntity.ok(Map.of(
            "ready", cacheService.isCacheReady(),
            "lastSync", stats.getOrDefault("lastSync", "Never"),
            "source", "database"
        ));
    }
}
