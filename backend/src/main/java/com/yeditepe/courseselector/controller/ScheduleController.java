package com.yeditepe.courseselector.controller;

import com.yeditepe.courseselector.dto.Course;
import com.yeditepe.courseselector.dto.MessageResponse;
import com.yeditepe.courseselector.entity.SavedSchedule;
import com.yeditepe.courseselector.entity.User;
import com.yeditepe.courseselector.service.SavedScheduleService;
import com.yeditepe.courseselector.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@PreAuthorize("isAuthenticated()")
public class ScheduleController {

    private final SavedScheduleService scheduleService;
    private final UserService userService;

    public ScheduleController(SavedScheduleService scheduleService, UserService userService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveSchedule(@RequestBody Map<String, Object> request, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long seasonId = ((Number) request.get("seasonId")).longValue();
            String seasonName = (String) request.get("seasonName");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> coursesData = (List<Map<String, Object>>) request.get("courses");
            
            // Convert to Course objects (simplified - you may want to create a proper DTO)
            List<Course> courses = new java.util.ArrayList<>();
            for (Map<String, Object> courseData : coursesData) {
                Course course = new Course();
                course.setCode((String) courseData.get("code"));
                course.setNameEn((String) courseData.get("nameEn"));
                course.setSection(((Number) courseData.get("section")).intValue());
                course.setCredit(courseData.get("credit") != null ? ((Number) courseData.get("credit")).intValue() : 0);
                course.setEcts(courseData.get("ects") != null ? ((Number) courseData.get("ects")).intValue() : 0);
                course.setInstructor((String) courseData.get("instructor"));
                // Add more fields as needed
                courses.add(course);
            }

            SavedSchedule savedSchedule = scheduleService.saveSchedule(
                    user, name, description, seasonId, seasonName, courses);

            return ResponseEntity.ok(savedSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error saving schedule: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserSchedules(Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SavedSchedule> schedules = scheduleService.getUserSchedules(user);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SavedSchedule schedule = scheduleService.getScheduleById(id, user)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));

            List<Course> courses = scheduleService.getScheduleCourses(schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("schedule", schedule);
            response.put("courses", courses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/season/{seasonId}")
    public ResponseEntity<?> getSchedulesBySeason(@PathVariable Long seasonId, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SavedSchedule> schedules = scheduleService.getUserSchedulesBySeason(user, seasonId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoriteSchedules(Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SavedSchedule> schedules = scheduleService.getFavoriteSchedules(user);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SavedSchedule schedule = scheduleService.toggleFavorite(id, user);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, 
                                           @RequestBody Map<String, String> request,
                                           Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String name = request.get("name");
            String description = request.get("description");

            SavedSchedule schedule = scheduleService.updateSchedule(id, user, name, description);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            scheduleService.deleteSchedule(id, user);
            return ResponseEntity.ok(new MessageResponse("Schedule deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
