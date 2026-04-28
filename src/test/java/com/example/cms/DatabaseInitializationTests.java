package com.example.cms;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cms.entity.CountryEntity;
import com.example.cms.repository.CityRepository;
import com.example.cms.repository.CountryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatabaseInitializationTests {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void schemaAndSeedDataLoadOnStartup() {
        assertThat(countryRepository.count()).isEqualTo(3L);

        CountryEntity sriLanka = countryRepository.findByCodeIgnoreCase("LK").orElseThrow();
        assertThat(cityRepository.findAllByCountryIdOrderByNameAsc(sriLanka.getId()))
                .extracting("name")
                .containsExactly("Colombo", "Galle", "Jaffna", "Kandy");
        assertThat(cityRepository.existsByCountryIdAndNameIgnoreCase(sriLanka.getId(), "Colombo")).isTrue();
    }
}