package cloud.praetoria.gaming.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	// Assignments created by a trainer
	@Query("SELECT a FROM Assignment a WHERE a.creator.id = :creatorId AND a.active = true "
			+ "ORDER BY a.createdAt DESC")
	List<Assignment> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);

	@Query("SELECT a FROM Assignment a WHERE a.creator.id = :creatorId "
			+ "AND a.completed = false AND a.active = true ORDER BY a.createdAt DESC")
	List<Assignment> findAssignmentsInProgress(@Param("creatorId") Long creatorId);

	@Query("SELECT a FROM Assignment a WHERE a.creator.id = :creatorId AND a.active = true "
			+ "ORDER BY a.createdAt DESC")
	List<Assignment> findTop5ByCreatorIdOrderByCreatedAtDesc(@Param("creatorId") Long creatorId, Pageable pageable);

	List<Assignment> findAllByFormationIdOrderByCreatedAtDesc(Long formationId);

}
