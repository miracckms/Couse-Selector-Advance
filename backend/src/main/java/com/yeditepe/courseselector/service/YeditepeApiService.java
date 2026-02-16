package com.yeditepe.courseselector.service;

import com.yeditepe.courseselector.dto.AcademicSeason;
import com.yeditepe.courseselector.dto.Course;
import com.yeditepe.courseselector.dto.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class YeditepeApiService {

    private static final Logger log = LoggerFactory.getLogger(YeditepeApiService.class);

    private final RestTemplate restTemplate;
    private final AuthService authService;
    private final String baseUrl;

    public YeditepeApiService(RestTemplate restTemplate, 
                              AuthService authService,
                              @Value("${yeditepe.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.authService = authService;
        this.baseUrl = baseUrl;
    }

    public List<AcademicSeason> getAcademicSeasons() {
        String url = baseUrl + "/common/academic-seasons-limited/true";
        return executeWithRetry(() -> {
            HttpEntity<?> entity = createHttpEntity();
            ResponseEntity<List<AcademicSeason>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<AcademicSeason>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        }, "academic seasons");
    }

    public List<Course> getCourses(Long seasonId, Long departmentId) {
        String url = baseUrl + "/common/dep-courses/true/" + seasonId + "/" + departmentId;
        return executeWithRetry(() -> {
            HttpEntity<?> entity = createHttpEntity();
            ResponseEntity<List<Course>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Course>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        }, "courses");
    }

    public List<Course> getAllCourses(Long seasonId) {
        log.info("Fetching courses from all departments for season {}", seasonId);
        
        // First, get all departments
        List<Department> departments = getDepartments();
        log.info("Found {} departments, fetching courses in parallel...", departments.size());
        
        // Fetch courses from all departments in parallel
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            List<CompletableFuture<List<Course>>> futures = departments.stream()
                .map(dept -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return getCourses(seasonId, dept.getId());
                    } catch (Exception e) {
                        log.warn("Failed to fetch courses for department {}: {}", dept.getName(), e.getMessage());
                        return Collections.<Course>emptyList();
                    }
                }, executor))
                .collect(Collectors.toList());
            
            // Wait for all futures and combine results
            List<Course> allCourses = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
            
            log.info("Fetched {} total courses from all departments", allCourses.size());
            return allCourses;
        } finally {
            executor.shutdown();
        }
    }

    public List<Department> getDepartments() {
        String url = baseUrl + "/common/course-deps-limited/true/1";
        return executeWithRetry(() -> {
            HttpEntity<?> entity = createHttpEntity();
            ResponseEntity<List<Department>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Department>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        }, "departments");
    }

    private <T> T executeWithRetry(ApiCall<T> call, String resourceName) {
        try {
            return call.execute();
        } catch (HttpClientErrorException.Unauthorized e) {
            // Token expired, invalidate and retry with new token
            log.warn("Token expired, refreshing and retrying...");
            authService.invalidateToken();
            try {
                return call.execute();
            } catch (Exception retryEx) {
                throw new RuntimeException("Failed to fetch " + resourceName + " after token refresh: " + retryEx.getMessage(), retryEx);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch " + resourceName + ": " + e.getMessage(), e);
        }
    }

    private HttpEntity<?> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        String token = authService.getValidToken();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(headers);
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T execute();
    }
}
