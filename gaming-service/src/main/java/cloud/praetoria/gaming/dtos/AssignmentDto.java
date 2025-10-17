package cloud.praetoria.gaming.dtos;

import cloud.praetoria.gaming.enums.EvalType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDto {
    private Long id;
    private String title;
    private String description;
    private EvalType type;
    private Integer maxPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private Boolean completed;
    private Boolean active;
    private Long creatorId;
    private Long classGroupId;
}