package com.yeditepe.courseselector.repository;

import com.yeditepe.courseselector.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    
    List<CourseSection> findByCourseId(Long courseId);
    
    @Modifying
    @Query("DELETE FROM CourseSection cs WHERE cs.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
