package com.example.cms.service;

import java.util.List;

import com.example.cms.dto.CityResponse;
import com.example.cms.dto.CountryResponse;
import com.example.cms.entity.CityEntity;
import com.example.cms.entity.CountryEntity;
import com.example.cms.repository.CityRepository;
import com.example.cms.repository.CountryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MasterDataService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public MasterDataService(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<CountryResponse> getCountries() {
        List<CountryEntity> countries = countryRepository.findAllByOrderByNameAsc();
        List<CountryResponse> responses = new java.util.ArrayList<CountryResponse>();
        for (CountryEntity country : countries) {
            responses.add(new CountryResponse(country.getId(), country.getName(), country.getCode()));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<CityResponse> getCities(Long countryId) {
        List<CityEntity> cities = countryId == null
                ? cityRepository.findAllByOrderByNameAsc()
                : cityRepository.findAllByCountryIdOrderByNameAsc(countryId);
        List<CityResponse> responses = new java.util.ArrayList<CityResponse>();
        for (CityEntity city : cities) {
            CountryEntity country = city.getCountry();
            responses.add(new CityResponse(city.getId(), city.getName(), new CountryResponse(country.getId(), country.getName(), country.getCode())));
        }
        return responses;
    }
}