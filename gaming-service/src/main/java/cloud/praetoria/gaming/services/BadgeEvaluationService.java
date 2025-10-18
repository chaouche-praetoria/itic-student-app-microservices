package cloud.praetoria.gaming.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.enums.ActivityType;
import cloud.praetoria.gaming.repositories.BadgeProgressRepository;
import cloud.praetoria.gaming.repositories.BadgeRepository;
import cloud.praetoria.gaming.repositories.UserActivityRepository;
import cloud.praetoria.gaming.repositories.UserBadgeRepository;

@Service
public class BadgeEvaluationService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private BadgeProgressRepository badgeProgressRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    public Boolean evaluateBadgeCriteria(Long userId, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return false;
        }

        switch (badgeCode) {
            case "PREMIER_PAS":
                return evaluateProfileCompletion(userId);
            case "BIBLIOPHILE_BRONZE":
                return evaluateBibliophile(userId, 20);
            case "MAITRE_PROJETS_BRONZE":
                return evaluateMaitreProjects(userId, 3);
            default:
                return false;
        }
    }

    public void checkAllBadgesForUser(Long userId) {
        List<Badge> allBadges = badgeRepository.findAll();
        for (Badge badge : allBadges) {
            evaluateBadgeCriteria(userId, badge.getCode());
        }
    }

    public Boolean evaluateProfileCompletion(Long userId) {
        return true;
    }

    private Boolean evaluateBibliophile(Long userId, int targetCount) {
        Long viewCount = userActivityRepository.countByUserIdAndActivityType(userId, ActivityType.RESOURCE_VIEW);
        return viewCount >= targetCount;
    }

    private Boolean evaluateMaitreProjects(Long userId, int targetCount) {
        Long projectCount = userActivityRepository.countByUserIdAndActivityType(userId, ActivityType.PROJECT_SUBMIT);
        return projectCount >= targetCount;
    }
}