package cloud.praetoria.gaming.dtos;

import cloud.praetoria.gaming.enums.BadgeCategory;
import cloud.praetoria.gaming.enums.BadgeLevel;
import cloud.praetoria.gaming.enums.BadgeRarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDto {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private BadgeCategory category;
    private BadgeRarity rarity;
    private BadgeLevel level;
    private Integer xpReward;
    private String iconUrl;
    private String emoji;
    private Boolean repeatable;
    private Integer maxRepeats;
    private Boolean visibleBeforeUnlock;
    private Long nextBadgeId;
    private Long parentBadgeId;
    private Integer displayOrder;
}