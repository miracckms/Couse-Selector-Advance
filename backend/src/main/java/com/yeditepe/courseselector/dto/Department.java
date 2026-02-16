package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private Long id;
    private String name;
    private String nameEn;
    private String nameTr; // Added
    private String code; // Added
    private String unitName;
    private String unitNameEn;
    private Long unitId;
    private Long facultyId; // Added (alias for unitId)
    private String facultyName; // Added (alias for unitName)
}
