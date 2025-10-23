package cloud.praetoria.gaming.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "points_awards")
public class PointsAward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer pointsEarned;

    @Column(nullable = false, updatable = false)
    private LocalDateTime awardedAt;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grader_id", nullable = false)
    private User grader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    
    @PrePersist
    public void onCreate() {
        this.awardedAt = LocalDateTime.now();
        validate(); 
    }

    @PreUpdate
    public void onUpdate() {
        validate(); 
    }

    private void validate() {
        if (grader == null || !grader.isTrainer()) {
            throw new IllegalArgumentException("Only a trainer can assign points");
        }
        if (student == null || !student.isStudent()) {
            throw new IllegalArgumentException("Points can only be awarded to students");
        }
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment must not be null");
        }
        if (pointsEarned == null || pointsEarned < 0) {
            throw new IllegalArgumentException("Points cannot be negative or null");
        }
        if (pointsEarned > assignment.getMaxPoints()) {
            throw new IllegalArgumentException(
                "Points (" + pointsEarned + ") cannot exceed max points (" + assignment.getMaxPoints() + ")"
            );
        }
    }
}
