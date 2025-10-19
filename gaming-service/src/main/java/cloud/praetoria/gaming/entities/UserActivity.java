package cloud.praetoria.gaming.entities;

import java.time.LocalDateTime;

import cloud.praetoria.gaming.enums.ActivityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_activities", 
    indexes = {
        @Index(name = "idx_activity_user", columnList = "user_id"),
        @Index(name = "idx_activity_type", columnList = "activityType"),
        @Index(name = "idx_activity_date", columnList = "activityDate"),
        @Index(name = "idx_activity_target_user", columnList = "targetUserId")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private ActivityType activityType;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime activityDate = LocalDateTime.now();
    
    @Column
    private Long targetUserId;
    
    @Column(length = 255)
    private String targetUserName;
    
    @Column
    private Long resourceId;
    
    @Column(length = 100)
    private String resourceType;
    
    @Column(length = 500)
    private String resourceName;
    
    @Column(length = 100)
    private String formationCode;
    
    @Column
    private Double value;
    
    @Column(length = 50)
    private String valueUnit;
    
    @Column
    @Builder.Default
    private Integer xpEarned = 0;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String metadataJson;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean countsForBadges = true;
    
    @Column(length = 50)
    private String source;
    
    @Column(length = 50)
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    public boolean isRecent() {
        return activityDate.isAfter(LocalDateTime.now().minusHours(24));
    }
    
    public boolean hasTargetUser() {
        return targetUserId != null;
    }
    
    public boolean hasResource() {
        return resourceId != null;
    }
    
   
}