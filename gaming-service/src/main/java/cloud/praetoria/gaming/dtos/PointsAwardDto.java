package cloud.praetoria.gaming.dtos;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Attribution de points pour un devoir")
public class PointsAwardDto {
    private Long id;
    private Integer pointsEarned;
    private LocalDateTime awardedAt;
    private LocalDateTime updatedAt;
    private String comment;
    private Long graderId;
    private Long studentId;
    private Long assignmentId;

    @Schema(description = "Pourcentage obtenu (= pointsEarned / maxPoints * 100)")
    private Double percent;

    @Schema(description = "Max points de l'assignement au moment de la réponse")
    private Integer assignmentMaxPoints;
}