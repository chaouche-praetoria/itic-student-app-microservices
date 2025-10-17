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
@Schema(description = "Création d'un devoir")
public class AssignmentCreateRequestDto {

    @NotBlank
    @Schema(example = "Devoir 1 - POO")
    private String title;

    @Schema(example = "Implémentez une liste chaînée en Java")
    private String description;

    @NotNull
    private EvalType type;

    @NotNull @Positive
    @Schema(example = "20")
    private Integer maxPoints;

    @Schema(example = "2025-11-01T23:59:00")
    private LocalDateTime dueDate;

    @NotNull
    @Schema(example = "42", description = "ID du créateur (trainer)")
    private Long creatorId;

    @NotNull
    @Schema(example = "100", description = "ID de la classe")
    private Long classGroupId;
}
