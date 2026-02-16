package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResult {
    private boolean success;
    private String message;
    private List<Course> selectedCourses;
    private Map<String, List<ScheduleSlot>> weeklySchedule;
    private int totalCredits;
    private int totalEcts;
    private ScheduleMetrics metrics;
    private List<String> excludedCourses; // Courses that couldn't fit in the schedule
    private boolean hasOverlap; // Çakışma var mı
    private int overlapMinutes; // Toplam çakışma süresi (dakika)
    private List<OverlapDetail> overlapDetails; // Çakışma detayları
}
