package cloud.praetoria.gaming.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.PointsAward;

@Repository
public interface PointsAwardRepository extends JpaRepository<PointsAward, Long> {

    List<PointsAward> findByStudentIdOrderByAwardedAtDesc(Long studentId);

    List<PointsAward> findByAssignmentId(Long assignmentId);


    Optional<PointsAward> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    List<PointsAward> findByGraderIdOrderByAwardedAtDesc(Long graderId);
}
