package cloud.praetoria.ypareo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.ypareo.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

	/**
	 * Find a student by their YParéo unique code.
	 * 
	 * @param ypareoCode the YParéo student code
	 * @return Optional<Student>
	 */
	Optional<Student> findByYpareoCode(Long ypareoCode);

	/**
	 * Find all students belonging to a given group ID.
	 * 
	 * @param groupId the internal database group ID
	 * @return list of students
	 */
	List<Student> findByGroup_Id(Long groupId);

	/**
	 * Find all students by last name (case insensitive).
	 * 
	 * @param lastName the student's last name
	 * @return list of students
	 */
	List<Student> findByLastNameIgnoreCase(String lastName);

	/**
	 * Find all students whose email contains a keyword. Useful for quick search or
	 * debugging.
	 */
	List<Student> findByEmailContainingIgnoreCase(String emailPart);

	@Query("SELECT s FROM Student s LEFT JOIN FETCH s.group")
	List<Student> findAllWithGroup();

	Optional<Student> findByEmail(String email);

	Optional<Student> findByLogin(String login);

	/**
	 * Récupère les étudiants d'un groupe avec le groupe chargé (pas de lazy
	 * loading)
	 */
	@Query("SELECT s FROM Student s LEFT JOIN FETCH s.group WHERE s.group.id = :groupId")
	List<Student> findByGroupIdWithGroup(@Param("groupId") Long groupId);
}
