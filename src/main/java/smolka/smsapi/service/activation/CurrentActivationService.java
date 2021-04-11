package smolka.smsapi.service.activation;

import smolka.smsapi.dto.*;
import smolka.smsapi.dto.input.ChangeActivationStatusRequest;
import smolka.smsapi.dto.input.GetActivationRequest;
import smolka.smsapi.dto.input.GetCostRequest;
import smolka.smsapi.dto.input.OrderRequest;
import smolka.smsapi.exception.*;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;

import java.util.List;
import java.util.Map;

public interface CurrentActivationService {
    CurrentActivationCreateInfoDto orderActivation(OrderRequest orderRequest) throws Throwable;

    ActivationMessageDto getCurrentActivationForUser(GetActivationRequest getActivationRequest) throws ActivationNotFoundException, UserNotFoundException;

    CurrentActivationsStatusDto getCurrentActivationsForUser(String apiKey) throws UserNotFoundException;

    CostMapDto getCostsForActivations(GetCostRequest costRequest) throws ReceiverException, UserNotFoundException;

    void setMessageForCurrentActivation(CurrentActivation activation, String message) throws Throwable;

    List<CurrentActivation> findAllCurrentActivationsWithoutReceivedMessage();

    ChangedStatusDto setStatusForActivation(ChangeActivationStatusRequest changeActivationStatusRequest) throws Throwable;

    void closeCurrentActivationsForUser(User user, List<CurrentActivation> activationsForClose) throws Throwable;

    void succeedCurrentActivationsForUser(User user, List<CurrentActivation> activationsForSucceed) throws Throwable;

    Map<User, List<CurrentActivation>> findAllCurrentExpiredActivationsForUsers();
}
