package cloud.praetoria.gaming.entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cloud.praetoria.gaming.enums.EvalType;
import cloud.praetoria.gaming.enums.UserType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor     
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // 🆕 FIX StackOverflow
@ToString(exclude = {"classGroups", "createdAssignments", "givenAwards", "receivedAwards"})  // 🆕 FIX StackOverflow
public class User {
    
    @Id
    @EqualsAndHashCode.Include  // 🆕 Uniquement l'ID dans hashCode/equals
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type; 
    
    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_class_groups",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "class_group_id", referencedColumnName = "id"),
        indexes = {
            @Index(name = "idx_user_class_user", columnList = "user_id"),
            @Index(name = "idx_user_class_group", columnList = "class_group_id")
        }
    )
    @Builder.Default
    private List<ClassGroup> classGroups = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Assignment> createdAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "grader", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PointsAward> givenAwards = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PointsAward> receivedAwards = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ========================================
    // MÉTHODES UTILITAIRES
    // ========================================

    public boolean isTrainer() {
        return type == UserType.TRAINER;
    }

    public boolean isStudent() {
        return type == UserType.STUDENT;
    }

    public boolean isAdmin() {
        return type == UserType.ADMIN;
    }

    public Integer getTotalPoints() {
        if (!isStudent()) return 0;
        return receivedAwards.stream()
                .mapToInt(PointsAward::getPointsEarned)
                .sum();
    }

    public Map<EvalType, Integer> getPointsByType() {
        if (!isStudent()) return new HashMap<>();
        return receivedAwards.stream()
                .collect(Collectors.groupingBy(
                        award -> award.getAssignment().getType(),
                        Collectors.summingInt(PointsAward::getPointsEarned)
                ));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void addClassGroup(ClassGroup classGroup) {
        if (this.classGroups == null) {
            this.classGroups = new ArrayList<>();
        }
        
        if (!this.classGroups.contains(classGroup)) {
            this.classGroups.add(classGroup);
            
            if (this.isTrainer()) {
                if (classGroup.getTrainers() == null) {
                    classGroup.setTrainers(new HashSet<>());
                }
                classGroup.getTrainers().add(this);
            } else if (this.isStudent()) {
                if (classGroup.getStudents() == null) {
                    classGroup.setStudents(new HashSet<>());
                }
                classGroup.getStudents().add(this);
            }
        }
    }

    public void removeClassGroup(ClassGroup classGroup) {
        if (this.classGroups != null && this.classGroups.contains(classGroup)) {
            this.classGroups.remove(classGroup);
            
            if (this.isTrainer() && classGroup.getTrainers() != null) {
                classGroup.getTrainers().remove(this);
            } else if (this.isStudent() && classGroup.getStudents() != null) {
                classGroup.getStudents().remove(this);
            }
        }
    }

    public List<Long> getClassGroupIds() {
        if (classGroups == null) return new ArrayList<>();
        return classGroups.stream()
            .map(ClassGroup::getId)
            .collect(Collectors.toList());
    }

    public boolean teachesInClass(Long classGroupId) {
        if (classGroups == null) return false;
        return classGroups.stream()
            .anyMatch(cg -> cg.getId().equals(classGroupId));
    }

    public int getClassGroupCount() {
        return classGroups == null ? 0 : classGroups.size();
    }
}