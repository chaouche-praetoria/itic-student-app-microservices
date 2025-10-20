package cloud.praetoria.auth.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Schema(description = "Réponse d'erreur standardisée")
public class ErrorResponse {

    @Schema(description = "Timestamp ISO-8601 de l'erreur", example = "2025-10-20T14:00:00+02:00")
    private OffsetDateTime timestamp;

    @Schema(description = "Code HTTP", example = "400")
    private int status;

    @Schema(description = "Code interne d'erreur", example = "TEACHER_ALREADY_REGISTERED")
    private String code;

    @Schema(description = "Message lisible pour l'utilisateur", example = "Ce formateur est déjà inscrit")
    private String message;

    @Schema(description = "Détails supplémentaires (optionnel)", example = "Le champ email est déjà utilisé")
    private String details;

    public ErrorResponse() {}

    public ErrorResponse(int status, String code, String message, String details) {
        this.timestamp = OffsetDateTime.now();
        this.status = status;
        this.code = code;
        this.message = message;
        this.details = details;
    }
}
