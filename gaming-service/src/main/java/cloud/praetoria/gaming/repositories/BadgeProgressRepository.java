package cloud.praetoria.gaming.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.BadgeProgress;
import cloud.praetoria.gaming.entities.User;

@Repository
public interface BadgeProgressRepository extends JpaRepository<BadgeProgress, Long> {
    
	  List<BadgeProgress> findByUser(User user);
	    List<BadgeProgress> findByUserId(Long userId);
	    Optional<BadgeProgress> findByUserAndBadge(User user, Badge badge);
}