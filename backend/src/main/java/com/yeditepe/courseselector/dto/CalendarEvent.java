package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {
    private String category;
    private String event;
    private String eventEn;
    private Map<String, Object> startDate;
    private Map<String, Object> endDate;
    private String subCategory;
    private String superCategory;
    private String location;
    private String link;
}
