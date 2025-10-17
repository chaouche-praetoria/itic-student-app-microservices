package cloud.praetoria.gaming.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	  private Long id;
	    private String firstName;
	    private String lastName;
	    private String fullName;
	    private Integer points;
	    private Boolean active;
}
