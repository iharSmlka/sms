package smolka.smsapi.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import smolka.smsapi.enums.ReceiverErrorDictionary;
import smolka.smsapi.enums.SourceList;
import smolka.smsapi.enums.smshub.SmsHubErrorResponseDictionary;

@Getter
public class ReceiverException extends Exception {
    private final String responseFromReceiver;
    private final ReceiverErrorDictionary receiverErrorElement;

    public ReceiverException(String msg, ResponseEntity<String> responseEntityFromReceiver, SourceList source) {
        super(msg);
        this.responseFromReceiver = responseEntityFromReceiver == null ? "" : responseEntityFromReceiver.getBody();
        this.receiverErrorElement = getIdentifiedErrorByResponseAndSource(responseFromReceiver, source);
    }

    private ReceiverErrorDictionary getIdentifiedErrorByResponseAndSource(String responseFromReceiver, SourceList source) {
        switch (source) {
            case SMSHUB: {
                return SmsHubErrorResponseDictionary.getError(responseFromReceiver);
            }
            default: {
                return null;
            }
        }
    }
}
