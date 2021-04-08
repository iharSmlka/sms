package smolka.smsapi.service.markupper;

import smolka.smsapi.dto.CostMapDto;

import java.math.BigDecimal;

public interface MarkUpperService {
    void changePercentage(Integer percentage);

    CostMapDto markUpCostMap(CostMapDto costMapDto);

    BigDecimal markUp(BigDecimal cost);
}
