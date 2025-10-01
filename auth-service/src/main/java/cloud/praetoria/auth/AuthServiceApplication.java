package cloud.praetoria.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import cloud.praetoria.auth.entities.Role;
import cloud.praetoria.auth.enums.RoleName;
import cloud.praetoria.auth.repositories.RoleRepository;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(RoleRepository roleRepository) {
	    return args -> {
	    	
	    	
	        for (RoleName roleName : RoleName.values()) {
	            roleRepository.findByRoleName(roleName)
	            .orElseGet(() -> roleRepository.save(new Role(null,roleName)));
	        }
	    };
	}
	
}
