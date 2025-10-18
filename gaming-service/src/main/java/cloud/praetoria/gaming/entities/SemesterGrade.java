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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "semester_grades", 
    indexes = {
        @Index(name = "idx_grade_user", columnList = "user_id"),
        @Index(name = "idx_grade_semester", columnList = "semesterCode"),
        @Index(name = "idx_grade_formation", columnList = "formationCode"),
        @Index(name = "idx_grade_average", columnList = "averageGrade")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_semester", 
            columnNames = {"user_id", "semesterCode", "formationCode"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SemesterGrade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 10)
    private String semesterCode;
    
    @Column(nullable = false, length = 20)
    private String academicYear;
    
    @Column(nullable = false, length = 100)
    private String formationCode;
    
    @Column(length = 255)
    private String formationName;
    
    @Column(nullable = false)
    private Double averageGrade;
    
    @Column
    private Double minGrade;
    
    @Column
    private Double maxGrade;
    
    @Column
    private Integer subjectCount;
    
    @Column
    private Integer ectsEarned;
    
    @Column
    private Integer ectsPossible;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean validated = false;
    
    @Column
    private Integer rank;
    
    @Column
    private Integer totalStudents;

    @Column(length = 50)
    private String mention;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime semesterEndDate;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean currentSemester = false;
    
    @Column(columnDefinition = "TEXT")
    private String detailsJson;
    
    public void calculateMention() {
        if (averageGrade >= 16.0) {
            this.mention = "Félicitations";
        } else if (averageGrade >= 14.0) {
            this.mention = "Très Bien";
        } else if (averageGrade >= 12.0) {
            this.mention = "Bien";
        } else if (averageGrade >= 10.0) {
            this.mention = "Assez Bien";
        } else if (averageGrade >= 8.0) {
            this.mention = "Passable";
        } else {
            this.mention = "Non validé";
        }
    }
    
    public boolean isSTier() {
        return averageGrade >= 16.0;
    }
    
    public double getEctsValidationPercentage() {
        if (ectsPossible == null || ectsPossible == 0) {
            return 0.0;
        }
        return (ectsEarned.doubleValue() / ectsPossible.doubleValue()) * 100.0;
    }
    
    public boolean isInTopPercentage(double percentage) {
        if (rank == null || totalStudents == null || totalStudents == 0) {
            return false;
        }
        double currentPercentage = (rank.doubleValue() / totalStudents.doubleValue()) * 100.0;
        return currentPercentage <= percentage;
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculatedAt = LocalDateTime.now();
        validated = averageGrade >= 10.0;
        calculateMention();
    }
    
    @PrePersist
    protected void onCreate() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
        validated = averageGrade >= 10.0;
        calculateMention();
    }
}
