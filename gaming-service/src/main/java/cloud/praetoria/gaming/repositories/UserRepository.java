package cloud.praetoria.gaming.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.enums.UserType;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByType(UserType type);
    List<User> findByTypeAndActive(UserType type, Boolean active);
}