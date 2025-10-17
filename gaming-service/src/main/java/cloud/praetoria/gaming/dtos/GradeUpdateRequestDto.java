package cloud.praetoria.gaming.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Mise à jour d'une attribution de points")
public class GradeUpdateRequestDto {

    @NotNull
    @PositiveOrZero
    @Schema(example = "18", description = "Nouveaux points attribués")
    private Integer points;

    @Schema(example = "Réévaluation après réclamation")
    private String comment;
}