package com.yeditepe.courseselector.repository;

import com.yeditepe.courseselector.entity.ScheduleCourse;
import com.yeditepe.courseselector.entity.SavedSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleCourseRepository extends JpaRepository<ScheduleCourse, Long> {
    
    List<ScheduleCourse> findBySchedule(SavedSchedule schedule);
    
    void deleteBySchedule(SavedSchedule schedule);
}
