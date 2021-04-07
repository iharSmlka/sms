package smolka.smsapi.service.receiver;

import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.exception.ReceiverException;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;

import java.util.List;

public interface RestReceiver {
    ReceiverActivationInfoDto orderActivation(Country country, ActivationTarget service) throws ReceiverException;

    List<ReceiverActivationStatusDto> getActivationsStatus() throws ReceiverException;

    ReceiverCostMapDto getCostMap(Country country) throws ReceiverException;
}
