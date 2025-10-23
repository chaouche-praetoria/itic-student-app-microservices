package cloud.praetoria.gaming.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloud.praetoria.gaming.entities.Formation;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    Optional<Formation> findByKeyName(String keyName);
    @Query("SELECT DISTINCT f FROM Formation f " +
            "JOIN f.classes cg " +
            "JOIN cg.trainers t " +
            "WHERE t.id = :trainerId " +
            "ORDER BY f.displayName")
     List<Formation> findByTrainerId(@Param("trainerId") Long trainerId);
     Optional<Formation> findByYpareoFormationCode(Long ypareoFormationCode);
     
     @Query("SELECT f FROM Formation f ORDER BY f.displayName")
     List<Formation> findAllOrderByDisplayName();
 }
