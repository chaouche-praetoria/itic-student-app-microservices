package cloud.praetoria.auth.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String details;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public ApiException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

}
