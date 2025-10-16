package cloud.praetoria.gaming.dtos;

import lombok.Data;

import java.util.List;

@Data
public class TrainerClassGroupsResponse {
    private Long trainerId;
    private String trainerName;
    private List<ClassGroupDto> classGroups;
    private List<FormationDto> formations;
}
