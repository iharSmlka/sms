package smolka.smsapi.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.enums.SourceList;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;
import smolka.smsapi.service.activation_target.ActivationTargetService;
import smolka.smsapi.service.country.CountryService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsHubMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ActivationTargetService activationTargetService;
    @Autowired
    private CountryService countryService;

    public List<ReceiverActivationStatusDto> mapActivationsStatusResponse(Map<String, Object> root) {
        List<Map<String, Object>> arrayActivations = (List<Map<String, Object>>) root.get("array");
        List<ReceiverActivationStatusDto> activationStatusList = new ArrayList<>();
        for (Map<String, Object> element : arrayActivations) {
            final Long id = Long.parseLong(element.get("id").toString());
            ActivationStatus status = ActivationStatus.ACTIVE;
            int statusCode = Integer.parseInt(element.get("status").toString());
            switch (statusCode) {
                case 6: {
                    status = ActivationStatus.SMS_RECEIVED;
                }
            }
            final String code = element.get("code") != null ? element.get("code").toString() : null;
            ReceiverActivationStatusDto activationStatus = new ReceiverActivationStatusDto(id, status, code, SourceList.SMSHUB);
            activationStatusList.add(activationStatus);
        }
        return activationStatusList;
    }

    public Map<String, Object> getMapActivationsStatus(String response) throws JsonProcessingException {
        return objectMapper.readValue(response, HashMap.class);
    }

    public ReceiverActivationInfoDto extractActivationInfoFromResponse(String response) {
        final String activationNumber = response.substring(response.lastIndexOf(":") + 1);
        final Long activationId = Long.parseLong(response.substring(response.indexOf(":") + 1, response.lastIndexOf(":")));
        return new ReceiverActivationInfoDto(activationId, activationNumber, SourceList.SMSHUB, null);
    }

    public ReceiverCostMapDto mapCostMapForSmsHubJson(String response) throws JsonProcessingException {
        Map<String, Map<String, Map<String, Integer>>> rootJson = objectMapper.readValue(response, HashMap.class);
        ReceiverCostMapDto costMap = new ReceiverCostMapDto();
        costMap.setSource(SourceList.SMSHUB);
        for (String countryInJson : rootJson.keySet()) {
            Map<String, Map<String, Integer>> serviceSegment = rootJson.get(countryInJson);
            Country countryEntity = countryService.getCountryBySmsHubCode(countryInJson);
            if (countryEntity == null) {
                continue;
            }
            for (String serviceInRootJson : serviceSegment.keySet()) {
                ActivationTarget serviceEntity = activationTargetService.getTargetBySmsHubCode(serviceInRootJson);
                if (serviceEntity == null) {
                    continue;
                }
                Map<String, Integer> costCountMapForService = serviceSegment.get(serviceInRootJson);
                for (String cost : costCountMapForService.keySet()) {
                    costMap.addCostToMap(countryEntity, serviceEntity, new BigDecimal(cost), costCountMapForService.get(cost));
                }
            }
        }
        return costMap;
    }
}
