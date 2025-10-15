package cloud.praetoria.ypareo.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
	private Long codeSeance;
	private String nomMatiere;
	private LocalDate date;
	private LocalTime heureDebut;
	private LocalTime heureFin;
	private String duree;
	private Boolean isDistance;

	private List<String> nomsFormateurs; // À remplir depuis la table trainers
	private List<String> nomsSalles; // À remplir depuis la table rooms
	private Integer nombreApprenants;

	// Pour l'app mobile je me dit que ça peut être utile (maquette figma)	
	private String heureDebutFormatted; // "09h00"
	private String heureFinFormatted; // "11h00"
	private String dateFormatted; // "Lundi 20 octobre"
}
