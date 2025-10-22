package cloud.praetoria.gaming.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationDto {
    private Long id;
    private String displayName;
    private int totalStudents;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    
    @Builder.Default
    private List<ClassGroupSummaryDto> classGroups = new ArrayList<>();
}
