package com.example.cms.config;

import java.util.Arrays;
import java.util.List;

import com.example.cms.entity.CityEntity;
import com.example.cms.entity.CountryEntity;
import com.example.cms.repository.CityRepository;
import com.example.cms.repository.CountryRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedMasterData(CountryRepository countryRepository, CityRepository cityRepository) {
        return args -> {
            CountryEntity sriLanka = ensureCountry(countryRepository, "Sri Lanka", "LK");
            CountryEntity india = ensureCountry(countryRepository, "India", "IN");
            CountryEntity usa = ensureCountry(countryRepository, "United States", "US");

            ensureCities(cityRepository, sriLanka, Arrays.asList("Colombo", "Kandy", "Galle", "Jaffna"));
            ensureCities(cityRepository, india, Arrays.asList("Chennai", "Mumbai", "Bangalore"));
            ensureCities(cityRepository, usa, Arrays.asList("New York", "Boston", "Seattle"));
        };
    }

    private CountryEntity ensureCountry(CountryRepository countryRepository, String name, String code) {
        return countryRepository.findByCodeIgnoreCase(code)
                .orElseGet(() -> countryRepository.save(new CountryEntity(name, code)));
    }

    private void ensureCities(CityRepository cityRepository, CountryEntity country, List<String> cityNames) {
        for (String cityName : cityNames) {
            if (!cityRepository.existsByCountryIdAndNameIgnoreCase(country.getId(), cityName)) {
                cityRepository.save(new CityEntity(cityName, country));
            }
        }
    }
}