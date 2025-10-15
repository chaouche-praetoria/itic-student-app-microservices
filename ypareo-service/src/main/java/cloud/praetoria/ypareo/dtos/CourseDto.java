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
public class CourseDto {
	   private Long codeApprenant;
       private String nomMatiere;
       private List<Long> codesApprenant;
       private List<Long> codesGroupe;
       private String date; 
       private String heureDebut; 
       private String heureFin;
       private String duree;
       private List<Long> codesPersonnel;
       private List<Long> codesSalle;
       private Integer isDistance;
}