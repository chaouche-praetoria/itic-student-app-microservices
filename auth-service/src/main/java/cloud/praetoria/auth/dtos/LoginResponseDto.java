package cloud.praetoria.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    
	private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Boolean isFirstLogin;
    private UserInfoDto userInfo;
    
    @Builder.Default
    private String tokenType = "Bearer";
}