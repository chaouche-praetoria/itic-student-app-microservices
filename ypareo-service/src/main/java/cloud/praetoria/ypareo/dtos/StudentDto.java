package cloud.praetoria.ypareo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long id;
    private Long ypareoCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String postalCode;
    private String address;
    private String groupLabel; 
}
