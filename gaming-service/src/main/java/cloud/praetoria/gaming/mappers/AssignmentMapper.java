package cloud.praetoria.gaming.mappers;

import cloud.praetoria.gaming.dtos.AssignmentDto;
import cloud.praetoria.gaming.entities.Assignment;

public final class AssignmentMapper {
    private AssignmentMapper() {}

    public static AssignmentDto toDto(Assignment a) {
        if (a == null) return null;
        return AssignmentDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .type(a.getType())
                .maxPoints(a.getMaxPoints())
                .createdAt(a.getCreatedAt())
                .dueDate(a.getDueDate())
                .completed(a.getCompleted())
                .active(a.getActive())
                .creatorId(a.getCreator() != null ? a.getCreator().getId() : null)
                .classGroupId(a.getClassGroup() != null ? a.getClassGroup().getId() : null)
                .build();
    }
}
