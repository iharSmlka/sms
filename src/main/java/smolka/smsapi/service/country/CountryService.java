package smolka.smsapi.service.country;

import smolka.smsapi.model.Country;

public interface CountryService {
    Country getCountryBySmsHubCode(String smsHubCode);
}
