package cloud.praetoria.auth.dtos;

import java.time.LocalDateTime;

import cloud.praetoria.auth.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

	private Long id;
	private String ypareoId;
	private String ypareoLogin;
	private String email;
	private String firstName;
	private String lastName;
	private String fullName;
	private Boolean isFirstLogin;
	private Boolean isActive;
	private LocalDateTime lastLogin;
	private LocalDateTime createdAt;
	private RoleName rolename;
}