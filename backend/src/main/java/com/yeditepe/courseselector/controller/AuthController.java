package com.yeditepe.courseselector.controller;

import com.yeditepe.courseselector.dto.*;
import com.yeditepe.courseselector.entity.RefreshToken;
import com.yeditepe.courseselector.entity.User;
import com.yeditepe.courseselector.security.JwtUtils;
import com.yeditepe.courseselector.service.RefreshTokenService;
import com.yeditepe.courseselector.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         JwtUtils jwtUtils,
                         RefreshTokenService refreshTokenService,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update last login
            userService.updateLastLogin(user.getUsername());

            // Create refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    roles
            ));
        } catch (Exception e) {
            // Debug: check if user exists and password hash
            try {
                var userOpt = userService.findByUsername(loginRequest.getUsername());
                if (userOpt.isPresent()) {
                    var user = userOpt.get();
                    System.out.println("LOGIN DEBUG: User found: " + user.getUsername() + ", active: " + user.getIsActive() + ", roles: " + user.getRoles());
                    System.out.println("LOGIN DEBUG: Password hash starts with: " + (user.getPassword() != null ? user.getPassword().substring(0, Math.min(10, user.getPassword().length())) : "NULL"));
                    System.out.println("LOGIN DEBUG: Password hash length: " + (user.getPassword() != null ? user.getPassword().length() : 0));
                    boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
                    System.out.println("LOGIN DEBUG: BCrypt matches: " + matches);
                    System.out.println("LOGIN DEBUG: Input password: '" + loginRequest.getPassword() + "'");
                } else {
                    System.out.println("LOGIN DEBUG: User NOT FOUND: " + loginRequest.getUsername());
                }
            } catch (Exception ex) {
                System.out.println("LOGIN DEBUG: Error checking user: " + ex.getMessage());
            }
            e.printStackTrace();
            System.out.println("Login failed: " + e.getClass().getName() + ": " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid username or password"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(token -> {
                    refreshTokenService.verifyExpiration(token);
                    User user = token.getUser();
                    String newAccessToken = jwtUtils.generateTokenFromUsername(user.getUsername());
                    List<String> roles = user.getRoles().stream().collect(Collectors.toList());

                    return ResponseEntity.ok((Object) new JwtResponse(
                            newAccessToken,
                            requestRefreshToken,
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            roles
                    ));
                })
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new MessageResponse("Refresh token is not valid!")));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setStudentId(signUpRequest.getStudentId());
        user.setDepartment(signUpRequest.getDepartment());

        userService.createUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody(required = false) TokenRefreshRequest request) {
        try {
            if (request != null && request.getRefreshToken() != null) {
                refreshTokenService.deleteByToken(request.getRefreshToken());
            }
            return ResponseEntity.ok(new MessageResponse("Logged out successfully!"));
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Logged out successfully!"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody java.util.Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Current password and new password are required"));
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("New password must be at least 6 characters"));
            }

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!userService.checkPassword(user, currentPassword)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Current password is incorrect"));
            }

            userService.changePassword(user, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profileUpdate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (profileUpdate.getFirstName() != null) {
                user.setFirstName(profileUpdate.getFirstName());
            }
            if (profileUpdate.getLastName() != null) {
                user.setLastName(profileUpdate.getLastName());
            }
            if (profileUpdate.getEmail() != null) {
                user.setEmail(profileUpdate.getEmail());
            }
            if (profileUpdate.getStudentId() != null) {
                user.setStudentId(profileUpdate.getStudentId());
            }
            if (profileUpdate.getDepartment() != null) {
                user.setDepartment(profileUpdate.getDepartment());
            }

            User updated = userService.saveUser(user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
