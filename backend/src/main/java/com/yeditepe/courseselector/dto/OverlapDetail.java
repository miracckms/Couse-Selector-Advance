package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverlapDetail {
    private String course1Code;
    private String course2Code;
    private String day;
    private String dayTurkish;
    private String overlapStart;
    private String overlapEnd;
    private int overlapMinutes;
}
