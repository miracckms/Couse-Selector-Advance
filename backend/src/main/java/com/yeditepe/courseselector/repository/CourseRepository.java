package com.yeditepe.courseselector.repository;

import com.yeditepe.courseselector.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findBySeasonIdAndDepartmentId(Long seasonId, Long departmentId);
    
    List<Course> findBySeasonId(Long seasonId);
    
    Optional<Course> findBySeasonIdAndCodeAndSection(Long seasonId, String code, Integer section);
    
    @Modifying
    @Query("DELETE FROM Course c WHERE c.seasonId = :seasonId AND c.departmentId = :departmentId")
    void deleteBySeasonIdAndDepartmentId(@Param("seasonId") Long seasonId, @Param("departmentId") Long departmentId);
    
    @Modifying
    @Query("DELETE FROM Course c WHERE c.seasonId = :seasonId")
    void deleteBySeasonId(@Param("seasonId") Long seasonId);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.seasonId = :seasonId")
    long countBySeasonId(@Param("seasonId") Long seasonId);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.seasonId = :seasonId AND c.departmentId = :departmentId")
    long countBySeasonIdAndDepartmentId(@Param("seasonId") Long seasonId, @Param("departmentId") Long departmentId);
}
