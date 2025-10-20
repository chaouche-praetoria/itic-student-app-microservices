package cloud.praetoria.auth.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.auth.dtos.CreatePasswordRequestDto;
import cloud.praetoria.auth.dtos.LoginRequestDto;
import cloud.praetoria.auth.dtos.LoginResponseDto;
import cloud.praetoria.auth.dtos.RefreshTokenRequestDto;
import cloud.praetoria.auth.dtos.UserInfoDto;
import cloud.praetoria.auth.dtos.responses.ErrorResponse;
import cloud.praetoria.auth.exceptions.ApiException;
import cloud.praetoria.auth.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    // ==================== INSCRIPTION ====================

    /**
     * Inscription d'un étudiant
     * Endpoint : POST /api/auth/register-student
     */

    @PostMapping("/register-student")
    public ResponseEntity<LoginResponseDto> registerStudent(@Valid @RequestBody CreatePasswordRequestDto request) {
        request.setYpareoLogin(request.getYpareoLogin().toUpperCase().trim());
        return handleRegistration(request, "student", () -> authService.registerStudent(request));
    }

    /**
     * Inscription d'un formateur
     * Endpoint : POST /api/auth/register-teacher
     */
    @Operation(
            summary = "Register a new teacher",
            description = "Create a new teacher account using a Ypareo login and password. The Ypareo login is normalized to uppercase and trimmed before processing. On success returns 201 Created with authentication tokens and user details. Validation errors or business rules (e.g. teacher already exists) return a structured ErrorResponse."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teacher registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Teacher already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Erreur de requête (ex: formateur déjà inscrit)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register-teacher")
    public ResponseEntity<LoginResponseDto> registerTeacher(@Valid @RequestBody CreatePasswordRequestDto request) {
        request.setYpareoLogin(request.getYpareoLogin().toUpperCase().trim());
        return handleRegistration(request, "teacher", () -> authService.registerTeacher(request));
    }

    // ==================== CONNEXION ====================

    /**
     * Connexion (student ou teacher)
     * Endpoint : POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Login request received for Ypareo Login: {}", request.getYpareoLogin());

        request.setYpareoLogin(request.getYpareoLogin().toUpperCase().trim());

        try {
            LoginResponseDto response = authService.authenticateUser(request);
            log.info("User authenticated successfully: {}", request.getYpareoLogin());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Login failed for Ypareo Login: {} - {}", request.getYpareoLogin(), e.getMessage());
            throw e;
        }
    }

    // ==================== GESTION DES TOKENS ====================

    /**
     * Rafraîchir le token d'accès
     * Endpoint : POST /api/auth/refresh
     */
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

    // ==================== DÉCONNEXION ====================

    /**
     * Déconnexion (révoque le refresh token)
     * Endpoint : POST /api/auth/logout
     */
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

    /**
     * Déconnexion de tous les appareils
     * Endpoint : POST /api/auth/logout-all?ypareoId=xxx
     */
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


    // ==================== INFORMATIONS UTILISATEUR ====================

    /**
     * Récupérer les informations de l'utilisateur connecté
     * Endpoint : GET /api/auth/current-user?ypareoId=xxx
     */
    @GetMapping("/current-user")
    public ResponseEntity<UserInfoDto> getCurrentUser(@RequestParam String ypareoId) {
        log.info("Getting user info for: {}", ypareoId);

        try {
            UserInfoDto userInfo = authService.getUserInfo(ypareoId);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("Failed to get user info for: {}", ypareoId, e);
            throw e;
        }
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Vérifier la santé du service
     * Endpoint : GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "auth-service",
                "timestamp", System.currentTimeMillis(),
                "message", "Auth Service is running properly"
        ));
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Méthode générique pour gérer l'inscription (factorisation)
     */
    private ResponseEntity<LoginResponseDto> handleRegistration(
            CreatePasswordRequestDto request,
            String userType,
            RegistrationSupplier registrationSupplier) {

        log.info("{} registration request received for Ypareo Login: {}",
                userType.substring(0, 1).toUpperCase() + userType.substring(1),
                request.getYpareoLogin());

        try {
            LoginResponseDto response = registrationSupplier.register();
            log.info("{} registered successfully: {}",
                    userType.substring(0, 1).toUpperCase() + userType.substring(1),
                    request.getYpareoLogin());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ApiException ae) {
            log.info("Business error during registration for {}: {}", request.getYpareoLogin(), ae.getMessage());
            throw ae;
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed - Invalid data: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            log.warn("Registration failed - {} already exists: {}", userType, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration for: {}", request.getYpareoLogin(), e);
            throw new RuntimeException("Registration failed. Please try again later.");
        }
    }

    @FunctionalInterface
    private interface RegistrationSupplier {
        LoginResponseDto register();
    }
}