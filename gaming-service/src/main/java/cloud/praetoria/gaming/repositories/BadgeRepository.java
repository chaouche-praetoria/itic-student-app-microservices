package cloud.praetoria.gaming.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.enums.BadgeCategory;
import cloud.praetoria.gaming.enums.BadgeRarity;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    Optional<Badge> findByCode(String code);
    List<Badge> findByCategory(BadgeCategory category);
    List<Badge> findByRarity(BadgeRarity rarity);
    List<Badge> findByRepeatable(Boolean isRepeatable);
    List<Badge> findByParentBadgeId(Long parentBadgeId);
}