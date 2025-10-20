package cloud.praetoria.auth.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    TEACHER_ALREADY_REGISTERED("TEACHER_ALREADY_REGISTERED", "Ce formateur est déjà inscrit", 400),
    USER_NOT_FOUND("USER_NOT_FOUND", "Utilisateur introuvable", 404),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Identifiants invalides", 401),
    BAD_REQUEST("BAD_REQUEST", "Requête invalide", 400),
    INTERNAL_ERROR("INTERNAL_ERROR", "Erreur interne du serveur", 500);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}