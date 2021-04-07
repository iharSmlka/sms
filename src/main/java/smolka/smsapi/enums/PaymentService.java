package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentService {
    QIWI("QIWI");

    String value;
}
