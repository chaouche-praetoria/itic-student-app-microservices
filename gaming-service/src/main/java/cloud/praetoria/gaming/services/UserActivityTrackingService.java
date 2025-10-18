package cloud.praetoria.gaming.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.entities.UserActivity;
import cloud.praetoria.gaming.enums.ActivityType;
import cloud.praetoria.gaming.events.UserActivityEvent;
import cloud.praetoria.gaming.repositories.UserActivityRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserActivityTrackingService {
    
    @Autowired
    private UserActivityRepository userActivityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public UserActivity trackActivity(Long userId, ActivityType activityType, String metadata) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        UserActivity activity = UserActivity.builder()
            .user(user)
            .activityType(activityType)
            .activityDate(LocalDateTime.now())  
            .metadataJson(metadata)          
            .xpEarned(activityType.getBaseXp())
            .build();
        
        UserActivity savedActivity = userActivityRepository.save(activity);
        
        eventPublisher.publishEvent(new UserActivityEvent(
            this, 
            userId, 
            activityType, 
            metadata
        ));
        
        return savedActivity;
    }
}