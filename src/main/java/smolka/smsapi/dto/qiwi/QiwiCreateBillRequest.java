package smolka.smsapi.dto.qiwi;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QiwiCreateBillRequest {
    private String billId;
    private QiwiAmountDto amount;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmZ", timezone = "UTC")
    private ZonedDateTime expirationDateTime;
    private String account;
}
