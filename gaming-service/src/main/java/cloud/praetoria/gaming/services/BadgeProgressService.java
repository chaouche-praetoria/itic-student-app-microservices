package cloud.praetoria.gaming.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.BadgeProgress;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.repositories.BadgeProgressRepository;
import cloud.praetoria.gaming.repositories.BadgeRepository;
import cloud.praetoria.gaming.repositories.UserRepository;

@Service
public class BadgeProgressService {

    @Autowired
    private BadgeProgressRepository badgeProgressRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    public BadgeProgress updateProgress(Long userId, String badgeCode, Integer currentValue) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        Optional<BadgeProgress> existingProgress = badgeProgressRepository
            .findByUserId(userId)
            .stream()
            .filter(p -> p.getBadge().getId().equals(badge.getId()))
            .findFirst();

        BadgeProgress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCurrentValue(currentValue);
        } else {
            progress = BadgeProgress.builder()
                .user(user)
                .badge(badge)
                .currentValue(currentValue)
                .targetValue(100)
                .build();
        }

        return badgeProgressRepository.save(progress);
    }

    public List<BadgeProgress> getProgressForUser(Long userId) {
        return badgeProgressRepository.findByUserId(userId);
    }

    public Boolean checkAndCompleteProgress(Long userId, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return false;
        }

        Optional<BadgeProgress> progressOpt = badgeProgressRepository
            .findByUserId(userId)
            .stream()
            .filter(p -> p.getBadge().getId().equals(badge.getId()))
            .findFirst();

        if (progressOpt.isPresent()) {
            BadgeProgress progress = progressOpt.get();
            if (progress.isCompleted()) {
                progress.setActive(false);
                badgeProgressRepository.save(progress);
                return true;
            }
        }

        return false;
    }
}
