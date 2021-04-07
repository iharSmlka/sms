package smolka.smsapi.service.activation;

import smolka.smsapi.dto.ActivationMessageDto;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.CurrentActivationCreateInfoDto;
import smolka.smsapi.dto.CurrentActivationsStatusDto;
import smolka.smsapi.dto.input.GetActivationRequest;
import smolka.smsapi.dto.input.GetCostRequest;
import smolka.smsapi.dto.input.OrderRequest;
import smolka.smsapi.exception.*;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;

import java.util.List;
import java.util.Map;

public interface CurrentActivationService {
    CurrentActivationCreateInfoDto orderActivation(OrderRequest orderRequest) throws ReceiverException, UserNotFoundException, UserBalanceIsEmptyException, NoNumbersException;

    ActivationMessageDto getCurrentActivationForUser(GetActivationRequest getActivationRequest) throws ActivationNotFoundException, UserNotFoundException;

    CurrentActivationsStatusDto getCurrentActivationsForUser(String apiKey) throws UserNotFoundException;

    CostMapDto getCostsForActivations(GetCostRequest costRequest) throws ReceiverException, UserNotFoundException;

    void setMessageForCurrentActivation(CurrentActivation activation, String message);

    List<CurrentActivation> findAllCurrentActivationsWithoutReceivedMessage();

    void closeCurrentActivationsForUser(User user, List<CurrentActivation> activationsForClose);

    void succeedCurrentActivationsForUser(User user, List<CurrentActivation> activationsForSucceed);

    Map<User, List<CurrentActivation>> findAllCurrentExpiredActivationsForUsers();
}
