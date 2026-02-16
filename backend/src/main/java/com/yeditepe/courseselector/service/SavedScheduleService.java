package com.yeditepe.courseselector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeditepe.courseselector.dto.Course;
import com.yeditepe.courseselector.dto.CourseDetail;
import com.yeditepe.courseselector.entity.SavedSchedule;
import com.yeditepe.courseselector.entity.ScheduleCourse;
import com.yeditepe.courseselector.entity.User;
import com.yeditepe.courseselector.repository.SavedScheduleRepository;
import com.yeditepe.courseselector.repository.ScheduleCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SavedScheduleService {

    private final SavedScheduleRepository scheduleRepository;
    private final ScheduleCourseRepository scheduleCourseRepository;
    private final ObjectMapper objectMapper;

    public SavedScheduleService(SavedScheduleRepository scheduleRepository,
                               ScheduleCourseRepository scheduleCourseRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleCourseRepository = scheduleCourseRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public SavedSchedule saveSchedule(User user, String name, String description, 
                                     Long seasonId, String seasonName, 
                                     List<Course> courses) {
        SavedSchedule schedule = new SavedSchedule();
        schedule.setUser(user);
        schedule.setName(name);
        schedule.setDescription(description);
        schedule.setSeasonId(seasonId);
        schedule.setSeasonName(seasonName);
        schedule.setIsFavorite(false);

        // Calculate totals
        int totalCredits = courses.stream().mapToInt(c -> c.getCredit() != null ? c.getCredit() : 0).sum();
        int totalEcts = courses.stream().mapToInt(c -> c.getEcts() != null ? c.getEcts() : 0).sum();
        schedule.setTotalCredits(totalCredits);
        schedule.setTotalEcts(totalEcts);

        SavedSchedule savedSchedule = scheduleRepository.save(schedule);

        // Save courses
        Set<ScheduleCourse> scheduleCourses = new HashSet<>();
        for (Course course : courses) {
            ScheduleCourse sc = new ScheduleCourse();
            sc.setSchedule(savedSchedule);
            sc.setCourseCode(course.getCode());
            sc.setCourseName(course.getNameEn());
            sc.setSection(course.getSection());
            sc.setCredits(course.getCredit());
            sc.setEcts(course.getEcts());
            sc.setInstructor(course.getInstructor());
            sc.setDepartmentId(course.getDepartmentId());
            sc.setDepartmentName(course.getDepartmentName());

            // Convert time slots to JSON
            try {
                String timeSlotsJson = objectMapper.writeValueAsString(course.getDetails());
                sc.setTimeSlots(timeSlotsJson);
            } catch (Exception e) {
                sc.setTimeSlots("[]");
            }

            scheduleCourses.add(sc);
        }
        
        scheduleCourseRepository.saveAll(scheduleCourses);
        savedSchedule.setCourses(scheduleCourses);
        
        return savedSchedule;
    }

    public List<SavedSchedule> getUserSchedules(User user) {
        return scheduleRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    public List<SavedSchedule> getUserSchedulesBySeason(User user, Long seasonId) {
        return scheduleRepository.findByUserAndSeasonIdOrderByUpdatedAtDesc(user, seasonId);
    }

    public Optional<SavedSchedule> getScheduleById(Long id, User user) {
        return scheduleRepository.findByIdAndUser(id, user);
    }

    public List<SavedSchedule> getFavoriteSchedules(User user) {
        return scheduleRepository.findFavoritesByUser(user);
    }

    @Transactional
    public void deleteSchedule(Long id, User user) {
        SavedSchedule schedule = scheduleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        scheduleRepository.delete(schedule);
    }

    @Transactional
    public SavedSchedule toggleFavorite(Long id, User user) {
        SavedSchedule schedule = scheduleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setIsFavorite(!schedule.getIsFavorite());
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public SavedSchedule updateSchedule(Long id, User user, String name, String description) {
        SavedSchedule schedule = scheduleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setName(name);
        schedule.setDescription(description);
        return scheduleRepository.save(schedule);
    }

    public Long getScheduleCount(User user) {
        return scheduleRepository.countByUser(user);
    }

    /**
     * Get full course details for a saved schedule
     */
    public List<Course> getScheduleCourses(SavedSchedule schedule) {
        List<ScheduleCourse> scheduleCourses = scheduleCourseRepository.findBySchedule(schedule);
        
        return scheduleCourses.stream().map(sc -> {
            Course course = new Course();
            course.setCode(sc.getCourseCode());
            course.setNameEn(sc.getCourseName());
            course.setSection(sc.getSection());
            course.setCredit(sc.getCredits());
            course.setEcts(sc.getEcts());
            course.setInstructor(sc.getInstructor());
            course.setDepartmentId(sc.getDepartmentId());
            course.setDepartmentName(sc.getDepartmentName());

            // Parse time slots from JSON
            try {
                if (sc.getTimeSlots() != null && !sc.getTimeSlots().isEmpty()) {
                    CourseDetail[] details = objectMapper.readValue(sc.getTimeSlots(), CourseDetail[].class);
                    course.setDetails(Arrays.asList(details));
                }
            } catch (Exception e) {
                course.setDetails(new ArrayList<>());
            }

            return course;
        }).collect(Collectors.toList());
    }
}
