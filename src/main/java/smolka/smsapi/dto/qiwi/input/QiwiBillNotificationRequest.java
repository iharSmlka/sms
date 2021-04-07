package smolka.smsapi.dto.qiwi.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import smolka.smsapi.dto.qiwi.QiwiBillNotificationBody;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QiwiBillNotificationRequest {
    private QiwiBillNotificationBody bill;
}
