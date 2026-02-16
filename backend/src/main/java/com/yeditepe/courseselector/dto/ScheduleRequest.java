package com.yeditepe.courseselector.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    @NotNull(message = "Season ID is required")
    private Long seasonId;
    
    // Nullable - null or 0 means "all departments"
    private Long departmentId;
    
    @NotEmpty(message = "At least one course code is required")
    private List<String> courseCodes;
    
    // Optional: additional departments to search for courses (e.g., MATH, PHYS, ENG)
    private List<Long> additionalDepartmentIds;
    
    // Optimization mode: "AUTO" or "MANUAL"
    // AUTO: System finds best non-conflicting sections
    // MANUAL: User specifies exact sections
    private String mode = "AUTO";
    
    // For MANUAL mode: list of selected sections
    private List<SelectedSection> selectedSections;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedSection {
        private String code;
        private String section;
    }
}
