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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badge_progress", 
    indexes = {
        @Index(name = "idx_progress_user", columnList = "user_id"),
        @Index(name = "idx_progress_badge", columnList = "badge_id"),
        @Index(name = "idx_progress_completion", columnList = "completionPercentage")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_badge_progress", 
            columnNames = {"user_id", "badge_id"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BadgeProgress {
    
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
    private Integer currentValue = 0;
    
    @Column(nullable = false)
    private Integer targetValue;
    
    @Column(nullable = false)
    @Builder.Default
    private Double completionPercentage = 0.0;
    
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastUpdatedAt = LocalDateTime.now();
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @Column
    private Integer daysRemaining;
    
    @Column
    private LocalDateTime deadline;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public void updateProgress(int increment) {
        this.currentValue += increment;
        
        if (this.currentValue < 0) {
            this.currentValue = 0;
        }
        
        if (targetValue > 0) {
            this.completionPercentage = Math.min(
                100.0, 
                (currentValue.doubleValue() / targetValue.doubleValue()) * 100.0
            );
        }
        
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    public void setCurrentValue(int value) {
        this.currentValue = Math.max(0, value);
        
        if (targetValue > 0) {
            this.completionPercentage = Math.min(
                100.0, 
                (currentValue.doubleValue() / targetValue.doubleValue()) * 100.0
            );
        }
        
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    public boolean isCompleted() {
        return currentValue >= targetValue;
    }
    
    public int getRemainingPoints() {
        return Math.max(0, targetValue - currentValue);
    }
    
    public boolean isExpired() {
        return deadline != null && LocalDateTime.now().isAfter(deadline);
    }
    
    public long calculateDaysRemaining() {
        if (deadline == null) {
            return -1;
        }
        
        long days = java.time.Duration.between(LocalDateTime.now(), deadline).toDays();
        return Math.max(0, days);
    }
    public void updateDaysRemaining() {
        if (deadline != null) {
            this.daysRemaining = (int) calculateDaysRemaining();
        }
    }
    
    public String getProgressDisplay() {
        return String.format("%d/%d (%.1f%%)", currentValue, targetValue, completionPercentage);
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
        updateDaysRemaining();
    }
}