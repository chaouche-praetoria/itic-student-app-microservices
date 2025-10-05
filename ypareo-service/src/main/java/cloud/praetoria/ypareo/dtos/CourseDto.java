package cloud.praetoria.ypareo.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id;
    private Long ypareoCode;
    private String subjectLabel; 
    private String roomLabel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean cancelled;
    private boolean online;
    private String groupLabel;  
    private String trainerName;  
}
