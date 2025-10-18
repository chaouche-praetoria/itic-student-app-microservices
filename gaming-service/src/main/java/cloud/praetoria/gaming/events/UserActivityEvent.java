package cloud.praetoria.gaming.events;


import org.springframework.context.ApplicationEvent;

import cloud.praetoria.gaming.enums.ActivityType;
import lombok.Getter;

@Getter
public class UserActivityEvent extends ApplicationEvent {
    
    private final Long userId;
    private final ActivityType activityType;
    private final String metadata;
    
    public UserActivityEvent(Object source, Long userId, 
                            ActivityType activityType, String metadata) {
        super(source);
        this.userId = userId;
        this.activityType = activityType;
        this.metadata = metadata;
    }
}

