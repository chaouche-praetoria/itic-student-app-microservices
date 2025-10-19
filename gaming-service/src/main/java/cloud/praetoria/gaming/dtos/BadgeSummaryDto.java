package cloud.praetoria.gaming.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BadgeSummaryDto {
    private Long userId;
    private Integer totalBadges;
    private Integer uniqueBadges;
    private Integer totalXpFromBadges;
    private Integer unseenBadges;
    private List<UserBadgeDto> recentBadges;
    private List<UserBadgeDto> favoriteBadges;
    private List<BadgeProgressDto> activeProgress;
    private List<BadgeProgressDto> nearCompletion;
    private Double averageProgressCompletion;
}
