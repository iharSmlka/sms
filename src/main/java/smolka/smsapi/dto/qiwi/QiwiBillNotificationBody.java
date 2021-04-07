package smolka.smsapi.dto.qiwi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiwiBillNotificationBody {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QiwiBillStatus {
        private String value;
    }

    private String siteId;
    private String billId;
    private QiwiAmountDto amount;
    private QiwiBillCustomerDto customer;
    private QiwiBillStatus status;
}
