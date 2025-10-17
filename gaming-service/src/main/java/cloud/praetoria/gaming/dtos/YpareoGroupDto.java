package cloud.praetoria.gaming.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class YpareoGroupDto {
    private Long id;
    private String label;
    private String shortLabel;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long codeGroup;
    private String fullLabel;
}
