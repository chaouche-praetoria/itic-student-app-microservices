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
public class BadgeProgressDto {
    
    private Long id;
    private Long userId;
    private BadgeDto badge;
    private Integer currentValue;
    private Integer targetValue;
    private Double completionPercentage;
    private LocalDateTime startedAt;
    private LocalDateTime lastUpdatedAt;
    private Boolean active;
    private Integer daysRemaining;
    private LocalDateTime deadline;
    private String progressDisplay;
    private Boolean completed;
}