package smolka.smsapi.exception;

public class UserBalanceIsEmptyException extends Exception {

    public UserBalanceIsEmptyException(String message) {
        super(message);
    }
}
