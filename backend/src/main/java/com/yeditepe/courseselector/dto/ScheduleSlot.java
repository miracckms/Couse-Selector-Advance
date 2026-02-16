package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlot {
    private String courseCode;
    private String courseName;
    private String section;
    private String type;
    private String startHour;
    private String endHour;
    private String instructor;
    private String room;
}
