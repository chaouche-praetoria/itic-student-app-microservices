package cloud.praetoria.gaming.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import cloud.praetoria.gaming.enums.AttendanceStatus;
import cloud.praetoria.gaming.enums.EventType;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendances", 
    indexes = {
        @Index(name = "idx_attendance_user", columnList = "user_id"),
        @Index(name = "idx_attendance_date", columnList = "attendanceDate"),
        @Index(name = "idx_attendance_status", columnList = "status"),
        @Index(name = "idx_attendance_classgroup", columnList = "classGroupCode")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDate attendanceDate;
    
    @Column(nullable = false)
    private LocalTime scheduledStartTime;
    
    @Column
    private LocalTime scheduledEndTime;
    
    @Column
    private LocalTime actualArrivalTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AttendanceStatus status;
    
    @Column
    private Integer lateMinutes;
    
    @Column(length = 100)
    private String classGroupCode;
    
    @Column(length = 255)
    private String courseName;
    
    @Column(length = 100)
    private String courseCode;
    
    @Column(length = 255)
    private String trainerName;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EventType eventType;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Source de la donnée (Fait par le prof à la min\ ou via YPAREO)
     */
    @Column(length = 50)
    private String source;
    
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt;
    
    public void calculateLate() {
        if (actualArrivalTime != null && scheduledStartTime != null) {
            if (actualArrivalTime.isAfter(scheduledStartTime)) {
                this.lateMinutes = (int) java.time.Duration
                    .between(scheduledStartTime, actualArrivalTime)
                    .toMinutes();
                
                if (this.lateMinutes > 0) {
                    this.status = AttendanceStatus.LATE;
                }
            }
        }
    }
    
    public boolean isSignificantlyLate() {
        return lateMinutes != null && lateMinutes > 10;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateLate();
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        calculateLate();
    }
}