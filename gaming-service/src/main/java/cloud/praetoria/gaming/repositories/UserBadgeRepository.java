package cloud.praetoria.gaming.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.entities.UserBadge;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(User user);
    List<UserBadge> findByUserId(Long userId);
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
    Long countByUser(User user);
    Long countByUserId(Long userId);
    List<UserBadge> findByUserIdOrderByUnlockedAtDesc(Long userId);
}