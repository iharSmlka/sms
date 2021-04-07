package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BillStatus {
    ACTIVE(0), CLOSED(1), SUCCEED(2);

    Integer code;
}
