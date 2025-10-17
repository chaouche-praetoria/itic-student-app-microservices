package cloud.praetoria.gaming.entities;


import java.util.ArrayList;
import java.util.HashMap;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor     
@AllArgsConstructor
public class User {
	// l'id c'est le code ypareo de l'apprenant (Ypareo_Code dans la bdd ypareo_db)
	@Id
    private Long id;	

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type; 
    
    @Column(nullable = false)
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
    private List<Assignment> createdAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "grader", cascade = CascadeType.ALL)
    private List<PointsAward> givenAwards = new ArrayList<>();
    
    

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointsAward> receivedAwards = new ArrayList<>();

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
    
    @Column(nullable = false)
    private Boolean active = true;

    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void addClassGroup(ClassGroup classGroup) {
        if (!this.classGroups.contains(classGroup)) {
            this.classGroups.add(classGroup);
            classGroup.getTrainers().add(this);
        }
    }

    public void removeClassGroup(ClassGroup classGroup) {
        if (this.classGroups.contains(classGroup)) {
            this.classGroups.remove(classGroup);
            classGroup.getTrainers().remove(this);
        }
    }

    public List<Long> getClassGroupIds() {
        return classGroups.stream()
            .map(ClassGroup::getId)
            .collect(Collectors.toList());
    }

    public boolean teachesInClass(Long classGroupId) {
        return classGroups.stream()
            .anyMatch(cg -> cg.getId().equals(classGroupId));
    }

    public int getClassGroupCount() {
        return classGroups.size();
    }
}