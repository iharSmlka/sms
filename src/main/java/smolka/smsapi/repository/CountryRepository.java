package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.Country;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    List<Country> findAll();

    Country findByCountryCode(String countryCode);
}
