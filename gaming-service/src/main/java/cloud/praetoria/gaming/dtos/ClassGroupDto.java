package cloud.praetoria.gaming.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroupDto {
    private Long id;
    private String label;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    private FormationDto formation;
}