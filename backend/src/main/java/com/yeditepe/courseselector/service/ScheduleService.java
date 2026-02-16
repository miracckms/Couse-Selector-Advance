package com.yeditepe.courseselector.service;

import com.yeditepe.courseselector.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private static final List<String> DAYS_ORDER = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    
    // Çakışma toleransı: en fazla 1 çakışma, en fazla 60 dakika
    private static final int MAX_OVERLAP_COUNT = 1;
    private static final int MAX_OVERLAP_MINUTES = 60;

    /**
     * Main entry point that handles both AUTO and MANUAL modes
     */
    public ScheduleResult generateSchedule(List<Course> allCourses, ScheduleRequest request) {
        if ("MANUAL".equalsIgnoreCase(request.getMode())) {
            return generateManualSchedule(allCourses, request.getSelectedSections());
        } else {
            return generateOptimalSchedule(allCourses, request.getCourseCodes());
        }
    }

    /**
     * MANUAL mode: User specifies exact sections
     */
    private ScheduleResult generateManualSchedule(List<Course> allCourses, List<ScheduleRequest.SelectedSection> selectedSections) {
        if (selectedSections == null || selectedSections.isEmpty()) {
            return createErrorResult("Manuel modda en az bir section seçilmelidir.");
        }

        List<Course> selectedCourses = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (ScheduleRequest.SelectedSection sel : selectedSections) {
            Course found = allCourses.stream()
                .filter(c -> c.getCode().equals(sel.getCode()) && 
                            String.valueOf(c.getSection()).equals(sel.getSection()))
                .findFirst()
                .orElse(null);

            if (found != null) {
                selectedCourses.add(found);
            } else {
                notFound.add(sel.getCode() + " (Grup " + sel.getSection() + ")");
            }
        }

        if (!notFound.isEmpty()) {
            return createErrorResult("Şu dersler bulunamadı: " + String.join(", ", notFound));
        }

        // Check for conflicts with tolerance
        List<TimeSlot> allSlots = new ArrayList<>();
        for (Course course : selectedCourses) {
            allSlots.addAll(getTimeSlotsWithCode(course));
        }
        OverlapInfo overlap = calculateOverlap(allSlots);
        
        if (!overlap.isAcceptable()) {
            return createErrorResult("Seçilen derslerde kabul edilemez çakışma var! " +
                "En fazla " + MAX_OVERLAP_COUNT + " çakışma ve " + MAX_OVERLAP_MINUTES + " dakika kabul edilir. " +
                "Mevcut: " + overlap.count + " çakışma, " + overlap.totalMinutes + " dakika.");
        }

        ScheduleMetrics metrics = calculateMetrics(selectedCourses);
        ScheduleResult result = createSuccessResult(selectedCourses, metrics);
        
        if (overlap.count > 0) {
            result.setMessage("⚠️ Program oluşturuldu! " + overlap.count + " çakışma var (" + 
                overlap.totalMinutes + " dakika). Çakışan derslere dikkat edin.");
            result.setHasOverlap(true);
            result.setOverlapMinutes(overlap.totalMinutes);
            result.setOverlapDetails(calculateOverlapDetails(allSlots));
        }
        
        return result;
    }

    /**
     * AUTO mode: System finds best non-conflicting sections
     * Uses backtracking with pruning for efficiency
     */
    public ScheduleResult generateOptimalSchedule(List<Course> allCourses, List<String> requestedCourseCodes) {
        // Group courses by code (each code can have multiple sections)
        // Filter out sections without valid time slots
        Map<String, List<Course>> coursesByCode = allCourses.stream()
                .filter(c -> requestedCourseCodes.contains(c.getCode()))
                .filter(this::hasValidTimeSlots) // Sadece saati olan dersleri al
                .collect(Collectors.groupingBy(Course::getCode));

        // Check which courses have no schedulable sections
        List<String> noScheduleCourses = requestedCourseCodes.stream()
                .filter(code -> !coursesByCode.containsKey(code) || coursesByCode.get(code).isEmpty())
                .filter(code -> allCourses.stream().anyMatch(c -> c.getCode().equals(code))) // Ders var ama saati yok
                .collect(Collectors.toList());
        
        // Check if all requested courses are available
        List<String> missingCourses = requestedCourseCodes.stream()
                .filter(code -> !coursesByCode.containsKey(code) || coursesByCode.get(code).isEmpty())
                .filter(code -> allCourses.stream().noneMatch(c -> c.getCode().equals(code))) // Ders hiç yok
                .collect(Collectors.toList());

        if (!missingCourses.isEmpty()) {
            return createErrorResult("Şu ders kodları bulunamadı: " + String.join(", ", missingCourses));
        }

        // Filter out courses with no valid time slots from the request
        List<String> availableCodes = requestedCourseCodes.stream()
                .filter(code -> coursesByCode.containsKey(code) && !coursesByCode.get(code).isEmpty())
                .collect(Collectors.toList());
        
        if (availableCodes.isEmpty()) {
            return createErrorResult("Seçilen derslerin hiçbirinde ders saati bulunamadı. " +
                "Staj, proje gibi dersler programa eklenemez.");
        }
        
        // Use backtracking to find valid schedules efficiently
        List<ScoredSchedule> validSchedules = new ArrayList<>();
        backtrackFindSchedules(coursesByCode, availableCodes, 0, new ArrayList<>(), new ArrayList<>(), validSchedules, 10);
        
        if (!validSchedules.isEmpty()) {
            // Sort by: 1) overlap count, 2) overlap minutes, 3) score
            // Önce çakışması olmayanlar, sonra az çakışması olanlar
            validSchedules.sort(Comparator
                .comparingInt((ScoredSchedule s) -> s.overlapCount)
                .thenComparingInt(s -> s.totalOverlapMinutes)
                .thenComparingInt(s -> s.metrics.getScore()));
            
            ScoredSchedule best = validSchedules.get(0);
            ScheduleResult result = createSuccessResult(best.courses, best.metrics);
            
            StringBuilder message = new StringBuilder();
            
            // Saati olmayan dersler varsa bilgilendir
            if (!noScheduleCourses.isEmpty()) {
                message.append("⚠️ Saati olmayan " + noScheduleCourses.size() + " ders çıkarıldı: " + 
                    String.join(", ", noScheduleCourses) + ". ");
                // Bu dersleri excludedCourses'a ekle
                List<String> excluded = result.getExcludedCourses() != null ? 
                    new ArrayList<>(result.getExcludedCourses()) : new ArrayList<>();
                excluded.addAll(noScheduleCourses);
                result.setExcludedCourses(excluded);
            }
            
            // Çakışma varsa kullanıcıyı bilgilendir ve detayları ekle
            if (best.overlapCount > 0) {
                message.append("⚠️ " + best.overlapCount + " çakışma var (" + 
                    best.totalOverlapMinutes + " dakika). ");
                result.setHasOverlap(true);
                result.setOverlapMinutes(best.totalOverlapMinutes);
                
                // Çakışma detaylarını hesapla
                List<TimeSlot> allSlots = new ArrayList<>();
                for (Course course : best.courses) {
                    allSlots.addAll(getTimeSlotsWithCode(course));
                }
                result.setOverlapDetails(calculateOverlapDetails(allSlots));
            }
            
            if (message.length() > 0) {
                result.setMessage(message.toString().trim());
            }
            
            return result;
        }

        // No complete solution - try greedy approach for partial solution
        ScheduleResult partialResult = findPartialScheduleGreedy(coursesByCode, availableCodes);
        
        // Saati olmayan dersleri de excluded listesine ekle
        if (!noScheduleCourses.isEmpty() && partialResult.isSuccess()) {
            List<String> excluded = partialResult.getExcludedCourses() != null ? 
                new ArrayList<>(partialResult.getExcludedCourses()) : new ArrayList<>();
            excluded.addAll(noScheduleCourses);
            partialResult.setExcludedCourses(excluded);
            
            String currentMessage = partialResult.getMessage() != null ? partialResult.getMessage() : "";
            partialResult.setMessage("⚠️ Saati olmayan dersler çıkarıldı: " + 
                String.join(", ", noScheduleCourses) + ". " + currentMessage);
        }
        
        return partialResult;
    }
    
    /**
     * Backtracking algorithm with overlap tolerance
     * En fazla 1 çakışma ve en fazla 60 dakika çakışma kabul edilir
     */
    private void backtrackFindSchedules(Map<String, List<Course>> coursesByCode, 
                                        List<String> courseCodes, 
                                        int index,
                                        List<Course> currentSchedule,
                                        List<TimeSlot> currentSlots,
                                        List<ScoredSchedule> validSchedules,
                                        int maxResults) {
        // Found enough solutions
        if (validSchedules.size() >= maxResults) {
            return;
        }
        
        // Successfully scheduled all courses
        if (index == courseCodes.size()) {
            OverlapInfo overlap = calculateOverlap(currentSlots);
            ScheduleMetrics metrics = calculateMetrics(currentSchedule);
            validSchedules.add(new ScoredSchedule(new ArrayList<>(currentSchedule), metrics, 
                                                   overlap.count, overlap.totalMinutes));
            return;
        }
        
        String code = courseCodes.get(index);
        List<Course> sections = coursesByCode.get(code);
        
        if (sections == null || sections.isEmpty()) {
            return;
        }
        
        // Try each section for this course
        for (Course section : sections) {
            List<TimeSlot> sectionSlots = getTimeSlotsWithCode(section);
            
            // Calculate overlap with current schedule
            List<TimeSlot> testSlots = new ArrayList<>(currentSlots);
            testSlots.addAll(sectionSlots);
            OverlapInfo overlap = calculateOverlap(testSlots);
            
            // Check if overlap is within acceptable limits
            if (overlap.isAcceptable()) {
                // Acceptable - add and recurse
                currentSchedule.add(section);
                currentSlots.addAll(sectionSlots);
                
                backtrackFindSchedules(coursesByCode, courseCodes, index + 1, 
                                       currentSchedule, currentSlots, validSchedules, maxResults);
                
                // Backtrack
                currentSchedule.remove(currentSchedule.size() - 1);
                currentSlots.subList(currentSlots.size() - sectionSlots.size(), currentSlots.size()).clear();
            }
        }
    }
    
    /**
     * Çakışma miktarını hesaplar
     */
    private OverlapInfo calculateOverlap(List<TimeSlot> slots) {
        int overlapCount = 0;
        int totalOverlapMinutes = 0;
        Set<String> overlappingPairs = new HashSet<>();
        
        for (int i = 0; i < slots.size(); i++) {
            for (int j = i + 1; j < slots.size(); j++) {
                TimeSlot slot1 = slots.get(i);
                TimeSlot slot2 = slots.get(j);
                
                // Aynı dersin farklı saatlerini sayma
                if (slot1.courseCode != null && slot1.courseCode.equals(slot2.courseCode)) {
                    continue;
                }
                
                int overlapMinutes = slot1.getOverlapMinutes(slot2);
                if (overlapMinutes > 0) {
                    // Aynı çakışmayı birden fazla sayma
                    String pairKey = slot1.courseCode + "-" + slot2.courseCode;
                    String pairKeyReverse = slot2.courseCode + "-" + slot1.courseCode;
                    
                    if (!overlappingPairs.contains(pairKey) && !overlappingPairs.contains(pairKeyReverse)) {
                        overlappingPairs.add(pairKey);
                        overlapCount++;
                        totalOverlapMinutes += overlapMinutes;
                    }
                }
            }
        }
        
        return new OverlapInfo(overlapCount, totalOverlapMinutes);
    }
    
    /**
     * Dersin geçerli ders saati olup olmadığını kontrol eder
     * Staj, proje gibi dersler saatsiz olabilir
     */
    private boolean hasValidTimeSlots(Course course) {
        if (course.getDetails() == null || course.getDetails().isEmpty()) {
            return false;
        }
        
        // En az bir geçerli zaman dilimi olmalı
        return course.getDetails().stream().anyMatch(detail -> 
            detail.getDay() != null && 
            detail.getStartHour() != null && 
            detail.getEndHour() != null &&
            !detail.getStartHour().isEmpty() &&
            !detail.getEndHour().isEmpty()
        );
    }
    
    /**
     * Ders koduyla birlikte TimeSlot listesi oluşturur
     */
    private List<TimeSlot> getTimeSlotsWithCode(Course course) {
        List<TimeSlot> slots = new ArrayList<>();
        if (course.getDetails() == null) return slots;
        
        for (CourseDetail detail : course.getDetails()) {
            if (detail.getDay() != null && detail.getStartHour() != null && detail.getEndHour() != null) {
                slots.add(new TimeSlot(
                    detail.getDay(),
                    parseTime(detail.getStartHour()),
                    parseTime(detail.getEndHour()),
                    course.getCode()
                ));
            }
        }
        return slots;
    }
    
    /**
     * Greedy approach for partial solution when complete solution doesn't exist
     * Çakışma toleransını da dikkate alır
     */
    private ScheduleResult findPartialScheduleGreedy(Map<String, List<Course>> coursesByCode, List<String> courseCodes) {
        List<Course> selectedCourses = new ArrayList<>();
        List<TimeSlot> usedSlots = new ArrayList<>();
        List<String> includedCodes = new ArrayList<>();
        List<String> excludedCodes = new ArrayList<>();
        
        // Sort courses by number of sections (fewer sections = harder to schedule, do first)
        List<String> sortedCodes = new ArrayList<>(courseCodes);
        sortedCodes.sort(Comparator.comparingInt(code -> 
            coursesByCode.getOrDefault(code, Collections.emptyList()).size()));
        
        for (String code : sortedCodes) {
            List<Course> sections = coursesByCode.get(code);
            if (sections == null) continue;
            
            // Find best fitting section for this course
            Course bestSection = null;
            List<TimeSlot> bestSlots = null;
            int bestScore = Integer.MAX_VALUE;
            OverlapInfo bestOverlap = null;
            
            for (Course section : sections) {
                List<TimeSlot> sectionSlots = getTimeSlotsWithCode(section);
                
                // Calculate overlap with current schedule
                List<TimeSlot> testSlots = new ArrayList<>(usedSlots);
                testSlots.addAll(sectionSlots);
                OverlapInfo overlap = calculateOverlap(testSlots);
                
                // Check if overlap is acceptable
                if (overlap.isAcceptable()) {
                    // Calculate how well this fits (prefer sections with less overlap and leave more room)
                    int score = calculateFitScore(sectionSlots, usedSlots) + (overlap.totalMinutes * 10);
                    if (score < bestScore) {
                        bestScore = score;
                        bestSection = section;
                        bestSlots = sectionSlots;
                        bestOverlap = overlap;
                    }
                }
            }
            
            if (bestSection != null) {
                selectedCourses.add(bestSection);
                usedSlots.addAll(bestSlots);
                includedCodes.add(code);
            } else {
                excludedCodes.add(code);
            }
        }
        
        if (selectedCourses.isEmpty()) {
            return createErrorResult("Hiçbir ders kombinasyonu oluşturulamadı.");
        }
        
        OverlapInfo finalOverlap = calculateOverlap(usedSlots);
        ScheduleMetrics metrics = calculateMetrics(selectedCourses);
        ScheduleResult result = createSuccessResult(selectedCourses, metrics);
        
        StringBuilder message = new StringBuilder();
        
        if (finalOverlap.count > 0) {
            message.append("⚠️ " + finalOverlap.count + " çakışma var (" + finalOverlap.totalMinutes + " dk). ");
            result.setHasOverlap(true);
            result.setOverlapMinutes(finalOverlap.totalMinutes);
            result.setOverlapDetails(calculateOverlapDetails(usedSlots));
        }
        
        if (!excludedCodes.isEmpty()) {
            message.append("Tüm dersler sığmadı! " + excludedCodes.size() + " ders çıkarıldı: " + 
                String.join(", ", excludedCodes) + ". ");
            result.setExcludedCourses(excludedCodes);
        }
        
        if (message.length() > 0) {
            message.append(selectedCourses.size() + " derslik program oluşturuldu.");
            result.setMessage(message.toString());
        }
        
        return result;
    }
    
    private List<TimeSlot> getTimeSlots(Course course) {
        return getTimeSlotsWithCode(course);
    }
    
    private int calculateFitScore(List<TimeSlot> sectionSlots, List<TimeSlot> usedSlots) {
        // Lower score is better
        int score = 0;
        for (TimeSlot slot : sectionSlots) {
            // Prefer afternoon slots
            if (slot.start.isBefore(LocalTime.of(10, 0))) {
                score += 10;
            }
            // Prefer continuous blocks (check proximity to existing slots)
            for (TimeSlot used : usedSlots) {
                if (slot.day.equals(used.day)) {
                    long gap = Math.abs(java.time.Duration.between(slot.end, used.start).toMinutes());
                    if (gap < 60) score -= 5; // Small gap is good
                }
            }
        }
        return score;
    }

    /**
     * Kabul edilemez çakışma olup olmadığını kontrol eder
     * MAX_OVERLAP_COUNT ve MAX_OVERLAP_MINUTES sınırlarını aşarsa true döner
     */
    private boolean hasUnacceptableConflict(List<Course> courses) {
        List<TimeSlot> allSlots = new ArrayList<>();

        for (Course course : courses) {
            allSlots.addAll(getTimeSlotsWithCode(course));
        }

        OverlapInfo overlap = calculateOverlap(allSlots);
        return !overlap.isAcceptable();
    }

    private ScheduleMetrics calculateMetrics(List<Course> courses) {
        Map<String, List<TimeSlot>> slotsByDay = new HashMap<>();

        for (Course course : courses) {
            if (course.getDetails() == null) continue;
            
            for (CourseDetail detail : course.getDetails()) {
                if (detail.getDay() == null || detail.getStartHour() == null || detail.getEndHour() == null) {
                    continue;
                }
                slotsByDay.computeIfAbsent(detail.getDay(), k -> new ArrayList<>())
                    .add(new TimeSlot(
                        detail.getDay(),
                        parseTime(detail.getStartHour()),
                        parseTime(detail.getEndHour())
                    ));
            }
        }

        int totalGaps = 0;
        LocalTime earliestStart = LocalTime.of(23, 59);
        LocalTime latestEnd = LocalTime.of(0, 0);

        for (List<TimeSlot> daySlots : slotsByDay.values()) {
            daySlots.sort(Comparator.comparing(s -> s.start));
            
            for (int i = 0; i < daySlots.size() - 1; i++) {
                LocalTime gapStart = daySlots.get(i).end;
                LocalTime gapEnd = daySlots.get(i + 1).start;
                if (gapEnd.isAfter(gapStart)) {
                    totalGaps += java.time.Duration.between(gapStart, gapEnd).toMinutes();
                }
            }

            for (TimeSlot slot : daySlots) {
                if (slot.start.isBefore(earliestStart)) {
                    earliestStart = slot.start;
                }
                if (slot.end.isAfter(latestEnd)) {
                    latestEnd = slot.end;
                }
            }
        }

        int daysWithClasses = slotsByDay.size();
        
        // Calculate score: prefer fewer gaps, fewer days, later starts
        int score = totalGaps + (daysWithClasses * 100);
        if (earliestStart.isBefore(LocalTime.of(10, 0))) {
            score += 50; // Penalize early classes
        }

        return new ScheduleMetrics(
            totalGaps,
            daysWithClasses,
            earliestStart.format(TIME_FORMATTER),
            latestEnd.format(TIME_FORMATTER),
            score
        );
    }

    private LocalTime parseTime(String time) {
        if (time.contains(":")) {
            String[] parts = time.split(":");
            return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return LocalTime.of(Integer.parseInt(time), 0);
    }

    private ScheduleResult createErrorResult(String message) {
        ScheduleResult result = new ScheduleResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    private ScheduleResult createSuccessResult(List<Course> courses, ScheduleMetrics metrics) {
        ScheduleResult result = new ScheduleResult();
        result.setSuccess(true);
        result.setMessage("Optimal ders programı oluşturuldu!");
        result.setSelectedCourses(courses);
        result.setMetrics(metrics);

        // Calculate totals
        int totalCredits = courses.stream().mapToInt(c -> c.getCredit() != null ? c.getCredit() : 0).sum();
        int totalEcts = courses.stream().mapToInt(c -> c.getEcts() != null ? c.getEcts() : 0).sum();
        result.setTotalCredits(totalCredits);
        result.setTotalEcts(totalEcts);

        // Build weekly schedule map
        Map<String, List<ScheduleSlot>> weeklySchedule = new LinkedHashMap<>();
        for (String day : DAYS_ORDER) {
            weeklySchedule.put(day, new ArrayList<>());
        }

        for (Course course : courses) {
            if (course.getDetails() == null) continue;
            
            for (CourseDetail detail : course.getDetails()) {
                if (detail.getDay() == null) continue;
                
                ScheduleSlot slot = new ScheduleSlot(
                    course.getCode(),
                    course.getNameEn(),
                    course.getSection() != null ? String.valueOf(course.getSection()) : null,
                    detail.getTypeShort(),
                    detail.getStartHour(),
                    detail.getEndHour(),
                    detail.getFullName(),
                    detail.getRoomName()
                );
                weeklySchedule.get(detail.getDay()).add(slot);
            }
        }

        // Sort each day's slots by start time
        for (List<ScheduleSlot> daySlots : weeklySchedule.values()) {
            daySlots.sort(Comparator.comparing(s -> parseTime(s.getStartHour())));
        }

        result.setWeeklySchedule(weeklySchedule);
        return result;
    }

    private static class TimeSlot {
        String day;
        LocalTime start;
        LocalTime end;
        String courseCode; // Hangi derse ait olduğunu takip etmek için

        TimeSlot(String day, LocalTime start, LocalTime end) {
            this.day = day;
            this.start = start;
            this.end = end;
            this.courseCode = null;
        }
        
        TimeSlot(String day, LocalTime start, LocalTime end, String courseCode) {
            this.day = day;
            this.start = start;
            this.end = end;
            this.courseCode = courseCode;
        }

        boolean overlapsWith(TimeSlot other) {
            if (!this.day.equals(other.day)) {
                return false;
            }
            return this.start.isBefore(other.end) && other.start.isBefore(this.end);
        }
        
        /**
         * Çakışma süresini dakika olarak hesaplar
         */
        int getOverlapMinutes(TimeSlot other) {
            if (!this.day.equals(other.day)) {
                return 0;
            }
            
            LocalTime overlapStart = this.start.isAfter(other.start) ? this.start : other.start;
            LocalTime overlapEnd = this.end.isBefore(other.end) ? this.end : other.end;
            
            if (overlapStart.isBefore(overlapEnd)) {
                return (int) java.time.Duration.between(overlapStart, overlapEnd).toMinutes();
            }
            return 0;
        }
    }

    private static class ScoredSchedule {
        List<Course> courses;
        ScheduleMetrics metrics;
        int adjustedScore;
        int overlapCount;
        int totalOverlapMinutes;

        ScoredSchedule(List<Course> courses, ScheduleMetrics metrics, int overlapCount, int totalOverlapMinutes) {
            this.courses = courses;
            this.metrics = metrics;
            this.overlapCount = overlapCount;
            this.totalOverlapMinutes = totalOverlapMinutes;
            // Çakışma varsa score'u artır (daha kötü)
            this.adjustedScore = metrics.getScore() + (overlapCount * 200) + totalOverlapMinutes;
        }
    }
    
    /**
     * Çakışma bilgisini tutan yardımcı sınıf
     */
    private static class OverlapInfo {
        int count;
        int totalMinutes;
        List<OverlapDetail> details;
        
        OverlapInfo(int count, int totalMinutes) {
            this.count = count;
            this.totalMinutes = totalMinutes;
            this.details = new ArrayList<>();
        }
        
        OverlapInfo(int count, int totalMinutes, List<OverlapDetail> details) {
            this.count = count;
            this.totalMinutes = totalMinutes;
            this.details = details;
        }
        
        boolean isAcceptable() {
            return count <= MAX_OVERLAP_COUNT && totalMinutes <= MAX_OVERLAP_MINUTES;
        }
    }
    
    /**
     * Çakışma detaylarını hesaplar
     */
    private List<OverlapDetail> calculateOverlapDetails(List<TimeSlot> slots) {
        List<OverlapDetail> details = new ArrayList<>();
        Set<String> processedPairs = new HashSet<>();
        
        Map<String, String> dayTurkish = Map.of(
            "MON", "Pazartesi",
            "TUE", "Salı", 
            "WED", "Çarşamba",
            "THU", "Perşembe",
            "FRI", "Cuma",
            "SAT", "Cumartesi",
            "SUN", "Pazar"
        );
        
        for (int i = 0; i < slots.size(); i++) {
            for (int j = i + 1; j < slots.size(); j++) {
                TimeSlot slot1 = slots.get(i);
                TimeSlot slot2 = slots.get(j);
                
                // Aynı dersin farklı saatlerini sayma
                if (slot1.courseCode != null && slot1.courseCode.equals(slot2.courseCode)) {
                    continue;
                }
                
                int overlapMinutes = slot1.getOverlapMinutes(slot2);
                if (overlapMinutes > 0) {
                    String pairKey = slot1.courseCode + "-" + slot2.courseCode + "-" + slot1.day;
                    String pairKeyReverse = slot2.courseCode + "-" + slot1.courseCode + "-" + slot1.day;
                    
                    if (!processedPairs.contains(pairKey) && !processedPairs.contains(pairKeyReverse)) {
                        processedPairs.add(pairKey);
                        
                        // Çakışma başlangıç ve bitiş saatlerini hesapla
                        LocalTime overlapStart = slot1.start.isAfter(slot2.start) ? slot1.start : slot2.start;
                        LocalTime overlapEnd = slot1.end.isBefore(slot2.end) ? slot1.end : slot2.end;
                        
                        OverlapDetail detail = new OverlapDetail(
                            slot1.courseCode,
                            slot2.courseCode,
                            slot1.day,
                            dayTurkish.getOrDefault(slot1.day, slot1.day),
                            overlapStart.format(TIME_FORMATTER),
                            overlapEnd.format(TIME_FORMATTER),
                            overlapMinutes
                        );
                        details.add(detail);
                    }
                }
            }
        }
        
        return details;
    }
}
