package cloud.praetoria.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePasswordRequestDto {
    
    @NotBlank(message = "Ypareo login is required")
    @Size(min = 2, max = 50, message = "Ypareo login must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "Ypareo login must contain only letters")
    @Schema(
            description = "Ypareo login (letters only)",
            example = "CHAOUCHE",
            minLength = 2,
            maxLength = 50,
            pattern = "^[A-Za-z]{2,50}$"
    )
    private String ypareoLogin;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#-])[A-Za-z\\d@$!%*?&.#-]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    )
    @Schema(
            description = "User password. Must contain at least one uppercase letter, one lowercase letter, one number and one special character",
            example = "Password@2025",
            minLength = 8,
            maxLength = 100,
            pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#-])[A-Za-z\\d@$!%*?&.#-]{8,}$"
    )
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation, must match password", example = "Password@2025")
    private String confirmPassword;
    
    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
