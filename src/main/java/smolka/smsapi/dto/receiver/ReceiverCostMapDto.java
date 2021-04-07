package smolka.smsapi.dto.receiver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import smolka.smsapi.enums.SourceList;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ReceiverCostMapDto {

    private final Map<Country, Map<ActivationTarget, Map<BigDecimal, Integer>>> costMap = new HashMap<>();
    private SourceList source;

    public void addCostToMap(Country country, ActivationTarget service, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(country, c -> new HashMap<>());
        costMap.get(country).computeIfAbsent(service, k -> new HashMap<>());
        costMap.get(country).get(service).put(cost, count);
    }

    public boolean isExists(Country country, ActivationTarget activationTarget, BigDecimal cost) {
        if (costMap.get(country) == null || costMap.get(country).get(activationTarget) == null) {
            return false;
        }
        Map<BigDecimal, Integer> costs = costMap.get(country).get(activationTarget);
        for (BigDecimal costInMap : costs.keySet()) {
            int compareResult = costInMap.compareTo(cost);
            if (compareResult == 0 || compareResult < 0) {
                return true;
            }
        }
        return false;
    }
}
