package cloud.praetoria.auth.dtos;

import cloud.praetoria.auth.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInfo {

	private String ypareoId;
    private String email;
    private String firstName;
    private String lastName;
    private String className;
    private Boolean isActive;
    private String phoneNumber;
    private Role roleName;
}