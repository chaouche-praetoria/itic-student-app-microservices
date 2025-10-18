package cloud.praetoria.gaming.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.enums.BadgeCategory;
import cloud.praetoria.gaming.services.BadgeService;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping("/")
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Badge> getBadgeByCode(@PathVariable String code) {
        Optional<Badge> badge = badgeService.getBadgeByCode(code);
        return badge.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Badge>> getBadgesByCategory(@PathVariable BadgeCategory category) {
        List<Badge> badges = badgeService.getBadgesByCategory(category);
        return ResponseEntity.ok(badges);
    }
}