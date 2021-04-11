package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.ActivationHistoryDto;
import smolka.smsapi.dto.input.GetActivationHistoryRequest;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.enums.SortDictionary;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.ActivationHistoryRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.user.UserService;
import smolka.smsapi.utils.DateTimeUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationHistoryServiceImpl implements ActivationHistoryService {

    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ActivationHistoryRepository activationHistoryRepository;
    @Autowired
    private UserService userService;

    @Override
    public ActivationHistoryDto getActivationHistorySortedByFinishDateDesc(GetActivationHistoryRequest getActivationHistoryRequest) throws UserNotFoundException {
        User user = userService.findUserByUserKey(getActivationHistoryRequest.getApiKey());
        if (user == null) {
            throw new UserNotFoundException("Данный юзер не найден");
        }
        Pageable pageableForActivationHistoryWithSortFinishDateAsc = mainMapper.mapPageableFromCustomPageRequest(getActivationHistoryRequest, SortDictionary.ACTIVATION_HISTORY_SORT_FINISH_DATE_DESC);
        Page<ActivationHistory> activationHistory = null;
        if (getActivationHistoryRequest.getStartDateBefore() == null && getActivationHistoryRequest.getStartDateAfter() == null) {
            activationHistory = activationHistoryRepository.findAllByUser(pageableForActivationHistoryWithSortFinishDateAsc, user);
        }
        if (getActivationHistoryRequest.getStartDateBefore() == null) {
            activationHistory = activationHistoryRepository.findAllByUserAndCreateDateGreaterThanEqual(user, getActivationHistoryRequest.getStartDateAfter().toLocalDateTime(), pageableForActivationHistoryWithSortFinishDateAsc);
        }
        if (getActivationHistoryRequest.getStartDateAfter() == null) {
            activationHistory = activationHistoryRepository.findAllByUserAndCreateDateLessThanEqual(user, getActivationHistoryRequest.getStartDateBefore().toLocalDateTime(), pageableForActivationHistoryWithSortFinishDateAsc);
        }
        if (getActivationHistoryRequest.getStartDateAfter() != null && getActivationHistoryRequest.getStartDateBefore() != null) {
            activationHistory = activationHistoryRepository.findAllByUserAndCreateDateGreaterThanEqualAndCreateDateLessThanEqual(user, getActivationHistoryRequest.getStartDateAfter().toLocalDateTime(), getActivationHistoryRequest.getStartDateBefore().toLocalDateTime(), pageableForActivationHistoryWithSortFinishDateAsc);
        }
        return mainMapper.mapActivationHistoryDtoFromActivationHistoryListAndCount(activationHistory.toList(), pageableForActivationHistoryWithSortFinishDateAsc, activationHistoryRepository.countByUser(user));
    }

    @Override
    @Transactional
    public ActivationHistory saveCurrentActivationToHistoryWithStatus(CurrentActivation currentActivation, ActivationStatus status) {
        ActivationHistory activationHistory = mainMapper.mapActivationHistoryFromCurrentActivation(currentActivation);
        activationHistory.setStatus(status.getCode());
        activationHistory.setFinishDate(DateTimeUtils.getUtcCurrentLocalDateTime());
        activationHistory = activationHistoryRepository.save(activationHistory);
        return activationHistory;
    }

    @Override
    public ActivationHistory findActivationHistoryById(Long id) {
        return activationHistoryRepository.findById(id).orElse(null);
    }
}
