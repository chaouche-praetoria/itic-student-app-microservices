package cloud.praetoria.ypareo.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.ypareo.entities.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    // séances d'un apprenant pour une période
    @Query("SELECT s FROM Session s WHERE :codeApprenant MEMBER OF s.codesApprenant " +
           "AND s.date BETWEEN :dateDebut AND :dateFin ORDER BY s.date, s.heureDebut")
    List<Session> findByApprenantAndDateBetween(
        @Param("codeApprenant") Long codeApprenant,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    // séances du jour pour un apprenant
    @Query("SELECT s FROM Session s WHERE :codeApprenant MEMBER OF s.codesApprenant " +
           "AND s.date = :date ORDER BY s.heureDebut")
    List<Session> findByApprenantAndDate(
        @Param("codeApprenant") Long codeApprenant,
        @Param("date") LocalDate date
    );
    
    // séances d'un groupe je me dit ça peut être utile mais ptet faudra supprimer si pas besoin
    @Query("SELECT s FROM Session s WHERE :codeGroupe MEMBER OF s.codesGroupe " +
           "AND s.date BETWEEN :dateDebut AND :dateFin ORDER BY s.date, s.heureDebut")
    List<Session> findByGroupeAndDateBetween(
        @Param("codeGroupe") Long codeGroupe,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    void deleteByDateBefore(LocalDate date);
}
