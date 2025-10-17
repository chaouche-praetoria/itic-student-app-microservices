package cloud.praetoria.gaming.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_badges", 
    indexes = {
        @Index(name = "idx_user_badge_user", columnList = "user_id"),
        @Index(name = "idx_user_badge_badge", columnList = "badge_id"),
        @Index(name = "idx_user_badge_unlocked", columnList = "unlockedAt")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_badge_count", 
            columnNames = {"user_id", "badge_id", "obtainCount"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserBadge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime unlockedAt = LocalDateTime.now();
    
    /**
     * Là c'est pour les badges qu'on peut avoir plusieurs fois
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer obtainCount = 1;
    
    @Column(columnDefinition = "TEXT")
    private String unlockContext;
    
    @Column(nullable = false)
    private Integer xpEarned;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean notificationSeen = false;
    
    @Column
    private LocalDateTime notificationSeenAt;
    
    @Column(columnDefinition = "TEXT")
    private String metadataJson;
    
    public void markNotificationAsSeen() {
        this.notificationSeen = true;
        this.notificationSeenAt = LocalDateTime.now();
    }
    
    
    public String getDisplayName() {
        String baseName = badge.getName();
        if (badge.getRepeatable() && obtainCount > 1) {
            return baseName + " x" + obtainCount;
        }
        return baseName;
    }
    
}