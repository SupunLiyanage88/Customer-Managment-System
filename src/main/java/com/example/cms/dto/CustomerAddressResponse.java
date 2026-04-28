package com.example.cms.dto;

public class CustomerAddressResponse {

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private CityResponse city;
    private CountryResponse country;

    public CustomerAddressResponse() {
    }

    public CustomerAddressResponse(Long id, String addressLine1, String addressLine2, CityResponse city,
                                   CountryResponse country) {
        this.id = id;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public CityResponse getCity() {
        return city;
    }

    public CountryResponse getCountry() {
        return country;
    }
}