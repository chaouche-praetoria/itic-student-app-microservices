package cloud.praetoria.auth.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.auth.dtos.CreatePasswordRequestDto;
import cloud.praetoria.auth.dtos.LoginRequestDto;
import cloud.praetoria.auth.dtos.LoginResponseDto;
import cloud.praetoria.auth.dtos.RefreshTokenRequestDto;
import cloud.praetoria.auth.dtos.UserInfo;
import cloud.praetoria.auth.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody CreatePasswordRequestDto request) {
        log.info("Registration request received for Ypareo Login: {}", request.getYpareoLogin());
        
        try {
        	LoginResponseDto response = authService.registerStudent(request);
            log.info("Student registered successfully: {}", request.getYpareoLogin());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed - Invalid data: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            log.warn("Registration failed - Student already exists: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration for: {}", request.getYpareoLogin(), e);
            throw new RuntimeException("Registration failed. Please try again later.");
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Login request received for Ypareo ID: {}", request.getYpareoLogin());
        
        try {
        	LoginResponseDto response = authService.authenticateStudent(request);
            log.info("Student authenticated successfully: {}", request.getYpareoLogin());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Login failed for Ypareo ID: {} - {}", request.getYpareoLogin(), e.getMessage());
            throw e;
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Token refresh request received");
        
        try {
        	LoginResponseDto response = authService.refreshAccessToken(request);
            log.info("Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw e;
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Logout request received");
        
        try {
            authService.logout(request.getRefreshToken());
            log.info("User logged out successfully");
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("message", "Logout completed"));
        }
    }
    
    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutFromAllDevices(@RequestParam String ypareoId) {
        log.info("Logout from all devices request for: {}", ypareoId);
        
        try {
            authService.logoutFromAllDevices(ypareoId);
            log.info("User logged out from all devices: {}", ypareoId);
            return ResponseEntity.ok(Map.of("message", "Logged out from all devices successfully"));
        } catch (Exception e) {
            log.error("Failed to logout from all devices for: {}", ypareoId, e);
            throw e;
        }
    }
    
    @GetMapping("/check-student/{ypareoId}")
    public ResponseEntity<Map<String, Boolean>> checkStudent(@PathVariable String ypareoId) {
        log.info("Checking if student exists: {}", ypareoId);
        
        boolean exists = authService.studentExists(ypareoId);
        log.info("Student {} exists: {}", ypareoId, exists);
        
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/current-user")
    public ResponseEntity<UserInfo> getCurrentUser(@RequestParam String ypareoId) {
        log.info("Getting user info for: {}", ypareoId);
        
        try {
            UserInfo userInfo = authService.getUserInfo(ypareoId);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("Failed to get user info for: {}", ypareoId, e);
            throw e;
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "auth-service",
            "timestamp", System.currentTimeMillis(),
            "message", "Auth Service is running properly"
        ));
    }
}
