package cloud.praetoria.gaming.services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.enums.BadgeCategory;
import cloud.praetoria.gaming.repositories.BadgeRepository;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> getBadgeByCode(String code) {
        return badgeRepository.findByCode(code);
    }

    public List<Badge> getBadgesByCategory(BadgeCategory category) {
        return badgeRepository.findByCategory(category);
    }

    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }
}