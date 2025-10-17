package cloud.praetoria.gaming.services.impl;

import java.util.List;

import cloud.praetoria.gaming.services.interfaces.AssignmentServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.gaming.dtos.*;
import cloud.praetoria.gaming.entities.Assignment;
import cloud.praetoria.gaming.entities.ClassGroup;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.mappers.AssignmentMapper;
import cloud.praetoria.gaming.repositories.AssignmentRepository;
import cloud.praetoria.gaming.repositories.ClassGroupRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentServiceImpl implements AssignmentServiceInterface {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;

    @Override
    public AssignmentDto create(AssignmentCreateRequestDto request) {
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator not found: " + request.getCreatorId()));
        if (!creator.isTrainer()) {
            throw new SecurityException("Only a trainer can create an assignment");
        }

        ClassGroup group = classGroupRepository.findById(request.getClassGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Class group not found: " + request.getClassGroupId()));

        Assignment a = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .maxPoints(request.getMaxPoints())
                .dueDate(request.getDueDate())
                .completed(false)
                .active(true)
                .creator(creator)
                .classGroup(group)
                .build();

        Assignment saved = assignmentRepository.save(a);
        return AssignmentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentDto getById(Long id) {
        Assignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));
        return AssignmentMapper.toDto(a);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentDto> listByClass(Long classGroupId) {
        // Vérifie que la classe existe (retourne 404 si inexistante)
        classGroupRepository.findById(classGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Class group not found: " + classGroupId));

        return assignmentRepository.findAllByClassGroupIdOrderByCreatedAtDesc(classGroupId)
                .stream().map(AssignmentMapper::toDto).toList();
    }

    @Override
    public AssignmentDto update(Long id, AssignmentUpdateRequestDto request) {
        Assignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));

        // Si maxPoints change, tu peux ajouter ici une politique de recalcul/validation
        a.setTitle(request.getTitle());
        a.setDescription(request.getDescription());
        a.setType(request.getType());
        a.setMaxPoints(request.getMaxPoints());
        a.setDueDate(request.getDueDate());

        if (request.getCompleted() != null) a.setCompleted(request.getCompleted());
        if (request.getActive() != null) a.setActive(request.getActive());

        return AssignmentMapper.toDto(assignmentRepository.save(a));
    }

    @Override
    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) return;
        assignmentRepository.deleteById(id);
    }

    @Override
    public AssignmentDto markCompleted(Long id, boolean completed) {
        Assignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));
        a.setCompleted(completed);
        return AssignmentMapper.toDto(assignmentRepository.save(a));
    }

    @Override
    public AssignmentDto setActive(Long id, boolean active) {
        Assignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));
        a.setActive(active);
        return AssignmentMapper.toDto(assignmentRepository.save(a));
    }
}
