package cloud.praetoria.auth.dtos;

import cloud.praetoria.auth.utils.Constantes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    
    @NotBlank(message = Constantes.YPAREO_LOGIN)
    @Size(min = 3, max = 50, message = Constantes.YPAREO_LOGIN_SIZE)
    private String ypareoLogin;
    
    @NotBlank(message = Constantes.PASSWORD_REQUIRED)
    @Size(min = 8, message = Constantes.PASSWORD_SIZE)
    private String password;
}