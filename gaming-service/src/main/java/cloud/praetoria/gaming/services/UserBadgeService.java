package cloud.praetoria.gaming.services;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.entities.UserBadge;
import cloud.praetoria.gaming.repositories.BadgeRepository;
import cloud.praetoria.gaming.repositories.UserBadgeRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId);
    }

    public boolean userHasBadge(Long userId, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return false;
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        return userBadgeRepository.findByUserAndBadge(user, badge).isPresent();
    }

    public UserBadge awardBadge(Long userId, String badgeCode, String context) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        UserBadge userBadge = UserBadge.builder()
            .user(user)
            .badge(badge)
            .unlockedAt(LocalDateTime.now())
            .unlockContext(context)
            .obtainCount(1)
            .xpEarned(badge.getXpReward())
            .build();

        return userBadgeRepository.save(userBadge);
    }

    public Map<String, Object> getUserBadgesSummary(Long userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBadges", userBadges.size());
        summary.put("totalXp", userBadges.stream().mapToInt(UserBadge::getXpEarned).sum());
        return summary;
    }
}
