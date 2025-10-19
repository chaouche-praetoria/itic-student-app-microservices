package cloud.praetoria.auth.dtos;

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
public class LoginRequestDto {
    
    @NotBlank(message = "Ypareo login is required")
    @Size(min = 2, max = 50, message = "Ypareo login must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "Ypareo login must contain only letters")
    private String ypareoLogin;
    
    @NotBlank(message = "Password is required")
    private String password;
}