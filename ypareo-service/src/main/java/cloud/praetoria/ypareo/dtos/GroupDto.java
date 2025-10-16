package cloud.praetoria.ypareo.dtos;

import java.time.LocalDate;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDto {
    private Long id;
    private Long CodeGroup;
    private String label;       
    private String shortLabel; 
    private LocalDate dateDebut; 
    private LocalDate dateFin;
}
