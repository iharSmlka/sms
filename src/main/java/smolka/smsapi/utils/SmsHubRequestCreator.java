package smolka.smsapi.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;

@UtilityClass
public class SmsHubRequestCreator {

    private static final String ORDER_ACTION_NAME = "getNumber";
    private static final String COST_MAP_ACTION_NAME = "getPrices";
    private static final String API_KEY_FORM_DATA_PROP_NAME = "api_key";
    private static final String ACTION_FORM_DATA_PROP_NAME = "action";
    private static final String SERVICE_FORM_DATA_PROP_NAME = "service";
    private static final String OPERATOR_FORM_DATA_PROP_NAME = "operator";
    private static final String COUNTRY_FORM_DATA_PROP_NAME = "country";

    public static HttpEntity<MultiValueMap<String, String>> createOrderRequest(String apiKey, Country country, ActivationTarget service) {
        final String operator = "any";
        MultiValueMap<String, String> map = createStandardMultiValueMap(apiKey, ORDER_ACTION_NAME);
        map.add(SERVICE_FORM_DATA_PROP_NAME, service.getSmshubServiceCode());
        map.add(OPERATOR_FORM_DATA_PROP_NAME, operator);
        map.add(COUNTRY_FORM_DATA_PROP_NAME, country.getSmshubCountryCode());
        return new HttpEntity<>(map, createStandardHeaders());
    }

    public static HttpEntity<MultiValueMap<String, String>> createCostMapRequest(String apiKey, Country country) {
        MultiValueMap<String, String> map = createStandardMultiValueMap(apiKey, COST_MAP_ACTION_NAME);
        if (country != null) {
            map.add(COUNTRY_FORM_DATA_PROP_NAME, country.getSmshubCountryCode());
        }
        return new HttpEntity<>(map, createStandardHeaders());
    }

    public static HttpEntity<MultiValueMap<String, String>> createEmptyRequest(String apiKey, String action) {
        MultiValueMap<String, String> map = createStandardMultiValueMap(apiKey, action);
        return new HttpEntity<>(map, createStandardHeaders());
    }

    private static MultiValueMap<String, String> createStandardMultiValueMap(String apiKey, String action) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(API_KEY_FORM_DATA_PROP_NAME, apiKey);
        map.add(ACTION_FORM_DATA_PROP_NAME, action);
        return map;
    }

    private static HttpHeaders createStandardHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }
}
