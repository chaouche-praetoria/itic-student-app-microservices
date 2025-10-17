package cloud.praetoria.ypareo.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YpareoCourseDto {
    private Long codeApprenant;
    private String nomMatiere;
    private String date;
    private String heureDebut;
    private String heureFin;
    private String duree;
    private Integer isDistance;
    private List<Long> codesSalle;
    private List<Long> codesApprenant;
    private List<Long> codesPersonnel;
}
