package cloud.praetoria.gaming.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cloud.praetoria.gaming.enums.EvalType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvalType type;

    @Column(nullable = false)
    private Integer maxPoints;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassGroup classGroup;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<PointsAward> pointsAwards = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void validateCreator() {
        if (!creator.isTrainer()) {
            throw new IllegalArgumentException("Only a trainer can create an assignment");
        }
    }

}