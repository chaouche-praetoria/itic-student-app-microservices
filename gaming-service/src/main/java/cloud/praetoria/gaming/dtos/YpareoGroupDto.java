package cloud.praetoria.gaming.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class YpareoGroupDto {
    private Long ypareoCode;
    private String label;    
    private String fullLabel;
    private String shortLabel; 
    private LocalDate dateDebut; 
    private LocalDate dateFin;
}
