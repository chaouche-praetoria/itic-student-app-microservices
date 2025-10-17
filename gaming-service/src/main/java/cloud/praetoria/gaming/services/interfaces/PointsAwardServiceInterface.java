package cloud.praetoria.gaming.services.interfaces;

import java.util.List;
import java.util.Optional;

import cloud.praetoria.gaming.dtos.GradeRequestDto;
import cloud.praetoria.gaming.dtos.GradeUpdateRequestDto;
import cloud.praetoria.gaming.dtos.PointsAwardDto;

public interface PointsAwardServiceInterface {

    /** Crée / met à jour la note d'un élève pour un devoir (upsert) */
    PointsAwardDto gradeStudent(Long assignmentId, GradeRequestDto request);

    /** Récupère la note d'un élève pour un devoir */
    Optional<PointsAwardDto> getStudentGrade(Long assignmentId, Long studentId);

    /** Met à jour une attribution spécifique (par id) */
    PointsAwardDto updateGrade(Long awardId, GradeUpdateRequestDto request);

    /** Supprime une attribution */
    void deleteGrade(Long awardId);

    /** Liste toutes les attributions d'un devoir */
    List<PointsAwardDto> listAssignmentGrades(Long assignmentId);
}