package cloud.praetoria.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoDto implements YpareoUserInfo {
    private String ypareoId;
    private String ypareoLogin;
    private String firstName;
    private String lastName;
    private String email;
    private String className;
    private Boolean isActive;
}