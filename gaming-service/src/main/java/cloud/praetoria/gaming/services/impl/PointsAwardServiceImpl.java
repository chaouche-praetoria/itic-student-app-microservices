package cloud.praetoria.gaming.services.impl;


import cloud.praetoria.gaming.dtos.GradeRequestDto;
import cloud.praetoria.gaming.dtos.GradeUpdateRequestDto;
import cloud.praetoria.gaming.dtos.PointsAwardDto;
import cloud.praetoria.gaming.entities.Assignment;
import cloud.praetoria.gaming.entities.PointsAward;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.mappers.PointsAwardMapper;
import cloud.praetoria.gaming.repositories.AssignmentRepository;
import cloud.praetoria.gaming.repositories.PointsAwardRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import cloud.praetoria.gaming.services.interfaces.PointsAwardServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointsAwardServiceImpl implements PointsAwardServiceInterface {

    private final PointsAwardRepository pointsAwardRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Override
    public PointsAwardDto gradeStudent(Long assignmentId, GradeRequestDto request) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        User grader = userRepository.findById(request.getGraderId()).orElseThrow(() -> new IllegalArgumentException("Grader not found: " + request.getGraderId()));

        User student = userRepository.findById(request.getStudentId()).orElseThrow(() -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

        // Garde-fous métier (placés ici plutôt qu'en @PrePersist/@PreUpdate)
        if (!grader.isTrainer()) {
            throw new SecurityException("Only a trainer can assign points");
        }
        if (!student.isStudent()) {
            throw new IllegalArgumentException("Target must be a student");
        }
        int max = assignment.getMaxPoints();
        if (request.getPoints() < 0 || request.getPoints() > max) {
            throw new IllegalArgumentException("Points must be between 0 and " + max);
        }

        // Upsert (une note par élève et par devoir)
        PointsAward award = pointsAwardRepository.findByAssignmentIdAndStudentId(assignmentId, request.getStudentId()).orElseGet(PointsAward::new);

        award.setAssignment(assignment);
        award.setGrader(grader);
        award.setStudent(student);
        award.setPointsEarned(request.getPoints());
        award.setComment(request.getComment());

        PointsAward saved = pointsAwardRepository.save(award);
        return PointsAwardMapper.toDto(saved);
    }

    @Override
    @Transactional()
    public Optional<PointsAwardDto> getStudentGrade(Long assignmentId, Long studentId) {
        return pointsAwardRepository.findByAssignmentIdAndStudentId(assignmentId, studentId).map(PointsAwardMapper::toDto);
    }

    @Override
    public PointsAwardDto updateGrade(Long awardId, GradeUpdateRequestDto request) {
        PointsAward award = pointsAwardRepository.findById(awardId).orElseThrow(() -> new IllegalArgumentException("PointsAward not found: " + awardId));

        int max = award.getAssignment().getMaxPoints();
        if (request.getPoints() < 0 || request.getPoints() > max) {
            throw new IllegalArgumentException("Points must be between 0 and " + max);
        }

        award.setPointsEarned(request.getPoints());
        award.setComment(request.getComment());

        return PointsAwardMapper.toDto(pointsAwardRepository.save(award));
    }

    @Override
    public void deleteGrade(Long awardId) {
        if (!pointsAwardRepository.existsById(awardId)) {
            return; // idempotent
        }
        pointsAwardRepository.deleteById(awardId);
    }

    @Override
    @Transactional()
    public List<PointsAwardDto> listAssignmentGrades(Long assignmentId) {
        return pointsAwardRepository.findAllByAssignmentId(assignmentId).stream().map(PointsAwardMapper::toDto).toList();
    }
}
