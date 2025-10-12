package cloud.praetoria.gaming.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloud.praetoria.gaming.entities.Formation;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    Optional<Formation> findByKeyName(String keyName);
}

