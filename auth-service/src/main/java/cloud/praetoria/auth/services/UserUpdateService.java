package cloud.praetoria.auth.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdateService {
    
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateFailedLoginAttempts(Long userId, int attempts) {
        int updated = userRepository.updateFailedLoginAttempts(userId, attempts);
        log.info("🔥 Nombre de lignes modifiées (failedLoginAttempts) : {}", updated);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void lockAccount(Long userId, LocalDateTime lockedUntil) {
        userRepository.lockAccount(userId, lockedUntil);
        log.warn("Account locked for userId {} until {}", userId, lockedUntil);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unlockAccount(Long userId) {
        userRepository.unlockAccount(userId);
        log.info("Account unlocked for userId {}", userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateLastLogin(Long userId, LocalDateTime lastLogin) {
        userRepository.updateLastLogin(userId, lastLogin);
        log.info("Updated last login for userId {}: {}", userId, lastLogin);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFirstLoginCompleted(Long userId) {
        userRepository.markFirstLoginCompleted(userId);
        log.info("Marked first login completed for userId {}", userId);
    }
}