package ae.roommaster.app.exception;

public class InvalidBookingException extends RuntimeException {

    public InvalidBookingException(String message) {
        super(message);
    }

}
