package cloud.praetoria.gaming.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "points_awards")
public class PointsAward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer pointsEarned;

    @Column(nullable = false)
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
    @PreUpdate
    public void validate() {
        if (!grader.isTrainer()) {
            throw new IllegalArgumentException("Only a trainer can assign points");
        }
        if (!student.isStudent()) {
            throw new IllegalArgumentException("Points can only be awarded to students");
        }
        if (pointsEarned > assignment.getMaxPoints()) {
            throw new IllegalArgumentException(
                "Points (" + pointsEarned + ") cannot exceed max points (" + assignment.getMaxPoints() + ")"
            );
        }
        if (pointsEarned < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
    }

}

