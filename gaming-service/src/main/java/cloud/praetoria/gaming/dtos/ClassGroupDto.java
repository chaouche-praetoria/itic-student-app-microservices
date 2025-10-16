package cloud.praetoria.gaming.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClassGroupDto {
    private Long id;
    private String label;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    private FormationDto formation;
}
