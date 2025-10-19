package cloud.praetoria.auth.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.auth.dtos.CreatePasswordRequestDto;
import cloud.praetoria.auth.dtos.LoginRequestDto;
import cloud.praetoria.auth.dtos.LoginResponseDto;
import cloud.praetoria.auth.dtos.RefreshTokenRequestDto;
import cloud.praetoria.auth.dtos.StudentInfoDto;
import cloud.praetoria.auth.dtos.TrainerInfoDto;
import cloud.praetoria.auth.dtos.UserInfoDto;
import cloud.praetoria.auth.dtos.YpareoUserInfo;
import cloud.praetoria.auth.entities.RefreshToken;
import cloud.praetoria.auth.entities.Role;
import cloud.praetoria.auth.entities.User;
import cloud.praetoria.auth.enums.RoleName;
import cloud.praetoria.auth.repositories.RefreshTokenRepository;
import cloud.praetoria.auth.repositories.RoleRepository;
import cloud.praetoria.auth.repositories.UserRepository;
import cloud.praetoria.auth.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final YpareoServiceClient ypareoServiceClient;
    private final RoleRepository roleRepository;
    private final UserUpdateService userUpdateService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    // ========== MÉTHODE GÉNÉRIQUE REFACTORISÉE ==========
    @Transactional
    private LoginResponseDto registerUser(
            CreatePasswordRequestDto request, 
            RoleName roleName,
            YpareoUserInfo userInfo) {
        
        log.info("Registering new {} with Ypareo Login: {}", roleName, request.getYpareoLogin());

        if (!request.isPasswordsMatch()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByYpareoLogin(request.getYpareoLogin())) {
            throw new IllegalStateException(roleName + " already registered");
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " not found"));

        User user = User.builder()
                .ypareoId(userInfo.getYpareoId())
                .ypareoLogin(userInfo.getYpareoLogin())
                .email(userInfo.getEmail())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isFirstLogin(true)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        user = userRepository.save(user);
        log.info("Successfully registered {}: {} - {}", roleName, user.getYpareoLogin(), user.getFullName());

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtUtil.getExpirationTime())
                .isFirstLogin(true)
                .userInfo(mapToUserInfo(user))
                .build();
    }

    @Transactional
    public LoginResponseDto registerStudent(CreatePasswordRequestDto request) {
        StudentInfoDto studentInfo;
        
        try {
            studentInfo = ypareoServiceClient.getStudentInfo(request.getYpareoLogin());
            if (studentInfo == null) {
                throw new IllegalArgumentException("Student not found in Ypareo system. Please check your Ypareo Login.");
            }
        } catch (Exception e) {
            log.error("Error validating student in Ypareo: {}", request.getYpareoLogin(), e);

            if (ypareoServiceClient.isYpareoServiceAvailable()) {
                throw new IllegalArgumentException("Student not found in Ypareo system. Please check your Ypareo ID.");
            } else {
                log.warn("Ypareo service unavailable. Allowing registration for: {}", request.getYpareoLogin());
                studentInfo = StudentInfoDto.builder()
                        .ypareoId(request.getYpareoLogin())
                        .ypareoLogin(request.getYpareoLogin())
                        .firstName("À vérifier")
                        .lastName("À vérifier")
                        .email(request.getYpareoLogin() + "@iticparis.com")
                        .className("PENDING")
                        .isActive(true)
                        .build();
            }
        }

        return registerUser(request, RoleName.STUDENT, studentInfo);
    }

    // ========== INSCRIPTION FORMATEUR ==========
    @Transactional
    public LoginResponseDto registerTeacher(CreatePasswordRequestDto request) {
        TrainerInfoDto teacherInfo;
        
        try {
            teacherInfo = ypareoServiceClient.getTrainerInfo(request.getYpareoLogin());
            if (teacherInfo == null) {
                throw new IllegalArgumentException("Teacher not found in Ypareo system. Please check your Ypareo Login.");
            }
        } catch (Exception e) {
            log.error("Error validating teacher in Ypareo: {}", request.getYpareoLogin(), e);

            if (ypareoServiceClient.isYpareoServiceAvailable()) {
                throw new IllegalArgumentException("Teacher not found in Ypareo system. Please check your Ypareo ID.");
            } else {
                log.warn("Ypareo service unavailable. Allowing registration for: {}", request.getYpareoLogin());
                teacherInfo = TrainerInfoDto.builder()
                        .ypareoId(request.getYpareoLogin())
                        .ypareoLogin(request.getYpareoLogin())
                        .firstName("À vérifier")
                        .lastName("À vérifier")
                        .email(request.getYpareoLogin() + "@iticparis.com")
                        .fonction("PENDING")
                        .isActive(true)
                        .build();
            }
        }

        return registerUser(request, RoleName.TRAINER, teacherInfo);
    }

    @Transactional
    public LoginResponseDto authenticateUser(LoginRequestDto request) {
        log.info("Authenticating user: {}", request.getYpareoLogin());

        User user = userRepository.findByYpareoLoginAndIsActiveTrue(request.getYpareoLogin())
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent Ypareo Login: {}", request.getYpareoLogin());
                    return new BadCredentialsException("Invalid credentials");
                });

        if (!user.isAccountNonLocked()) {
            log.warn("Login attempt on locked account: {}", request.getYpareoLogin());
            throw new BadCredentialsException("Account is temporarily locked. Please try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new BadCredentialsException("Invalid credentials");
        }

        handleSuccessfulLogin(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        log.info("Successfully authenticated user: {}", request.getYpareoLogin());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtUtil.getExpirationTime())
                .isFirstLogin(false)
                .userInfo(mapToUserInfo(user))
                .build();
    }

    // ========== ALIAS POUR COMPATIBILITÉ ==========
    @Transactional
    public LoginResponseDto authenticateStudent(LoginRequestDto request) {
        return authenticateUser(request);
    }

    // ========== MÉTHODES UTILITAIRES (inchangées) ==========
    
    @Transactional
    public LoginResponseDto refreshAccessToken(RefreshTokenRequestDto request) {
        log.info("Refreshing access token");

        RefreshToken refreshToken = refreshTokenRepository
                .findValidRefreshToken(request.getRefreshToken(), LocalDateTime.now())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user);

        log.info("Successfully refreshed access token for user: {}", user.getYpareoLogin());

        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtUtil.getExpirationTime())
                .isFirstLogin(false)
                .userInfo(mapToUserInfo(user))
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        int affected = refreshTokenRepository.revokeToken(refreshToken);
        log.info("Nombre de tokens révoqués : {}", affected);
    }

    @Transactional
    public void logoutFromAllDevices(String ypareoId) {
        log.info("Logging out user from all devices: {}", ypareoId);
        User user = userRepository.findByYpareoId(ypareoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        int affected = refreshTokenRepository.revokeAllUserTokens(user);
        log.info("Nombre de tokens révoqués : {}", affected);
    }

    public boolean studentExists(String ypareoId) {
        return userRepository.existsByYpareoIdAndRole_RoleName(ypareoId, RoleName.STUDENT);
    }

    public boolean teacherExists(String ypareoId) {
        return userRepository.existsByYpareoIdAndRole_RoleName(ypareoId, RoleName.TRAINER);
    }

    public UserInfoDto getUserInfo(String ypareoId) {
        User user = userRepository.findByYpareoIdAndIsActiveTrue(ypareoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToUserInfo(user);
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
            userUpdateService.lockAccount(user.getId(), lockUntil);
            log.warn("Account locked for user: {} after {} failed attempts", user.getYpareoLogin(), attempts);
        } else {
            userUpdateService.updateFailedLoginAttempts(user.getId(), attempts);
            log.warn("Failed login attempt {} for user: {}", attempts, user.getYpareoLogin());
        }
    }

    private void handleSuccessfulLogin(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getAccountLockedUntil() != null) {
            userUpdateService.updateFailedLoginAttempts(user.getId(), 0);
            userUpdateService.unlockAccount(user.getId());
        }

        userUpdateService.updateLastLogin(user.getId(), LocalDateTime.now());

        if (user.getIsFirstLogin()) {
            userUpdateService.markFirstLoginCompleted(user.getId());
        }
    }

    private RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private UserInfoDto mapToUserInfo(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .ypareoLogin(user.getYpareoLogin())
                .ypareoId(user.getYpareoId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .isFirstLogin(user.getIsFirstLogin())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .rolename(user.getRole().getRoleName())
                .build();
    }
}