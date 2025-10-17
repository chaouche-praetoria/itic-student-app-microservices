package cloud.praetoria.gaming.dtos;

import java.time.LocalDateTime;
import cloud.praetoria.gaming.enums.EvalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Mise à jour d'un devoir")
public class AssignmentUpdateRequestDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private EvalType type;

    @NotNull @Positive
    private Integer maxPoints;

    private LocalDateTime dueDate;

    @Schema(description = "Marquer comme complété (optionnel)")
    private Boolean completed;

    @Schema(description = "Activer/Désactiver (optionnel)")
    private Boolean active;
}
