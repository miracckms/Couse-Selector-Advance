package com.yeditepe.courseselector.repository;

import com.yeditepe.courseselector.entity.SavedSchedule;
import com.yeditepe.courseselector.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedScheduleRepository extends JpaRepository<SavedSchedule, Long> {
    
    List<SavedSchedule> findByUserOrderByUpdatedAtDesc(User user);
    
    List<SavedSchedule> findByUserAndSeasonIdOrderByUpdatedAtDesc(User user, Long seasonId);
    
    Optional<SavedSchedule> findByIdAndUser(Long id, User user);
    
    @Query("SELECT s FROM SavedSchedule s WHERE s.user = :user AND s.isFavorite = true ORDER BY s.updatedAt DESC")
    List<SavedSchedule> findFavoritesByUser(@Param("user") User user);
    
    Long countByUser(User user);
}
