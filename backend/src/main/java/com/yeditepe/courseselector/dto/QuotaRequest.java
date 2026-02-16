package com.yeditepe.courseselector.dto;

import java.util.List;

public class QuotaRequest {
    private Long seasonId;
    private List<CourseIdentifier> courses;

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public List<CourseIdentifier> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseIdentifier> courses) {
        this.courses = courses;
    }

    public static class CourseIdentifier {
        private String code;
        private String section;
        private Long departmentId;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public Long getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(Long departmentId) {
            this.departmentId = departmentId;
        }
    }
}
