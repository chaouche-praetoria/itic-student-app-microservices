package cloud.praetoria.gaming.mappers;

import cloud.praetoria.gaming.dtos.PointsAwardDto;
import cloud.praetoria.gaming.entities.Assignment;
import cloud.praetoria.gaming.entities.PointsAward;

public final class PointsAwardMapper {
    private PointsAwardMapper() {}

    public static PointsAwardDto toDto(PointsAward entity) {
        if (entity == null) return null;

        Assignment a = entity.getAssignment();
        Integer max = (a != null) ? a.getMaxPoints() : null;
        Integer pts = entity.getPointsEarned();
        Double percent = (max != null && max > 0 && pts != null)
                ? (pts * 100.0) / max
                : null;

        return PointsAwardDto.builder()
                .id(entity.getId())
                .pointsEarned(entity.getPointsEarned())
                .awardedAt(entity.getAwardedAt())
                .comment(entity.getComment())
                .graderId(entity.getGrader() != null ? entity.getGrader().getId() : null)
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .assignmentId(a != null ? a.getId() : null)
                .percent(percent)
                .assignmentMaxPoints(max)
                .build();
    }
}
