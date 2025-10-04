package cloud.praetoria.auth.utils;

public class Constantes {

	public static final String YPAREO_ID_SIZE = "Ypareo ID must be between 3 and 50 characters";
	public static final String YPAREO_LOGIN_SIZE = "Ypareo login must be between 3 and 50 characters";
	public static final String YPAREO_ID_REQUIRED = "Ypareo ID is required";
	public static final String YPAREO_LOGIN = "Ypareo login is required";
	public static final String PASSWORD_REQUIRED = "Password is required";
	public static final String PASSWORD_SIZE = "Password must be between 8 and 50 characters";
	
	public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";
	public static final String PASSWORD_REQUIREMENT = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character";
	public static final String PASSWORD_CONFIRMATION_REQUIRED = "Password confirmation is required";
	
	public static final String REFRESH_TOKEN_REQUIRED = "Refresh token is required";
}
