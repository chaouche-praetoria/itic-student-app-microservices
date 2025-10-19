package cloud.praetoria.gaming.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    /**
     * Trouve toutes les présences d'un étudiant
     * @param userId ID de l'étudiant
     * @return Liste des présences
     */
    List<Attendance> findByUserIdOrderByAttendanceDateDesc(Long userId);
    
    /**
     * Trouve les présences d'un étudiant sur une période
     * @param userId ID étudiant
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des présences
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Compte les absences d'un étudiant
     * @param userId ID étudiant
     * @return Nombre d'absences
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.status = 'ABSENT'")
    long countAbsences(@Param("userId") Long userId);
    
    /**
     * Compte les retards d'un étudiant
     * @param userId ID étudiant
     * @return Nombre de retards
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.status = 'LATE'")
    long countLateArrivals(@Param("userId") Long userId);
    
    /**
     * Compte les présences sur une période
     * @param userId ID étudiant
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Nombre de présences
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.status = 'PRESENT' AND a.attendanceDate BETWEEN :startDate AND :endDate")
    long countPresentBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Vérifie si un étudiant a des absences sur une période
     * @param userId ID étudiant
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return true si aucune absence
     */
    @Query("SELECT CASE WHEN COUNT(a) = 0 THEN true ELSE false END FROM Attendance a WHERE a.user.id = :userId AND a.status = 'ABSENT' AND a.attendanceDate BETWEEN :startDate AND :endDate")
    boolean hasNoAbsenceBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Vérifie si un étudiant a des retards sur une période
     * @param userId ID étudiant
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return true si aucun retard
     */
    @Query("SELECT CASE WHEN COUNT(a) = 0 THEN true ELSE false END FROM Attendance a WHERE a.user.id = :userId AND a.status = 'LATE' AND a.attendanceDate BETWEEN :startDate AND :endDate")
    boolean hasNoLateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Calcule le taux de présence
     * @param userId ID étudiant
     * @return Taux de présence en pourcentage
     */
    @Query("SELECT (COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0 / COUNT(*)) FROM Attendance a WHERE a.user.id = :userId")
    Double calculateAttendanceRate(@Param("userId") Long userId);
    
    /**
     * Trouve les présences par classe
     * @param userId ID étudiant
     * @param classGroupCode Code classe
     * @return Liste des présences
     */
    List<Attendance> findByUserIdAndClassGroupCode(Long userId, String classGroupCode);
}
