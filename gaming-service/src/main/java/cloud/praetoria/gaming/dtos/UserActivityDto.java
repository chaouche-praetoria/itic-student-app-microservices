package cloud.praetoria.gaming.dtos;

import java.time.LocalDateTime;

import cloud.praetoria.gaming.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto {
    
	    
	    private Long id;
	    private Long userId;
	    private ActivityType activityType;
	    private LocalDateTime activityDate;
	    private Long targetUserId;
	    private String targetUserName;
	    private Long resourceId;
	    private String resourceType;
	    private String resourceName;
	    private String formationCode;
	    private Double value;
	    private String valueUnit;
	    private Integer xpEarned;
	    private String description;
	    private Boolean countsForBadges;
	}