package com.example.cms.service;

import java.util.List;

import com.example.cms.dto.CityResponse;
import com.example.cms.dto.CountryResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final MasterDataService masterDataService;

    public MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    @GetMapping("/countries")
    public List<CountryResponse> countries() {
        return masterDataService.getCountries();
    }

    @GetMapping("/cities")
    public List<CityResponse> cities(@RequestParam(required = false) Long countryId) {
        return masterDataService.getCities(countryId);
    }
}