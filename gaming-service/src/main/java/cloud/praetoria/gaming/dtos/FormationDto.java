package cloud.praetoria.gaming.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormationDto {
    private Long id;
    private String keyName;
    private String displayName;
    private Long ypareoFormationCode;
}
