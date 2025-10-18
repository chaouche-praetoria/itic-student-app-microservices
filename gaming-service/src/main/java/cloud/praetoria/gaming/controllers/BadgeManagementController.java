package cloud.praetoria.gaming.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.entities.BadgeProgress;
import cloud.praetoria.gaming.entities.UserBadge;
import cloud.praetoria.gaming.services.BadgeProgressService;
import cloud.praetoria.gaming.services.BadgeService;
import cloud.praetoria.gaming.services.UserBadgeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/badges/management")
@RequiredArgsConstructor
public class BadgeManagementController {

    private final BadgeProgressService badgeProgressService;
    private final UserBadgeService userBadgeService;
    private final BadgeService badgeService;

    @PostMapping("/increment-progress")
    public ResponseEntity<Map<String, Object>> incrementProgress(
        @RequestParam Long userId,
        @RequestParam String badgeCode,
        @RequestParam(defaultValue = "1") Integer incrementBy
    ) {
        BadgeProgress progress = badgeProgressService.updateProgress(userId, badgeCode, incrementBy);
        
        Map<String, Object> response = new HashMap<>();
        if (progress != null) {
            response.put("success", true);
            response.put("badgeCode", badgeCode);
            response.put("currentValue", progress.getCurrentValue());
            response.put("targetValue", progress.getTargetValue());
            response.put("progressPercentage", progress.getCompletionPercentage());
            response.put("isCompleted", progress.isCompleted());
            
            if (progress.isCompleted()) {
                response.put("message", "Badge débloqué ! 🎉");
            } else {
                response.put("message", "Progression mise à jour");
            }
        } else {
            response.put("success", false);
            response.put("message", "Badge non trouvé");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/award-manual")
    public ResponseEntity<Map<String, Object>> awardBadgeManually(
        @RequestParam Long userId,
        @RequestParam String badgeCode,
        @RequestParam(required = false) String context
    ) {
        if (context == null) {
            context = "Attribution manuelle par formateur";
        }
        
        UserBadge userBadge = userBadgeService.awardBadge(userId, badgeCode, context);
        
        Map<String, Object> response = new HashMap<>();
        if (userBadge != null) {
            response.put("success", true);
            response.put("badgeName", userBadge.getBadge().getName());
            response.put("badgeEmoji", userBadge.getBadge().getEmoji());
            response.put("xpEarned", userBadge.getXpEarned());
            response.put("message", "Badge attribué avec succès ! 🎉");
        } else {
            response.put("success", false);
            response.put("message", "Impossible d'attribuer le badge");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{userId}/progress")
    public ResponseEntity<List<BadgeProgress>> getStudentProgress(@PathVariable Long userId) {
        List<BadgeProgress> progressList = badgeProgressService.getProgressForUser(userId);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/student/{userId}/badges")
    public ResponseEntity<List<UserBadge>> getStudentBadges(@PathVariable Long userId) {
        List<UserBadge> badges = userBadgeService.getUserBadges(userId);
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/student/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getStudentSummary(@PathVariable Long userId) {
        Map<String, Object> summary = userBadgeService.getUserBadgesSummary(userId);
        List<BadgeProgress> progressList = badgeProgressService.getProgressForUser(userId);
        
        summary.put("inProgressBadges", progressList.size());
        summary.put("progressDetails", progressList);
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/badges/available")
    public ResponseEntity<List<Badge>> getAllAvailableBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        return ResponseEntity.ok(badges);
    }

    @PostMapping("/student/{userId}/reset-progress")
    public ResponseEntity<Map<String, Object>> resetProgress(
        @PathVariable Long userId,
        @RequestParam String badgeCode
    ) {
        badgeProgressService.updateProgress(userId, badgeCode, 0);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Progression réinitialisée");
        
        return ResponseEntity.ok(response);
    }
}
