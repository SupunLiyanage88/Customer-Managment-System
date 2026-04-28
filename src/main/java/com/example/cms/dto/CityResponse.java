package com.example.cms.dto;

public class CityResponse {

    private Long id;
    private String name;
    private CountryResponse country;

    public CityResponse() {
    }

    public CityResponse(Long id, String name, CountryResponse country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CountryResponse getCountry() {
        return country;
    }
}