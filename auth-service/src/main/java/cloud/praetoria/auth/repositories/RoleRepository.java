package cloud.praetoria.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.auth.entities.Role;
import cloud.praetoria.auth.enums.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	 Optional<Role> findByRoleName(RoleName roleName);
}
