package cloud.praetoria.ypareo.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_ypareo_code", columnList = "ypareo_code"),
    @Index(name = "idx_student_login", columnList = "login"),
    @Index(name = "idx_student_email", columnList = "email"),
    @Index(name = "idx_student_group", columnList = "group_id"),
    @Index(name = "idx_student_nom_prenom", columnList = "last_name, first_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ypareo_code", nullable = false, unique = true)
    private Long ypareoCode; 
    
    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName; 
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "email", unique = true, length = 100)
    private String email; 
    
    @Column(name = "birth_date")
    private LocalDate birthDate; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group; 
    
    @Column(name = "is_pending")
    private boolean pending = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getDisplayName() {
        return lastName + " " + (firstName != null ? firstName.substring(0, 1).toUpperCase() + "." : "");
    }
}
