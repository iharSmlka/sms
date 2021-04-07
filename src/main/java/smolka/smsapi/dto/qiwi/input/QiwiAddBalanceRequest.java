package smolka.smsapi.dto.qiwi.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QiwiAddBalanceRequest {
    @NotNull
    private String apiKey;
    @NotNull
    @Digits(integer = 6, fraction = 2)
    private BigDecimal amount;
}
