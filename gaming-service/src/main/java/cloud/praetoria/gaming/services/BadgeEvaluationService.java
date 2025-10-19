package cloud.praetoria.gaming.services;


import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.UserBadge;
import cloud.praetoria.gaming.enums.ActivityType;
import cloud.praetoria.gaming.events.BadgeUnlockedEvent;
import cloud.praetoria.gaming.repositories.BadgeRepository;
import cloud.praetoria.gaming.repositories.UserActivityRepository;
import cloud.praetoria.gaming.repositories.UserBadgeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeEvaluationService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserBadgeService userBadgeService;
    private final BadgeProgressService badgeProgressService;
    private final ApplicationEventPublisher eventPublisher;

    public Boolean evaluateBadgeCriteria(Long userId, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) {
            return false;
        }

        switch (badgeCode) {
            case "PREMIER_PAS":
                return evaluateProfileCompletion(userId);
            case "LEGENDE_ITIC":
                return evaluateLegendeITIC(userId);
            case "TOP_3_PROMO":
                return evaluateTop3Promo(userId);
            case "COLLECTIONNEUR_BRONZE":
                return evaluateCollectionneur(userId, 10);
            case "COLLECTIONNEUR_SILVER":
                return evaluateCollectionneur(userId, 20);
            case "COLLECTIONNEUR_GOLD":
                return evaluateCollectionneur(userId, 30);
            case "PERFECTIONNISTE":
                return evaluatePerfectionniste(userId);
            default:
                return false;
        }
    }

    public void checkAllBadgesForUser(Long userId) {
        List<Badge> allBadges = badgeRepository.findAll();
        for (Badge badge : allBadges) {
            evaluateAndAwardBadge(userId, badge.getCode());
        }
    }

    public void evaluateAndAwardBadge(Long userId, String badgeCode) {
        if (userBadgeService.userHasBadge(userId, badgeCode)) {
            return;
        }

        if (evaluateBadgeCriteria(userId, badgeCode)) {
            UserBadge newBadge = userBadgeService.awardBadge(userId, badgeCode, "Auto-attribution");
            
            if (newBadge != null) {
                eventPublisher.publishEvent(new BadgeUnlockedEvent(
                    this,
                    userId,
                    badgeCode,
                    newBadge.getXpEarned(),
                    "Badge débloqué automatiquement"
                ));

                badgeProgressService.checkAndCompleteProgress(userId, badgeCode);
            }
        }
    }

    public Boolean evaluateProfileCompletion(Long userId) {
        Long profileActivities = userActivityRepository.countByUserIdAndActivityType(
            userId, 
            ActivityType.PROFILE_COMPLETE
        );
        return profileActivities > 0;
    }

    private Boolean evaluateLegendeITIC(Long userId) {
        Long totalXp = userBadgeRepository.findByUserId(userId)
            .stream()
            .mapToLong(ub -> ub.getXpEarned())
            .sum();
        return totalXp >= 10000;
    }

    private Boolean evaluateTop3Promo(Long userId) {
        return false;
    }

    private Boolean evaluateCollectionneur(Long userId, int targetCount) {
        Long badgeCount = userBadgeRepository.countByUserId(userId);
        return badgeCount >= targetCount;
    }

    private Boolean evaluatePerfectionniste(Long userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
        long goldBadges = userBadges.stream()
            .filter(ub -> ub.getBadge().getLevel() != null)
            .filter(ub -> ub.getBadge().getLevel().name().equals("GOLD"))
            .count();
        
        long totalGoldBadgesAvailable = badgeRepository.findAll().stream()
            .filter(b -> b.getLevel() != null)
            .filter(b -> b.getLevel().name().equals("GOLD"))
            .count();
        
        return goldBadges >= totalGoldBadgesAvailable;
    }
}
