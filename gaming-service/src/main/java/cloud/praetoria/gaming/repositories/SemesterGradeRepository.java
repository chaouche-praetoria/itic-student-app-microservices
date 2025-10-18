package cloud.praetoria.gaming.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.SemesterGrade;

@Repository
public interface SemesterGradeRepository extends JpaRepository<SemesterGrade, Long> {
    
    /**
     * Trouve toutes les notes d'un étudiant
     * @param userId ID de l'étudiant
     * @return Liste des notes semestrielles
     */
    List<SemesterGrade> findByUserIdOrderBySemesterEndDateDesc(Long userId);
    
    /**
     * Trouve la note d'un semestre spécifique
     * @param userId ID étudiant
     * @param semesterCode Code semestre (S1, S2, etc.)
     * @param academicYear Année académique
     * @return Note trouvée
     */
    Optional<SemesterGrade> findByUserIdAndSemesterCodeAndAcademicYear(Long userId, String semesterCode, String academicYear);
    
    /**
     * Trouve le semestre en cours d'un étudiant
     * @param userId ID étudiant
     * @return Semestre en cours
     */
    Optional<SemesterGrade> findByUserIdAndCurrentSemesterTrue(Long userId);
    
    /**
     * Trouve tous les semestres avec moyenne S-Tier (>= 16/20)
     * @param userId ID étudiant
     * @return Liste des semestres S-Tier
     */
    @Query("SELECT sg FROM SemesterGrade sg WHERE sg.user.id = :userId AND sg.averageGrade >= 16.0 AND sg.validated = true")
    List<SemesterGrade> findSTierSemesters(@Param("userId") Long userId);
    
    /**
     * Compte le nombre de semestres validés
     * @param userId ID étudiant
     * @return Nombre de semestres validés
     */
    long countByUserIdAndValidatedTrue(Long userId);
    
    /**
     * Trouve les semestres par formation
     * @param userId ID étudiant
     * @param formationCode Code formation
     * @return Liste des semestres
     */
    List<SemesterGrade> findByUserIdAndFormationCode(Long userId, String formationCode);
    
    /**
     * Calcule la moyenne générale de tous les semestres
     * @param userId ID étudiant
     * @return Moyenne générale
     */
    @Query("SELECT AVG(sg.averageGrade) FROM SemesterGrade sg WHERE sg.user.id = :userId AND sg.validated = true")
    Double calculateOverallAverage(@Param("userId") Long userId);
    
    /**
     * Vérifie si un étudiant a obtenu une mention sur un semestre
     * @param userId ID étudiant
     * @param mention Mention recherchée
     * @return true si mention obtenue
     */
    boolean existsByUserIdAndMention(Long userId, String mention);
}