package smolka.smsapi.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class PaymentSystemException extends Exception {

    private String response;

    public PaymentSystemException(String message) {
        super(message);
    }

    public PaymentSystemException(String message, ResponseEntity<String> response) {
        super(message);
        if (response != null) {
            this.response = response.getBody();
        }
    }
}
