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
public class BadgeUnlockedEventDto {
    private Long userId;
    private String userName;
    private BadgeDto badge;
    private Integer obtainCount;
    private Integer xpEarned;
    private String unlockContext;
    private LocalDateTime unlockedAt;
    private Boolean isFirstTime;
    private String message;
}
