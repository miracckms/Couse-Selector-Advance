package com.yeditepe.courseselector.service;

import com.yeditepe.courseselector.dto.AcademicSeason;
import com.yeditepe.courseselector.dto.Course;
import com.yeditepe.courseselector.dto.CourseDetail;
import com.yeditepe.courseselector.dto.Department;
import com.yeditepe.courseselector.entity.CourseSection;
import com.yeditepe.courseselector.repository.AcademicSeasonRepository;
import com.yeditepe.courseselector.repository.CourseRepository;
import com.yeditepe.courseselector.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Course Cache Service - Now database-backed
 * Data is synced from Yeditepe API to database by DataSyncService (daily at 06:00)
 * This service reads from database instead of memory cache
 */
@Service
public class CourseCacheService {

    private static final Logger log = LoggerFactory.getLogger(CourseCacheService.class);

    private final YeditepeApiService yeditepeApiService;
    private final AcademicSeasonRepository seasonRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    public CourseCacheService(YeditepeApiService yeditepeApiService,
                             AcademicSeasonRepository seasonRepository,
                             DepartmentRepository departmentRepository,
                             CourseRepository courseRepository) {
        this.yeditepeApiService = yeditepeApiService;
        this.seasonRepository = seasonRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Get all academic seasons from database
     */
    public List<AcademicSeason> getSeasons() {
        try {
            List<com.yeditepe.courseselector.entity.AcademicSeason> entities = 
                seasonRepository.findAllByOrderByIdDesc();
            
            if (entities.isEmpty()) {
                log.warn("No seasons found in database, falling back to API");
                return yeditepeApiService.getAcademicSeasons();
            }
            
            return entities.stream()
                .map(this::convertSeasonToDto)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to get seasons from database, falling back to API", e);
            return yeditepeApiService.getAcademicSeasons();
        }
    }

    /**
     * Get all departments from database
     */
    public List<Department> getDepartments() {
        try {
            List<com.yeditepe.courseselector.entity.Department> entities = 
                departmentRepository.findAllByOrderByNameAsc();
            
            if (entities.isEmpty()) {
                log.warn("No departments found in database, falling back to API");
                return yeditepeApiService.getDepartments();
            }
            
            return entities.stream()
                .map(this::convertDepartmentToDto)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to get departments from database, falling back to API", e);
            return yeditepeApiService.getDepartments();
        }
    }

    // Calendar functionality removed - not needed for course selection

    /**
     * Get courses for specific season and department from database
     */
    public List<Course> getCourses(Long seasonId, Long departmentId) {
        try {
            List<com.yeditepe.courseselector.entity.Course> entities = 
                courseRepository.findBySeasonIdAndDepartmentId(seasonId, departmentId);
            
            if (entities.isEmpty()) {
                log.warn("No courses found in database for season {} dept {}, falling back to API", 
                    seasonId, departmentId);
                return yeditepeApiService.getCourses(seasonId, departmentId);
            }
            
            return entities.stream()
                .map(this::convertCourseToDto)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to get courses from database, falling back to API", e);
            return yeditepeApiService.getCourses(seasonId, departmentId);
        }
    }

    /**
     * Get all courses for a season from database
     */
    public List<Course> getAllCoursesForSeason(Long seasonId) {
        try {
            List<com.yeditepe.courseselector.entity.Course> entities = 
                courseRepository.findBySeasonId(seasonId);
            
            if (entities.isEmpty()) {
                log.warn("No courses found in database for season {}, returning empty list", seasonId);
                return Collections.emptyList();
            }
            
            return entities.stream()
                .map(this::convertCourseToDto)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to get all courses for season from database", e);
            return Collections.emptyList();
        }
    }

    /**
     * Check if cache is ready (database has data)
     */
    public boolean isCacheReady() {
        try {
            long seasonCount = seasonRepository.countSeasons();
            long deptCount = departmentRepository.countDepartments();
            return seasonCount > 0 && deptCount > 0;
        } catch (Exception e) {
            log.error("Failed to check cache status", e);
            return false;
        }
    }

    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long seasonCount = seasonRepository.countSeasons();
            long deptCount = departmentRepository.countDepartments();
            
            stats.put("seasons", seasonCount);
            stats.put("departments", deptCount);
            stats.put("ready", seasonCount > 0 && deptCount > 0);
            stats.put("source", "database");
            
            // Get last sync time
            List<com.yeditepe.courseselector.entity.AcademicSeason> seasons = 
                seasonRepository.findAll();
            if (!seasons.isEmpty()) {
                var latest = seasons.stream()
                    .max((s1, s2) -> s1.getLastSyncedAt().compareTo(s2.getLastSyncedAt()))
                    .orElse(seasons.get(0));
                stats.put("lastSync", latest.getLastSyncedAt());
            }
            
        } catch (Exception e) {
            log.error("Failed to get cache stats", e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }

    // ========== Conversion Methods ==========

    private AcademicSeason convertSeasonToDto(com.yeditepe.courseselector.entity.AcademicSeason entity) {
        AcademicSeason dto = new AcademicSeason();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNameEn(entity.getNameEn());
        dto.setNameTr(entity.getNameTr());
        dto.setActive(entity.getActive());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }

    private Department convertDepartmentToDto(com.yeditepe.courseselector.entity.Department entity) {
        Department dto = new Department();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNameEn(entity.getNameEn());
        dto.setNameTr(entity.getNameTr());
        dto.setCode(entity.getCode());
        dto.setFacultyId(entity.getFacultyId());
        dto.setFacultyName(entity.getFacultyName());
        // Set aliases
        dto.setUnitId(entity.getFacultyId());
        dto.setUnitName(entity.getFacultyName());
        dto.setUnitNameEn(entity.getFacultyName()); // Assuming same
        return dto;
    }

    private Course convertCourseToDto(com.yeditepe.courseselector.entity.Course entity) {
        Course dto = new Course();
        dto.setCode(entity.getCode());
        dto.setSection(entity.getSection());
        dto.setName(entity.getName());
        dto.setNameEn(entity.getNameEn());
        dto.setNameTr(entity.getNameTr());
        dto.setCredit(entity.getCredit());
        dto.setEcts(entity.getEcts());
        dto.setFullQuota(entity.getFullQuota());
        dto.setQuota(entity.getQuota());
        dto.setInfo(entity.getInfo());
        dto.setInstructor(entity.getInstructor());
        dto.setDepartmentId(entity.getDepartmentId());
        dto.setDepartmentName(entity.getDepartmentName());
        
        // Convert sections
        if (entity.getSections() != null && !entity.getSections().isEmpty()) {
            List<CourseDetail> details = entity.getSections().stream()
                .map(this::convertSectionToDto)
                .collect(Collectors.toList());
            dto.setDetails(details);
        }
        
        return dto;
    }

    private CourseDetail convertSectionToDto(CourseSection entity) {
        CourseDetail dto = new CourseDetail();
        dto.setDay(entity.getDay());
        dto.setStartHour(entity.getStartTime());
        dto.setEndHour(entity.getEndTime());
        dto.setRoomFloor(entity.getBuilding());
        dto.setRoomName(entity.getRoom());
        dto.setType(entity.getType());
        dto.setFullName(null); // Not stored in entity
        dto.setTypeShort(null); // Not stored in entity
        dto.setNameShort(null); // Not stored in entity
        return dto;
    }
}
