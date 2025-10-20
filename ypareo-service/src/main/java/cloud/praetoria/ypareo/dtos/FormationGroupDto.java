package cloud.praetoria.ypareo.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant une formation avec ses groupes ALT et INIT regroupés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationGroupDto {
    
    private String formationId;
    private String label;
    private String shortLabel;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<SubGroupDto> groups;
    private Integer totalStudents;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubGroupDto {
        private Long id;
        private String type;
        private Long codeGroup;
        private Integer studentCount;
    }
}