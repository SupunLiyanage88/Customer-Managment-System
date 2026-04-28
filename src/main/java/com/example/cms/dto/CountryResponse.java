package com.example.cms.dto;

public class CountryResponse {

    private Long id;
    private String name;
    private String code;

    public CountryResponse() {
    }

    public CountryResponse(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}