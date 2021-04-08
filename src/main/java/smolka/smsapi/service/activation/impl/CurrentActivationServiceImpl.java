package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.ActivationMessageDto;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.CurrentActivationCreateInfoDto;
import smolka.smsapi.dto.CurrentActivationsStatusDto;
import smolka.smsapi.dto.input.GetActivationRequest;
import smolka.smsapi.dto.input.GetCostRequest;
import smolka.smsapi.dto.input.OrderRequest;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.exception.*;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.ActivationTargetRepository;
import smolka.smsapi.repository.CountryRepository;
import smolka.smsapi.repository.CurrentActivationRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.user.UserService;
import smolka.smsapi.service.receiver.ReceiversAdapter;
import smolka.smsapi.service.sync.BalanceSyncService;
import smolka.smsapi.utils.DateTimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrentActivationServiceImpl implements CurrentActivationService {

    @Value("${sms.api.minutes_for_activation:20}")
    private Integer minutesForActivation;
    @Autowired
    private CurrentActivationRepository currentActivationRepository;
    @Autowired
    private ActivationHistoryService activationHistoryService;
    @Autowired
    private ActivationTargetRepository activationTargetRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ReceiversAdapter receiversAdapter;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceSyncService balanceSyncService;


    @Override
    @Transactional(rollbackFor = { ReceiverException.class, UserNotFoundException.class, UserBalanceIsEmptyException.class, NoNumbersException.class })
    public CurrentActivationCreateInfoDto orderActivation(OrderRequest orderRequest) throws ReceiverException, UserNotFoundException, UserBalanceIsEmptyException, NoNumbersException {
        User user = userService.findUserByUserKey(orderRequest.getApiKey());
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        if (!balanceSyncService.orderIsPossible(user, orderRequest.getCost())) {
            throw new UserBalanceIsEmptyException("Не хватает денег");
        }
        ActivationTarget service = activationTargetRepository.findByServiceCode(orderRequest.getService());
        Country country = countryRepository.findByCountryCode(orderRequest.getCountry());
        ReceiverActivationInfoDto receiverActivationInfo = receiversAdapter.orderAttempt(country, service, orderRequest.getCost());
        CurrentActivation newActivation = mainMapper.createNewCurrentActivation(receiverActivationInfo, user, country, service, minutesForActivation);
        currentActivationRepository.save(newActivation);
        CurrentActivationCreateInfoDto activationInfo = mainMapper.mapActivationInfoFromActivation(newActivation);
        balanceSyncService.subFromRealBalanceAndAddToFreeze(user, receiverActivationInfo.getCost());
        return activationInfo;
    }

    @Override
    public ActivationMessageDto getCurrentActivationForUser(GetActivationRequest getActivationRequest) throws ActivationNotFoundException, UserNotFoundException {
        User user = userService.findUserByUserKey(getActivationRequest.getApiKey());
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        CurrentActivation activation = currentActivationRepository.findCurrentActivationByIdAndUser(getActivationRequest.getId(), user);
        if (activation == null) {
            throw new ActivationNotFoundException("Данной активации не существует");
        } else {
            return mainMapper.mapActivationMessageFromCurrentActivation(activation);
        }
    }

    @Override
    public CurrentActivationsStatusDto getCurrentActivationsForUser(String apiKey) throws UserNotFoundException {
        User user = userService.findUserByUserKey(apiKey);
        if (user == null) {
            throw new UserNotFoundException("This activation not exist");
        }
        List<CurrentActivation> activations = currentActivationRepository.findAllCurrentActivationsByUser(user);
        return mainMapper.mapActivationsStatusFromCurrentActivations(activations);
    }

    @Override
    public CostMapDto getCostsForActivations(GetCostRequest costRequest) throws ReceiverException, UserNotFoundException {
        User user = userService.findUserByUserKey(costRequest.getApiKey());
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        return receiversAdapter.getCommonCostMap(countryRepository.findByCountryCode(costRequest.getCountry()));
    }

    @Override
    @Transactional
    public void setMessageForCurrentActivation(CurrentActivation activation, String message) {
        activation.setMessage(message);
        activation.setStatus(ActivationStatus.SMS_RECEIVED.getCode());
        currentActivationRepository.save(activation);
    }

    @Override
    public List<CurrentActivation> findAllCurrentActivationsWithoutReceivedMessage() {
        return currentActivationRepository.findAllCurrentActivationsByStatus(ActivationStatus.ACTIVE.getCode());
    }

    @Override
    @Transactional
    public void closeCurrentActivationsForUser(User user, List<CurrentActivation> activationsForClose) {
        if (activationsForClose.isEmpty()) {
            return;
        }
        activationHistoryService.saveAllCurrentActivationsToHistoryWithStatus(activationsForClose, ActivationStatus.CLOSED);
        currentActivationRepository.deleteAll(activationsForClose);
        BigDecimal sum = activationsForClose.stream()
                .map(CurrentActivation::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceSyncService.subFromFreezeAndAddToRealBalance(user, sum);
    }

    @Override
    @Transactional
    public void succeedCurrentActivationsForUser(User user, List<CurrentActivation> activationsForSucceed) {
        if (activationsForSucceed.isEmpty()) {
            return;
        }
        activationHistoryService.saveAllCurrentActivationsToHistoryWithStatus(activationsForSucceed, ActivationStatus.SUCCEED);
        currentActivationRepository.deleteAll(activationsForSucceed);
        BigDecimal sum = activationsForSucceed.stream()
                .map(CurrentActivation::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceSyncService.subFromFreeze(user, sum);
    }

    @Override
    public Map<User, List<CurrentActivation>> findAllCurrentExpiredActivationsForUsers() {
        List<CurrentActivation> expiredActivations = currentActivationRepository.findAllCurrentActivationsByPlannedFinishDateLessThanEqual(DateTimeUtils.getUtcCurrentLocalDateTime());
        Map<User, List<CurrentActivation>> resultMap = new HashMap<>();
        for (CurrentActivation activation : expiredActivations) {
            User userForActivation = activation.getUser();
            resultMap.computeIfAbsent(userForActivation, k -> new ArrayList<>());
            resultMap.get(userForActivation).add(activation);
        }
        return resultMap;
    }
}
