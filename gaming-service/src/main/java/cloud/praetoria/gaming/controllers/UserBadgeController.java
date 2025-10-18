package cloud.praetoria.gaming.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.entities.BadgeProgress;
import cloud.praetoria.gaming.entities.UserBadge;
import cloud.praetoria.gaming.services.BadgeProgressService;
import cloud.praetoria.gaming.services.UserBadgeService;

@RestController
@RequestMapping("/api/users")
public class UserBadgeController {

    @Autowired
    private UserBadgeService userBadgeService;

    @Autowired
    private BadgeProgressService badgeProgressService;

    @GetMapping("/{userId}/badges")
    public ResponseEntity<List<UserBadge>> getUserBadges(@PathVariable Long userId) {
        List<UserBadge> userBadges = userBadgeService.getUserBadges(userId);
        return ResponseEntity.ok(userBadges);
    }


    @GetMapping("/{userId}/badges/progress")
    public ResponseEntity<List<BadgeProgress>> getMyBadgesProgress(@PathVariable Long userId) {
        List<BadgeProgress> progress = badgeProgressService.getProgressForUser(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/{userId}/badges/summary")
    public ResponseEntity<Map<String, Object>> getUserBadgesSummary(@PathVariable Long userId) {
        Map<String, Object> summary = userBadgeService.getUserBadgesSummary(userId);
        return ResponseEntity.ok(summary);
    }
}