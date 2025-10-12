package cloud.praetoria.ypareo.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.ypareo.entities.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Find a group by its YParéo unique code.
     * @param ypareoCode the YParéo group code (codeGroupe)
     * @return Optional<Group>
     */
    Optional<Group> findByCodeGroupe(Long ypareoCode);

    /**
     * Find a group by its short label (e.g. "M1-BD-ALT").
     * @param shortLabel the abbreviated group label
     * @return Optional<Group>
     */
    Optional<Group> findByShortLabel(String shortLabel);
}
