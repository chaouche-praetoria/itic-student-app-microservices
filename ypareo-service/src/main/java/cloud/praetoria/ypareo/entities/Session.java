package cloud.praetoria.ypareo.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long codeApprenant;

	@Column(length = 500)
	private String nomMatiere;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private LocalTime heureDebut;

	@Column(nullable = false)
	private LocalTime heureFin;

	private String duree;

	@Column(nullable = false)
	private Boolean isDistance;

	private Long codeSalle;

	@ElementCollection
	@CollectionTable(name = "session_apprenants", joinColumns = @JoinColumn(name = "code_seance"))
	@Column(name = "code_apprenant")
	private List<Long> codesApprenant;

	@ElementCollection
	@CollectionTable(name = "session_groupes", joinColumns = @JoinColumn(name = "code_seance"))
	@Column(name = "code_groupe")
	private List<Long> codesGroupe;

	@ElementCollection
	@CollectionTable(name = "session_personnel", joinColumns = @JoinColumn(name = "code_seance"))
	@Column(name = "code_personnel")
	private List<Long> codesPersonnel;
	
    @ElementCollection
    @CollectionTable(name = "session_salles", 
                     joinColumns = @JoinColumn(name = "code_seance"))
    @Column(name = "code_salle")
    private List<Long> codesSalle;

	@Column(name = "created_at")
	private java.time.LocalDateTime createdAt;

	@Column(name = "updated_at")
	private java.time.LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = java.time.LocalDateTime.now();
		updatedAt = java.time.LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = java.time.LocalDateTime.now();
	}
}