package smolka.smsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostMapDto {
    private final Map<String, Map<String, Map<BigDecimal, Integer>>> costMap = new HashMap<>();

    public BigDecimal getMinCost(String country, String service, BigDecimal cost) {
        if (costMap.get(country) == null || costMap.get(country).get(service) == null) {
            return null;
        }
        Map<BigDecimal, Integer> costs = costMap.get(country).get(service);
        return costs.keySet().stream().filter(c -> c.compareTo(cost) <= 0).min(BigDecimal::compareTo).orElse(null);
    }

    public void put(String country, String service, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(country, k -> new HashMap<>());
        costMap.get(country).computeIfAbsent(service, k -> new HashMap<>());
        costMap.get(country).get(service).put(cost, count);
    }

    public void addCostToMapWithAdd(Country country, ActivationTarget service, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(country.getCountryCode(), c -> new HashMap<>());
        costMap.get(country.getCountryCode()).computeIfAbsent(service.getServiceCode(), k -> new HashMap<>());
        Map<BigDecimal, Integer> costs = costMap.get(country.getCountryCode()).get(service.getServiceCode());
        costs.putIfAbsent(cost, 0);
        costs.put(cost, costs.get(cost) + count);
    }
}
