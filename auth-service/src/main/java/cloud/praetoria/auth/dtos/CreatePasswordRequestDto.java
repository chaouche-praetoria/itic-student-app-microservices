package cloud.praetoria.auth.dtos;

import cloud.praetoria.auth.utils.Constantes;
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
    
    @NotBlank(message = Constantes.YPAREO_LOGIN)
    @Size(min = 3, max = 50, message = Constantes.YPAREO_ID_SIZE)
    private String ypareoLogin;
    
    @NotBlank(message = Constantes.PASSWORD_REQUIRED)
    @Size(min = 8, max = 50, message = Constantes.PASSWORD_SIZE)
    @Pattern(
        regexp = Constantes.PASSWORD_PATTERN,
        message = Constantes.PASSWORD_REQUIREMENT
    )
    private String password;
    
    @NotBlank(message = Constantes.PASSWORD_CONFIRMATION_REQUIRED)
    private String confirmPassword;
    
    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
