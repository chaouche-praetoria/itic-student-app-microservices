package cloud.praetoria.ypareo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YpareoAbsenceDto {
    private Integer codeAbsence;
    private Long codeApprenant;
    private Integer codeGroupe;
    private String dateDeb;
    private String heureDeb;
    private String dateFin;
    private String heureFin;
    private Integer duree;
    private Boolean isJustifie;
    private Integer codeMotifAbsence;
    private String libelleMotifAbsence;
}
