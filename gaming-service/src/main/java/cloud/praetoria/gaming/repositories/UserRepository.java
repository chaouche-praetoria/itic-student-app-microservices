package cloud.praetoria.gaming.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.enums.UserType;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByType(UserType type);
    List<User> findByTypeAndActive(UserType type, Boolean active);
    
    @Query("SELECT DISTINCT u FROM User u " +
    	       "JOIN u.classGroups cg " +
    	       "WHERE u.type = 'STUDENT' " +
    	       "AND cg.id IN :classGroupIds " +
    	       "AND u.active = true")
    	List<User> findStudentsByClassGroupIds(@Param("classGroupIds") List<Long> classGroupIds);
}