package smolka.smsapi.service.receiver.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.exception.NoNumbersException;
import smolka.smsapi.exception.ReceiverException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;
import smolka.smsapi.service.markupper.MarkUpperService;
import smolka.smsapi.service.receiver.ReceiversAdapter;
import smolka.smsapi.service.receiver.RestReceiver;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReceiversAdapterImpl implements ReceiversAdapter {

    @Autowired
    private RestReceiver smsHubReceiver;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private MarkUpperService markUpperService;

    @Override
    public CostMapDto getCommonCostMap(Country country) throws ReceiverException {
        return markUpperService.markUp(mainMapper.mapToInternalCostMap(smsHubReceiver.getCostMap(country))); // пока что тут будет только получение инфы с СМС-хаба, т.к другие сервисы пока интегрировать рано
    }

    @Override
    public ReceiverActivationInfoDto orderAttempt(Country country, ActivationTarget service, BigDecimal cost) throws ReceiverException, NoNumbersException {
        return attemptToSmsHub(country, service, cost);
    }

    @Override
    public CommonReceiversActivationInfoMap getReceiversCurrentActivations() throws ReceiverException {
        List<ReceiverActivationStatusDto> receiverActivationStatusList = smsHubReceiver.getActivationsStatus();
        return mainMapper.getCommonReceiversActivationInfoMapFromReceiversActivationsList(receiverActivationStatusList);
    }

    private ReceiverActivationInfoDto attemptToSmsHub(Country country, ActivationTarget service, BigDecimal cost) throws ReceiverException, NoNumbersException {
        ReceiverCostMapDto costMap = getCostMapFromSmsHub(country);
        if (!costMap.isExists(country, service, cost)) {
            throw new NoNumbersException("Нет доступных номеров");
        }
        return smsHubReceiver.orderActivation(country, service);
    }

    private ReceiverCostMapDto getCostMapFromSmsHub(Country country) throws ReceiverException {
        return smsHubReceiver.getCostMap(country);
    }
}
