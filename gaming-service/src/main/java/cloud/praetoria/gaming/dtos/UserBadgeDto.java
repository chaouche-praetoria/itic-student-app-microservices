package cloud.praetoria.gaming.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadgeDto {
    
    private Long id;
    private Long userId;
    private String userName;
    private BadgeDto badge;
    private LocalDateTime unlockedAt;
    private Integer obtainCount;
    private String unlockContext;
    private Integer xpEarned;
    private Boolean notificationSeen;
    private Boolean favorite;
    private String displayName;
    private Boolean recentlyUnlocked;
}