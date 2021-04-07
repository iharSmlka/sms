package smolka.smsapi.enums.smshub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smolka.smsapi.enums.ReceiverErrorDictionary;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum SmsHubErrorResponseDictionary {
    SMSHUB_NO_KEY("NO_KEY", ReceiverErrorDictionary.WRONG_KEY),
    SMSHUB_BAD_KEY("BAD_KEY", ReceiverErrorDictionary.WRONG_KEY),
    SMSHUB_ERROR_SQL("ERROR_SQL", ReceiverErrorDictionary.UNKNOWN),
    SMSHUB_BAD_ACTION("BAD_ACTION", ReceiverErrorDictionary.BAD_REQUEST),
    SMSHUB_NO_NUMBERS("NO_NUMBERS", ReceiverErrorDictionary.NO_NUMBER),
    SMSHUB_NO_BALANCE("NO_BALANCE", ReceiverErrorDictionary.NO_BALANCE),
    SMSHUB_WRONG_SERVICE("WRONG_SERVICE", ReceiverErrorDictionary.WRONG_SERVICE),
    SMSHUB_BAD_SERVICE("BAD_SERVICE", ReceiverErrorDictionary.WRONG_SERVICE),
    SMSHUB_NO_ACTIVATION("NO_ACTIVATION", ReceiverErrorDictionary.NO_ACTIVATION);

    private final String smsHubErrorMsg;
    private final ReceiverErrorDictionary error;

    public static boolean isError(String response) {
        SmsHubErrorResponseDictionary dictResponse = Arrays.stream(values())
                .filter(e -> e.getSmsHubErrorMsg().equals(response))
                .findFirst()
                .orElse(null);
        return dictResponse != null;
    }

    public static ReceiverErrorDictionary getError(String response) {
        return Arrays.stream(values())
                .filter(e -> e.getSmsHubErrorMsg().equals(response))
                .findFirst()
                .map(e -> e.error)
                .orElse(ReceiverErrorDictionary.UNKNOWN);
    }
}
