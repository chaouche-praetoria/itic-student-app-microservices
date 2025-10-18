package cloud.praetoria.gaming.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cloud.praetoria.gaming.events.UserActivityEvent;
import cloud.praetoria.gaming.services.BadgeEvaluationService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BadgeEvaluationListener {
    
    private final BadgeEvaluationService badgeEvaluationService;
    
    @Async
    @EventListener
    public void handleUserActivity(UserActivityEvent event) {
        badgeEvaluationService.checkAllBadgesForUser(event.getUserId());
    }
}
