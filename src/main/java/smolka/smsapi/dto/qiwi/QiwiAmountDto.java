package smolka.smsapi.dto.qiwi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QiwiAmountDto {
    private BigDecimal value;
    private String currency;

    public String formatValue() {
        return value.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
    }
}
