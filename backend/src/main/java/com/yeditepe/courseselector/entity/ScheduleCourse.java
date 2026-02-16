package com.yeditepe.courseselector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ScheduleCourse entity representing a course within a saved schedule
 */
@Entity
@Table(name = "schedule_courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private SavedSchedule schedule;

    @NotBlank
    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_name", length = 200)
    private String courseName;

    @NotNull
    @Column(name = "section", nullable = false)
    private Integer section;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "ects")
    private Integer ects;

    @Column(name = "instructor", length = 200)
    private String instructor;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "time_slots", columnDefinition = "TEXT")
    private String timeSlots; // JSON format: [{"day":"MON","start":"09:00","end":"10:30","room":"C104"}]
}
