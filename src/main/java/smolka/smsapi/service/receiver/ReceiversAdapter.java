package smolka.smsapi.service.receiver;

import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.exception.NoNumbersException;
import smolka.smsapi.exception.ReceiverException;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;

import java.math.BigDecimal;

public interface ReceiversAdapter {
    CostMapDto getCommonCostMap(Country country) throws ReceiverException;

    CommonReceiversActivationInfoMap getReceiversCurrentActivations() throws ReceiverException;

    ReceiverActivationInfoDto orderAttempt(Country country, ActivationTarget service, BigDecimal cost) throws ReceiverException, NoNumbersException;
}
