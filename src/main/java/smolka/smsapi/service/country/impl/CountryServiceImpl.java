package smolka.smsapi.service.country.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.model.Country;
import smolka.smsapi.repository.CountryRepository;
import smolka.smsapi.service.country.CountryService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;
    private final Map<String, Country> countryMap = new HashMap<>();

    @PostConstruct
    private void init() {
        List<Country> countries = countryRepository.findAll();
        countries.forEach(c -> {
            countryMap.put(c.getSmshubCountryCode(), c);
        });
    }

    @Override
    public Country getCountryBySmsHubCode(String smsHubCode) {
        return countryMap.get(smsHubCode);
    }
}
