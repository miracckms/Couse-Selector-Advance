package com.yeditepe.courseselector.service;

import com.yeditepe.courseselector.entity.AcademicSeason;
import com.yeditepe.courseselector.entity.Course;
import com.yeditepe.courseselector.entity.CourseSection;
import com.yeditepe.courseselector.entity.Department;
import com.yeditepe.courseselector.repository.AcademicSeasonRepository;
import com.yeditepe.courseselector.repository.CourseRepository;
import com.yeditepe.courseselector.repository.CourseSectionRepository;
import com.yeditepe.courseselector.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Synchronization Service
 * Syncs data from Yeditepe API to Database
 * Scheduled to run daily at 06:00
 */
@Service
@Slf4j
public class DataSyncService {

    private final YeditepeApiService yeditepeApiService;
    private final AcademicSeasonRepository seasonRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final CourseSectionRepository sectionRepository;

    public DataSyncService(YeditepeApiService yeditepeApiService,
                          AcademicSeasonRepository seasonRepository,
                          DepartmentRepository departmentRepository,
                          CourseRepository courseRepository,
                          CourseSectionRepository sectionRepository) {
        this.yeditepeApiService = yeditepeApiService;
        this.seasonRepository = seasonRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

    /**
     * Scheduled job - runs every day at 06:00
     * Cron format: second minute hour day-of-month month day-of-week
     */
    @Scheduled(cron = "${data.sync.cron:0 0 6 * * *}")
    @Transactional
    public void scheduledSync() {
        log.info("🔄 Scheduled data sync started at {}", LocalDateTime.now());
        try {
            syncAllData();
            log.info("✅ Scheduled data sync completed successfully");
        } catch (Exception e) {
            log.error("❌ Scheduled data sync failed", e);
        }
    }

    /**
     * Manual sync - can be triggered via API
     */
    @Transactional
    public void syncAllData() {
        long startTime = System.currentTimeMillis();
        
        log.info("📊 Starting full data synchronization...");
        
        try {
            // 1. Sync Seasons
            log.info("📅 Syncing academic seasons...");
            syncSeasons();
            
            // 2. Sync Departments
            log.info("🏢 Syncing departments...");
            syncDepartments();
            
            // 3. Sync Courses
            log.info("📚 Syncing courses...");
            syncCourses();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ Full synchronization completed in {} ms", duration);
            
        } catch (Exception e) {
            log.error("❌ Data synchronization failed", e);
            throw new RuntimeException("Data sync failed: " + e.getMessage(), e);
        }
    }

    /**
     * Sync Academic Seasons
     */
    @Transactional
    public void syncSeasons() {
        try {
            List<com.yeditepe.courseselector.dto.AcademicSeason> apiSeasons = 
                yeditepeApiService.getAcademicSeasons();
            
            log.info("Found {} seasons from API", apiSeasons.size());
            
            int updated = 0, created = 0;
            
            for (com.yeditepe.courseselector.dto.AcademicSeason apiSeason : apiSeasons) {
                var existingSeason = seasonRepository.findById(apiSeason.getId());
                
                AcademicSeason season;
                if (existingSeason.isPresent()) {
                    season = existingSeason.get();
                    updated++;
                } else {
                    season = new AcademicSeason();
                    season.setId(apiSeason.getId());
                    created++;
                }
                
                season.setName(apiSeason.getName());
                season.setNameEn(apiSeason.getNameEn());
                season.setNameTr(apiSeason.getNameTr());
                season.setActive(apiSeason.getActive());
                season.setStartDate(apiSeason.getStartDate());
                season.setEndDate(apiSeason.getEndDate());
                
                seasonRepository.save(season);
            }
            
            log.info("✅ Seasons synced: {} created, {} updated", created, updated);
            
        } catch (Exception e) {
            log.error("❌ Failed to sync seasons", e);
            throw e;
        }
    }

    /**
     * Sync Departments
     */
    @Transactional
    public void syncDepartments() {
        try {
            List<com.yeditepe.courseselector.dto.Department> apiDepartments = 
                yeditepeApiService.getDepartments();
            
            log.info("Found {} departments from API", apiDepartments.size());
            
            int updated = 0, created = 0;
            
            for (com.yeditepe.courseselector.dto.Department apiDept : apiDepartments) {
                var existingDept = departmentRepository.findById(apiDept.getId());
                
                Department dept;
                if (existingDept.isPresent()) {
                    dept = existingDept.get();
                    updated++;
                } else {
                    dept = new Department();
                    dept.setId(apiDept.getId());
                    created++;
                }
                
                dept.setName(apiDept.getName());
                dept.setNameEn(apiDept.getNameEn());
                dept.setNameTr(apiDept.getNameTr());
                dept.setCode(apiDept.getCode());
                dept.setFacultyId(apiDept.getFacultyId());
                dept.setFacultyName(apiDept.getFacultyName());
                
                departmentRepository.save(dept);
            }
            
            log.info("✅ Departments synced: {} created, {} updated", created, updated);
            
        } catch (Exception e) {
            log.error("❌ Failed to sync departments", e);
            throw e;
        }
    }

    /**
     * Sync Courses for all seasons and departments
     */
    @Transactional
    public void syncCourses() {
        try {
            List<AcademicSeason> seasons = seasonRepository.findAll();
            List<Department> departments = departmentRepository.findAll();
            
            if (seasons.isEmpty()) {
                log.warn("⚠️ No seasons found, skipping course sync");
                return;
            }
            
            if (departments.isEmpty()) {
                log.warn("⚠️ No departments found, skipping course sync");
                return;
            }
            
            // Sync courses for active season
            AcademicSeason activeSeason = seasons.stream()
                .filter(s -> s.getActive() != null && s.getActive() == 1)
                .findFirst()
                .orElse(seasons.get(0));
            
            log.info("Syncing courses for season: {} (ID: {})", activeSeason.getName(), activeSeason.getId());
            
            int totalCreated = 0, totalUpdated = 0;
            
            for (Department dept : departments) {
                try {
                    int[] counts = syncCoursesForSeasonAndDepartment(activeSeason.getId(), dept.getId());
                    totalCreated += counts[0];
                    totalUpdated += counts[1];
                } catch (Exception e) {
                    log.error("Failed to sync courses for dept {}: {}", dept.getName(), e.getMessage());
                }
            }
            
            log.info("✅ Courses synced for season {}: {} created, {} updated", 
                activeSeason.getName(), totalCreated, totalUpdated);
            
        } catch (Exception e) {
            log.error("❌ Failed to sync courses", e);
            throw e;
        }
    }

    /**
     * Sync courses for specific season and department
     */
    @Transactional
    public int[] syncCoursesForSeasonAndDepartment(Long seasonId, Long departmentId) {
        int created = 0, updated = 0;
        
        try {
            List<com.yeditepe.courseselector.dto.Course> apiCourses = 
                yeditepeApiService.getCourses(seasonId, departmentId);
            
            for (com.yeditepe.courseselector.dto.Course apiCourse : apiCourses) {
                var existingCourse = courseRepository.findBySeasonIdAndCodeAndSection(
                    seasonId, apiCourse.getCode(), apiCourse.getSection()
                );
                
                Course course;
                if (existingCourse.isPresent()) {
                    course = existingCourse.get();
                    updated++;
                } else {
                    course = new Course();
                    created++;
                }
                
                course.setSeasonId(seasonId);
                course.setDepartmentId(departmentId);
                course.setCode(apiCourse.getCode());
                course.setSection(apiCourse.getSection());
                course.setName(apiCourse.getName());
                course.setNameEn(apiCourse.getNameEn());
                course.setNameTr(apiCourse.getNameTr());
                course.setCredit(apiCourse.getCredit());
                course.setEcts(apiCourse.getEcts());
                course.setFullQuota(apiCourse.getFullQuota());
                course.setQuota(apiCourse.getQuota());
                course.setInfo(apiCourse.getInfo());
                course.setInstructor(apiCourse.getInstructor());
                course.setDepartmentName(apiCourse.getDepartmentName());
                
                course = courseRepository.save(course);
                
                // Sync course sections (details)
                if (apiCourse.getDetails() != null && !apiCourse.getDetails().isEmpty()) {
                    // Delete old sections
                    sectionRepository.deleteByCourseId(course.getId());
                    
                    // Create new sections
                    for (com.yeditepe.courseselector.dto.CourseDetail detail : apiCourse.getDetails()) {
                        CourseSection section = new CourseSection();
                        section.setCourse(course);
                        section.setDay(detail.getDay());
                        section.setStartTime(detail.getStartTime());
                        section.setEndTime(detail.getEndTime());
                        section.setBuilding(detail.getBuilding());
                        section.setRoom(detail.getRoom());
                        section.setType(detail.getType());
                        sectionRepository.save(section);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to sync courses for season {} dept {}: {}", 
                seasonId, departmentId, e.getMessage());
        }
        
        return new int[]{created, updated};
    }

    /**
     * Get sync status
     */
    public SyncStatus getSyncStatus() {
        SyncStatus status = new SyncStatus();
        status.setSeasonCount(seasonRepository.countSeasons());
        status.setDepartmentCount(departmentRepository.countDepartments());
        
        List<AcademicSeason> seasons = seasonRepository.findAll();
        if (!seasons.isEmpty()) {
            AcademicSeason latest = seasons.stream()
                .max((s1, s2) -> s1.getLastSyncedAt().compareTo(s2.getLastSyncedAt()))
                .orElse(seasons.get(0));
            status.setLastSyncTime(latest.getLastSyncedAt());
        }
        
        return status;
    }

    @lombok.Data
    public static class SyncStatus {
        private long seasonCount;
        private long departmentCount;
        private LocalDateTime lastSyncTime;
    }
}
