package com.yeditepe.courseselector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course from Yeditepe API
 * Cached in database, refreshed daily at 06:00
 */
@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_season_id", columnList = "season_id"),
    @Index(name = "idx_season_dept", columnList = "season_id,department_id"),
    @Index(name = "idx_code", columnList = "code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private String code;

    @Column(name = "section_number")
    private Integer section;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_tr")
    private String nameTr;

    private Integer credit;
    private Integer ects;

    @Column(name = "full_quota")
    private Integer fullQuota;

    private Integer quota;

    @Column(columnDefinition = "TEXT")
    private String info;

    private String instructor;

    @Column(name = "department_name")
    private String departmentName;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CourseSection> sections = new ArrayList<>();

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastSyncedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastSyncedAt = LocalDateTime.now();
    }
}
