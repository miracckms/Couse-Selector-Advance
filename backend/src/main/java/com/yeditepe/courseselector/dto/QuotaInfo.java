package com.yeditepe.courseselector.dto;

public class QuotaInfo {
    private String code;
    private String nameEn;
    private String nameTr;
    private String section;
    private Integer capacity;       // Total capacity (from API: quota)
    private Integer enrolled;       // Enrolled students (from API: fullQuota)
    private Integer remaining;      // Remaining spots (calculated: capacity - enrolled)
    private String instructor;
    private boolean found;
    private String error;

    public QuotaInfo() {}

    public QuotaInfo(Course course) {
        this.code = course.getCode();
        this.nameEn = course.getNameEn();
        this.nameTr = course.getName();
        this.section = course.getSection() != null ? String.valueOf(course.getSection()) : null;
        // API naming is confusing: fullQuota = enrolled, quota = total capacity
        this.capacity = course.getQuota();           // Total capacity
        this.enrolled = course.getFullQuota();       // Enrolled students
        this.remaining = (capacity != null && enrolled != null) ? capacity - enrolled : null;
        this.instructor = course.getDetails() != null && !course.getDetails().isEmpty() 
            ? course.getDetails().get(0).getFullName() : null;
        this.found = true;
    }

    public static QuotaInfo notFound(String code, String section) {
        QuotaInfo info = new QuotaInfo();
        info.code = code;
        info.section = section;
        info.found = false;
        info.error = "Ders bulunamadı";
        return info;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameTr() {
        return nameTr;
    }

    public void setNameTr(String nameTr) {
        this.nameTr = nameTr;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Integer enrolled) {
        this.enrolled = enrolled;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
