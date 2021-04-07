package smolka.smsapi.service.markupper;

import smolka.smsapi.dto.CostMapDto;

public interface MarkUpperService {
    void changePercentage(Integer percentage);

    CostMapDto markUp(CostMapDto costMapDto);
}
