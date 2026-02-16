package com.yeditepe.courseselector.repository;

import com.yeditepe.courseselector.entity.AcademicSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicSeasonRepository extends JpaRepository<AcademicSeason, Long> {
    
    Optional<AcademicSeason> findByActive(Integer active);
    
    List<AcademicSeason> findAllByOrderByIdDesc();
    
    @Query("SELECT COUNT(s) FROM AcademicSeason s")
    long countSeasons();
}
