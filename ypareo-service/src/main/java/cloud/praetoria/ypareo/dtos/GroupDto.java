package cloud.praetoria.ypareo.dtos;

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
    private Long ypareoCode;
    private String label;       
    private String shortLabel; 
    private String formationLabel;
}
