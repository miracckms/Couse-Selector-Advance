package com.yeditepe.courseselector.service;

import com.yeditepe.courseselector.dto.YeditepeLoginRequest;
import com.yeditepe.courseselector.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String username;
    private final String password;
    
    private String currentToken;
    private long tokenExpirationTime;
    private final ReentrantLock tokenLock = new ReentrantLock();

    public AuthService(RestTemplate restTemplate,
                       @Value("${yeditepe.api.base-url}") String baseUrl,
                       @Value("${yeditepe.auth.username}") String username,
                       @Value("${yeditepe.auth.password}") String password) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    public String getValidToken() {
        tokenLock.lock();
        try {
            // Check if token is still valid (with 5 minute buffer)
            if (currentToken != null && System.currentTimeMillis() < (tokenExpirationTime - 300000)) {
                return currentToken;
            }
            
            // Token is expired or doesn't exist, get a new one
            log.info("Token expired or not found, logging in...");
            return login();
        } finally {
            tokenLock.unlock();
        }
    }

    private String login() {
        String url = baseUrl + "/auth/a7login";
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Log credentials being used (without password in production!)
            log.info("Attempting login to Yeditepe API with username: {}", username);
            log.debug("API URL: {}", url);
            
            YeditepeLoginRequest loginRequest = new YeditepeLoginRequest(username, password, false);
            HttpEntity<YeditepeLoginRequest> entity = new HttpEntity<>(loginRequest, headers);
            
            log.debug("Request body: username={}, rememberMe=false", username);
            
            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, entity, LoginResponse.class);
            
            log.info("Response status: {}", response.getStatusCode());
            log.debug("Response body: {}", response.getBody());
            
            if (response.getBody() != null && response.getBody().getTa7() != null) {
                currentToken = response.getBody().getTa7();
                tokenExpirationTime = extractExpirationTime(currentToken);
                log.info("✅ Successfully logged in to Yeditepe API, token expires at: {}", tokenExpirationTime);
                return currentToken;
            }
            
            // Check if response has error
            if (response.getBody() != null) {
                log.error("❌ Login response received but no token. Response: {}", response.getBody());
            }
            
            throw new RuntimeException("Login failed: No token received");
        } catch (Exception e) {
            log.error("❌ Login to Yeditepe API failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to login to Yeditepe API: " + e.getMessage(), e);
        }
    }

    private long extractExpirationTime(String token) {
        try {
            // JWT format: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                // Default to 1 hour if can't parse
                return System.currentTimeMillis() + 3600000;
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            // Simple extraction of "exp" field
            int expIndex = payload.indexOf("\"exp\":");
            if (expIndex != -1) {
                int start = expIndex + 6;
                int end = payload.indexOf(",", start);
                if (end == -1) {
                    end = payload.indexOf("}", start);
                }
                String expStr = payload.substring(start, end).trim();
                long expSeconds = Long.parseLong(expStr);
                return expSeconds * 1000; // Convert to milliseconds
            }
            
            // Default to 1 hour if can't extract
            return System.currentTimeMillis() + 3600000;
        } catch (Exception e) {
            log.warn("Could not parse token expiration, defaulting to 1 hour: {}", e.getMessage());
            return System.currentTimeMillis() + 3600000;
        }
    }

    public void invalidateToken() {
        tokenLock.lock();
        try {
            currentToken = null;
            tokenExpirationTime = 0;
        } finally {
            tokenLock.unlock();
        }
    }
}
