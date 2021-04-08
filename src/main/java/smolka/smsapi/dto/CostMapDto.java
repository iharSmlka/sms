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

    public boolean isExists(Country country, ActivationTarget service, BigDecimal cost) {
        if (costMap.get(country.getCountryCode()) == null || costMap.get(country.getCountryCode()).get(service.getServiceCode()) == null) {
            return false;
        }
        Map<BigDecimal, Integer> costs = costMap.get(country.getCountryCode()).get(service.getServiceCode());
        for (BigDecimal costInMap : costs.keySet()) {
            int compareResult = costInMap.compareTo(cost);
            if (compareResult == 0 || compareResult < 0) {
                return true;
            }
        }
        return false;
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
