package cloud.praetoria.gaming.entities;

import java.time.LocalDateTime;

import cloud.praetoria.gaming.enums.BadgeCategory;
import cloud.praetoria.gaming.enums.BadgeLevel;
import cloud.praetoria.gaming.enums.BadgeRarity;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badges", indexes = {
    @Index(name = "idx_badge_category", columnList = "category"),
    @Index(name = "idx_badge_code", columnList = "code", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BadgeCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BadgeRarity rarity;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BadgeLevel level;
    
    @Column(nullable = false)
    private Integer xpReward;
    
    @Column(length = 500)
    private String iconUrl;
    
    @Column(length = 10)
    private String emoji;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean repeatable = false;
    
    @Column
    private Integer maxRepeats;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean visibleBeforeUnlock = true;
    
    @Column(columnDefinition = "TEXT")
    private String criteriaJson;
    
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_badge_id")
    private Badge parentBadge;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_badge_id")
    private Badge nextBadge;
    
    @Column
    private Integer displayOrder;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public int getEffectiveXpReward() {
        double multiplier = 1.0;
        
        if (rarity != null) {
            multiplier *= rarity.getXpMultiplier();
        }
        
        if (level != null) {
            multiplier *= level.getXpMultiplier();
        }
        
        return (int) (xpReward * multiplier);
    }
    
    public String getFullDisplayName() {
        if (emoji != null && !emoji.isEmpty()) {
            return emoji + " " + name;
        }
        return name;
    }
    
    public boolean canBeObtainedAgain(int currentCount) {
        if (!repeatable) {
            return currentCount == 0;
        }
        if (maxRepeats == null) {
            return true;
        }
        return currentCount < maxRepeats;
    }
}