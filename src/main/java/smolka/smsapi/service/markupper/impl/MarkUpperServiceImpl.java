package smolka.smsapi.service.markupper.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.service.markupper.MarkUpperService;
import smolka.smsapi.service.parameters_service.ParametersService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class MarkUpperServiceImpl implements MarkUpperService {

    @Value(value = "${sms.api.bigdecimal_scaling}")
    private Integer scale;

    @Autowired
    private ParametersService parametersService;

    private Integer percentageCashedValue;

    @PostConstruct
    public void initialize() {
        percentageCashedValue = parametersService.getPercentageForMarkUpper();
    }

    @Override
    @Transactional
    public void changePercentage(Integer percentage) {
        percentageCashedValue = percentage;
        parametersService.savePercentageForMarkUpper(percentageCashedValue);
    }

    @Override
    public CostMapDto markUpCostMap(CostMapDto costMapDto) {
        Integer percentage = percentageCashedValue;
        if (percentage == 0) {
            return costMapDto;
        }
        CostMapDto newCostMapDto = new CostMapDto();
        for (String country : costMapDto.getCostMap().keySet()) {
            for (String srv : costMapDto.getCostMap().get(country).keySet()) {
                Map<BigDecimal, Integer> costs = costMapDto.getCostMap().get(country).get(srv);
                for (BigDecimal val : costs.keySet()) {
                    Integer tmpCount = costs.get(val);
                    newCostMapDto.put(country, srv, markUp(val), tmpCount);
                }
            }
        }
        return costMapDto;
    }

    @Override
    public BigDecimal markUp(BigDecimal cost) {
        Integer percentage = percentageCashedValue;
        if (percentage == 0) {
            return cost;
        }
        return cost.add(percentage(cost, percentage)).setScale(scale, RoundingMode.CEILING);
    }

    private BigDecimal percentage(BigDecimal base, Integer pct) {
        return base.multiply(new BigDecimal(pct)).divide(new BigDecimal(100), RoundingMode.CEILING).setScale(scale, RoundingMode.CEILING);
    }
}
