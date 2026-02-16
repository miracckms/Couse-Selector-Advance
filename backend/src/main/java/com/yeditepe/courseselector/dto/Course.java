package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String code;
    private Integer section;  // Changed to Integer for consistency
    private String name;
    private String nameEn;
    private String nameTr;    // Added Turkish name
    private Integer credit;
    private Integer ects;
    private Integer fullQuota;
    private Integer quota;
    private String info;
    private String instructor;  // Added instructor field
    private List<CourseDetail> details;
    private Long departmentId;
    private String departmentName;  // Added department name
}
