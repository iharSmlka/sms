package smolka.smsapi.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.input.CustomPageableRequest;
import smolka.smsapi.dto.qiwi.QiwiBillCustomerDto;
import smolka.smsapi.dto.qiwi.input.QiwiAddBalanceRequest;
import smolka.smsapi.dto.qiwi.QiwiAmountDto;
import smolka.smsapi.dto.qiwi.QiwiCreateBillRequest;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.enums.BillStatus;
import smolka.smsapi.enums.PaymentService;
import smolka.smsapi.enums.SortDictionary;
import smolka.smsapi.model.*;
import smolka.smsapi.utils.DateTimeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MainMapper {

    private static final Integer PAGE_NUMBER_DEFAULT = 0;
    private static final Integer PAGE_SIZE_DEFAULT = 1000;
    private static final String RUS_RUB_CURRENCY_ID = "RUB";
    @Value(value = "${sms.api.bigdecimal_scaling}")
    private Integer scale;
    @Autowired
    private ModelMapper modelMapper;

    public <E, D> D mapping(E object, Class<D> clazz) {
        if (object == null) {
            return null;
        }
        return modelMapper.map(object, clazz);
    }

    public CostMapDto mapToInternalCostMap(ReceiverCostMapDto costMap) {
        CostMapDto internalCostMap = new CostMapDto();
        if (costMap == null || costMap.getCostMap().isEmpty()) {
            return internalCostMap;
        }
        for (Country country : costMap.getCostMap().keySet()) {
            for (ActivationTarget service : costMap.getCostMap().get(country).keySet()) {
                Map<BigDecimal, Integer> costs = costMap.getCostMap().get(country).get(service);
                for (BigDecimal cost : costs.keySet()) {
                    BigDecimal newCost = new BigDecimal(cost.toString());
                    newCost = newCost.setScale(scale, RoundingMode.CEILING);
                    internalCostMap.addCostToMapWithAdd(country, service, newCost, costs.get(cost));
                }
            }
        }
        return internalCostMap;
    }

    public CurrentActivationsStatusDto mapActivationsStatusFromCurrentActivations(List<CurrentActivation> currentActivations) {
        List<CurrentActivationStatusDto> activationStatusList = currentActivations.stream().map(this::mapActivationStatusFromActivation).collect(Collectors.toList());
        return new CurrentActivationsStatusDto(activationStatusList);
    }

    public CurrentActivationCreateInfoDto mapActivationInfoFromActivation(CurrentActivation activation) {
        CurrentActivationCreateInfoDto activationInfo = mapping(activation, CurrentActivationCreateInfoDto.class);
        activationInfo.setCountryCode(activation.getCountry().getCountryCode());
        activationInfo.setServiceCode(activation.getService().getServiceCode());
        activationInfo.setCreateDate(DateTimeUtils.toUtcZonedDateTime(activation.getCreateDate()));
        return activationInfo;
    }

    public ActivationMessageDto mapActivationMessageFromCurrentActivation(CurrentActivation activation) {
        return ActivationMessageDto.builder()
                .message(activation.getMessage())
                .number(activation.getNumber())
                .build();
    }

    public ActivationHistory mapActivationHistoryFromCurrentActivation(CurrentActivation currActivation) {
        return ActivationHistory.builder()
                .id(currActivation.getId())
                .user(currActivation.getUser())
                .number(currActivation.getNumber())
                .message(currActivation.getMessage())
                .country(currActivation.getCountry())
                .service(currActivation.getService())
                .source(currActivation.getSource())
                .createDate(currActivation.getCreateDate())
                .sourceId(currActivation.getSourceId())
                .status(currActivation.getStatus())
                .cost(currActivation.getCost())
                .build();
    }

    public ActivationHistoryDto mapActivationHistoryDtoFromActivationHistoryListAndCount(List<ActivationHistory> activationHistory, Pageable pageable, Long count) {
        List<ActivationHistoryElementDto> activationHistoryElements = activationHistory.stream()
                .map(a -> {
                    ActivationHistoryElementDto activationHistoryDto = mapping(a, ActivationHistoryElementDto.class);
                    activationHistoryDto.setCountryCode(a.getCountry().getCountryCode());
                    activationHistoryDto.setServiceCode(a.getService().getServiceCode());
                    activationHistoryDto.setCreateDate(DateTimeUtils.toUtcZonedDateTime(a.getCreateDate()));
                    activationHistoryDto.setFinishDate(DateTimeUtils.toUtcZonedDateTime(a.getFinishDate()));
                    return activationHistoryDto;
                })
                .collect(Collectors.toList());
        Page<ActivationHistoryElementDto> activationHistoryElementsDtoPage = new PageImpl<>(activationHistoryElements, pageable, count);
        return ActivationHistoryDto.builder()
                .activationHistory(activationHistoryElementsDtoPage)
                .build();
    }

    public CurrentActivation createNewCurrentActivation(ReceiverActivationInfoDto receiverActivationInfo,
                                                        User user,
                                                        Country country,
                                                        ActivationTarget service,
                                                        BigDecimal cost,
                                                        Integer minutesForActivation) {
        LocalDateTime createDate = DateTimeUtils.getUtcCurrentLocalDateTime();
        return CurrentActivation.builder()
                .user(user)
                .number(receiverActivationInfo.getNumber())
                .message(null)
                .country(country)
                .service(service)
                .source(receiverActivationInfo.getSource())
                .createDate(createDate)
                .plannedFinishDate(createDate.plusMinutes(minutesForActivation))
                .sourceId(receiverActivationInfo.getId())
                .status(ActivationStatus.ACTIVE.getCode())
                .cost(cost)
                .build();
    }

    public Pageable mapPageableFromCustomPageRequest(CustomPageableRequest customPageableRequest, SortDictionary sortDictionaryElem) {
        int pageNumb = customPageableRequest.getPage() == null ? PAGE_NUMBER_DEFAULT : customPageableRequest.getPage();
        int pageSize = customPageableRequest.getPageSize() == null ? PAGE_SIZE_DEFAULT : customPageableRequest.getPageSize();
        if (sortDictionaryElem != null) {
            Sort sort = Sort.by(sortDictionaryElem.getDirection(), sortDictionaryElem.getAttrName());
            return PageRequest.of(pageNumb, pageSize, sort);
        }
        return PageRequest.of(pageNumb, pageSize);
    }

    public CommonReceiversActivationInfoMap getCommonReceiversActivationInfoMapFromReceiversActivationsList(List<ReceiverActivationStatusDto> receiverActivationStatusList) {
        CommonReceiversActivationInfoMap receiverActivationInfoMap = new CommonReceiversActivationInfoMap();
        for (ReceiverActivationStatusDto receiverActivation : receiverActivationStatusList) {
            receiverActivationInfoMap.addActivationInfo(receiverActivation);
        }
        return receiverActivationInfoMap;
    }

    public Bill mapQiwiBillFromAddBalanceRequestAndUser(QiwiAddBalanceRequest qiwiAddBalanceRequest, User user, Integer billLifeMinutes) {
        LocalDateTime currentDateTime = DateTimeUtils.getUtcCurrentLocalDateTime();
        return Bill.builder()
                .amount(qiwiAddBalanceRequest.getAmount())
                .createDate(currentDateTime)
                .user(user)
                .id(UUID.randomUUID().toString())
                .isPaid(false)
                .service(PaymentService.QIWI.getValue())
                .plannedCloseDate(currentDateTime.plusMinutes(billLifeMinutes))
                .closeDate(null)
                .build();
    }

    public QiwiCreateBillRequest mapQiwiBillRequestFromEntity(Bill bill, int expMinutes) {
        return QiwiCreateBillRequest.builder()
                .amount(QiwiAmountDto.builder()
                        .currency(RUS_RUB_CURRENCY_ID)
                        .value(bill.getAmount())
                        .build())
                .customer(QiwiBillCustomerDto.builder().account(bill.getUser().getUserId().toString()).build())
                .expirationDateTime(DateTimeUtils.toUtcZonedDateTime(bill.getCreateDate().plusMinutes(expMinutes)))
                .build();
    }

    private CurrentActivationStatusDto mapActivationStatusFromActivation(CurrentActivation activation) {
        CurrentActivationStatusDto activationStatus = mapping(activation, CurrentActivationStatusDto.class);
        activationStatus.setCountryCode(activation.getCountry().getCountryCode());
        activationStatus.setServiceCode(activation.getService().getServiceCode());
        activationStatus.setCreateDate(DateTimeUtils.toUtcZonedDateTime(activation.getCreateDate()));
        return activationStatus;
    }
}
