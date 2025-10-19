package cloud.praetoria.auth.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.auth.entities.User;
import cloud.praetoria.auth.enums.RoleName;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByYpareoId(String ypareoId);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByYpareoId(String ypareoId);
    
    boolean existsByYpareoLogin(String ypareoLogin);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByYpareoIdAndIsActiveTrue(String ypareoId);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :lastLogin, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateFailedLoginAttempts(@Param("userId") Long userId, @Param("attempts") Integer attempts);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountLockedUntil = NULL, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void unlockAccount(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountLockedUntil = :lockedUntil, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void lockAccount(@Param("userId") Long userId, @Param("lockedUntil") LocalDateTime lockedUntil);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isFirstLogin = false, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void markFirstLoginCompleted(@Param("userId") Long userId);
    
    boolean existsByYpareoIdAndRole_RoleName(String ypareoId, RoleName roleName);
    Optional<User> findByYpareoLoginAndIsActiveTrue(String ypareoLogin);

	boolean existsByYpareoLoginAndRole_RoleName(String ypareoLogin, RoleName roleName);
}
