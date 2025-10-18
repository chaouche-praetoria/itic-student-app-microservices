package cloud.praetoria.gaming.services;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.UserBadge;
import cloud.praetoria.gaming.repositories.BadgeRepository;
import cloud.praetoria.gaming.repositories.UserBadgeRepository;

@Service
public class UserBadgeService {

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId);
    }

    public UserBadge awardBadge(Long userId, String badgeCode, String context) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return null;
        }

        UserBadge userBadge = new UserBadge();
        userBadge.setId(userId);
        userBadge.setBadge(badge);
        userBadge.setUnlockedAt(LocalDateTime.now());
        userBadge.setUnlockContext(context);
        userBadge.setObtainCount(1);
        userBadge.setXpEarned(badge.getXpReward());

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