package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Login request for Yeditepe API
 * Uses "user" and "pass" fields as required by Yeditepe API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeditepeLoginRequest {
    @NotBlank
    private String user;  // Yeditepe API expects "user" not "username"
    
    @NotBlank
    private String pass;  // Yeditepe API expects "pass" not "password"
    
    private boolean rememberMe;
}
