package cloud.praetoria.gaming.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Demande de notation d'un élève pour un devoir")
public class GradeRequestDto {

    @NotNull
    @PositiveOrZero
    @Schema(example = "15", description = "Points attribués")
    private Integer points;

    @Schema(example = "Bon travail, attention aux détails sur la question 3")
    private String comment;

    @NotNull
    @Schema(example = "42", description = "ID du correcteur (trainer)")
    private Long graderId;

    @NotNull
    @Schema(example = "1001", description = "ID de l'élève à noter")
    private Long studentId;
}