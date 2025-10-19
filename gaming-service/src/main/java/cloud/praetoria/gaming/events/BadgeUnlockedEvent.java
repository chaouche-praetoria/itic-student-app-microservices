package cloud.praetoria.gaming.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class BadgeUnlockedEvent extends ApplicationEvent {
    
    private final Long userId;
    private final String badgeCode;
    private final Integer xpEarned;
    private final String context;
    
    public BadgeUnlockedEvent(Object source, Long userId, String badgeCode, 
                              Integer xpEarned, String context) {
        super(source);
        this.userId = userId;
        this.badgeCode = badgeCode;
        this.xpEarned = xpEarned;
        this.context = context;
    }
}