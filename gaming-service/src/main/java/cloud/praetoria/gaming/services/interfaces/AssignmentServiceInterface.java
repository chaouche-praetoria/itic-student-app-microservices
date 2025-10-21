package cloud.praetoria.gaming.services.interfaces;

import cloud.praetoria.gaming.dtos.AssignmentCreateRequestDto;
import cloud.praetoria.gaming.dtos.AssignmentDto;
import cloud.praetoria.gaming.dtos.AssignmentUpdateRequestDto;

import java.util.List;

public interface AssignmentServiceInterface {
    AssignmentDto create(AssignmentCreateRequestDto request);
    AssignmentDto getById(Long id);
    List<AssignmentDto> listByClass(Long classGroupId);
    AssignmentDto update(Long id, AssignmentUpdateRequestDto request);
    void delete(Long id);
    AssignmentDto markCompleted(Long id, boolean completed);
    AssignmentDto setActive(Long id, boolean active);
    List<AssignmentDto> listByFormation(Long formationId);
}
