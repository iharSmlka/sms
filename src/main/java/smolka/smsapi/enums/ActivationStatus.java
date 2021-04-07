package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivationStatus {
    ACTIVE(0), SMS_RECEIVED(1), SUCCEED(2), CLOSED(3);

    Integer code;
}
