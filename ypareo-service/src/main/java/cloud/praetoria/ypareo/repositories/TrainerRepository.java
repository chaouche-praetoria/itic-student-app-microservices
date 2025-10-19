package cloud.praetoria.ypareo.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.ypareo.entities.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
   
	Optional<Trainer> findByYpareoCode(Long ypareoCode);

    List<Trainer> findByLastNameIgnoreCase(String lastName);
    Optional<Trainer> findByLoginIgnoreCase(String login);
    
    boolean existsByLogin(String login);



}
