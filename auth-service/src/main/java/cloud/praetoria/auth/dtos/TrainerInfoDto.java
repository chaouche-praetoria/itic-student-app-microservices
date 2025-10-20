package cloud.praetoria.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerInfoDto  implements YpareoUserInfo {
    private String ypareoCode;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    
    @Override
    public String getYpareoId() {
        return ypareoCode; 
    }
    
    @Override
    public String getYpareoLogin() {
        return login;
    }
    
    @Override
    public String getFirstName() {
        return firstName;
    }
    
    @Override
    public String getLastName() {
        return lastName;
    }
    
    @Override
    public String getEmail() {
        return email;
    }

}
