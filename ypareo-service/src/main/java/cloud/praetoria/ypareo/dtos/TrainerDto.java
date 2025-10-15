package cloud.praetoria.ypareo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDto {
    private Long id;
    private Long ypareoCode;
    private String firstName;
    private String lastName;
}
