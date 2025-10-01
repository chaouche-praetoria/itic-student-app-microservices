package cloud.praetoria.auth.utils;

import java.util.Optional;

import cloud.praetoria.auth.enums.RoleName;

public class AuthUtils {
	
	public static Optional<RoleName> matchByName(String name) {
	    switch (name.trim().toUpperCase()) {
	        case "APPRENANT":  return Optional.of(RoleName.STUDENT);
	        case "PERSONNEL":  return Optional.of(RoleName.TRAINER);
	        default:
	            return Optional.empty();
	    }
	}


}
