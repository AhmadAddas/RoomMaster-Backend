package ae.roommaster.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final String details;

    public CustomException(String message, HttpStatus status, String details) {
        super(message);
        this.status = status;
        this.details = details;
    }

}
