package cloud.praetoria.gaming.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroupSummaryDto {
    private Long id;
    private String label;
    private int totalStudents;
}