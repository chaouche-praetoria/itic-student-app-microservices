package cloud.praetoria.gaming.entities;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "class_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // 🆕 FIX StackOverflow
@ToString(exclude = {"trainers", "students", "assignments", "formation"})  // 🆕 FIX StackOverflow
public class ClassGroup {

    @Id
    @EqualsAndHashCode.Include  // 🆕 Uniquement l'ID dans hashCode/equals
    private Long id;
    
    @Column(nullable = false)
    private String label;
    
    @Column(name = "date_debut")
    private LocalDate dateDebut; 
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    @ManyToMany(mappedBy = "classGroups")
    @JsonIgnore
    @Builder.Default
    private Set<User> trainers = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;
    
    @ManyToMany(mappedBy = "classGroups")
    @JsonIgnore
    @Builder.Default
    private Set<User> students = new HashSet<>();
}