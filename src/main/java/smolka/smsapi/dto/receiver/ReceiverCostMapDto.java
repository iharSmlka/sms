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

    public BigDecimal getMinCost(Country country, ActivationTarget activationTarget, BigDecimal cost) {
        if (costMap.get(country) == null || costMap.get(country).get(activationTarget) == null) {
            return null;
        }
        Map<BigDecimal, Integer> costs = costMap.get(country).get(activationTarget);
        return costs.keySet().stream().filter(c -> c.compareTo(cost) <= 0).min(BigDecimal::compareTo).orElse(null);
    }
}
