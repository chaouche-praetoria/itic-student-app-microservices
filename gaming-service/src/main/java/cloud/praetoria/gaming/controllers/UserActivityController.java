package cloud.praetoria.gaming.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.enums.ActivityType;
import cloud.praetoria.gaming.services.UserActivityTrackingService;

@RestController
@RequestMapping("/api/activities")
public class UserActivityController {
    
    @Autowired
    private UserActivityTrackingService trackingService;
    
    @PostMapping("/profile-completed")
    public ResponseEntity<Void> trackProfileCompleted(@RequestParam Long userId) {
        trackingService.trackActivity(
            userId, 
            ActivityType.PROFILE_COMPLETE, 
            "Profil complété"
        );
        return ResponseEntity.ok().build();
    }
}
