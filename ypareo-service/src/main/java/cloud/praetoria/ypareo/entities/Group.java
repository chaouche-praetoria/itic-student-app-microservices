package cloud.praetoria.ypareo.entities;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ygroups", indexes = {
    @Index(name = "idx_group_code_group", columnList = "code_group"),
    @Index(name = "idx_group_label", columnList = "label"),
    @Index(name = "idx_group_short_label", columnList = "short_label"),
    @Index(name = "idx_group_dates", columnList = "date_debut, date_fin"),
    @Index(name = "idx_group_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "code_group", nullable = false, unique = true)
    private Long codeGroupe; 

    @Column(name = "label", nullable = false, length = 150)
    private String label; 

    @Column(name = "short_label", length = 50)
    private String shortLabel; 

    
    
    @Column(name = "date_debut")
    private LocalDate dateDebut; 
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Student> students;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Course> courses;


    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isCurrentlyActive() {
        if (dateDebut == null || dateFin == null) return false;
        LocalDate now = LocalDate.now();
        return !now.isBefore(dateDebut) && !now.isAfter(dateFin);
    }
    
}
