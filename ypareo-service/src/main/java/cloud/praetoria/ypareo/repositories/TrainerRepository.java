package cloud.praetoria.ypareo.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.ypareo.entities.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * Find a trainer by its YParéo unique code.
     * @param ypareoCode the YParéo personnel code (codePersonnel)
     * @return Optional<Trainer>
     */
    Optional<Trainer> findByYpareoCode(Long ypareoCode);

    /**
     * Find trainers by last name (case insensitive).
     * @param lastName trainer's last name
     * @return list of trainers
     */
    List<Trainer> findByLastNameIgnoreCase(String lastName);

    /**
     * Find trainers by email (case insensitive).
     * @param email trainer's email
     * @return Optional<Trainer>
     */
    Optional<Trainer> findByEmailIgnoreCase(String email);

}
